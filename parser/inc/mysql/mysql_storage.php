<?

if(!class_exists("mysql_storage")):

require_once dirname(realpath(__FILE__)) . '/mysql_exception.php';
require_once dirname(realpath(__FILE__)) . '/mysql_connection.php';
require_once dirname(realpath(__FILE__)) . '/mysql_query_holder.php';
require_once dirname(realpath(__FILE__)) . '/mysql_table.php';

class mysql_storage{
	protected $db_conn_obj, $logger;

	function __construct($host, $login, $password, $db = FALSE){
		list($this->host, $this->login, $this->password, $this->db) = array($host, $login, $password, $db);
		$this->db_conn_obj = new mysql_connection($this->host, $this->login, $this->password);
		$this->connect();
		$this->debug = 0;
	}

	function __destruct(){
		unset($this->db_conn_obj);
	}

	function logger(&$logger){
		$this->logger = &$logger;
	}

	function debug($debug_level){
		$this->debug = $debug_level;
	}

	function prepare_query($template){
		$args = is_array($template) ? $template : func_get_args();
		$template = array_shift($args);
		$_args = array();
		foreach($args as $arg){
			$_args[] = $this->escape($arg);
		}
		return vsprintf($template, $_args);
	}

	function prepare_insert($info_hash){
		$fields = array();
		$values = array();
		foreach($info_hash as $k => $v){
			$fields[]= sprintf("`%s`", $k);
			$values[]= sprintf("'%s'", $this->escape($v) );
		}
		return array($fields, $values);
	}

	function prepare_update($info_hash){
		$pairs = array();
		foreach($info_hash as $k => $v){
			$pairs[] = sprintf("`%s` = '%s'", $k, $this->escape($v) );
		}
		return $pairs;
	}

	function escape($v){
		return $this->db_conn_obj->escape($v);
	}

	function connect(){
		$this->db_conn_obj->ping();
		if($this->db != FALSE){
			if(mysql_select_db($this->db, $this->db_conn_obj->db_conn) == FALSE){
				throw new mysql_exception($this->db_conn_obj->db_conn);
			}
		}
#		if(function_exists("mysql_set_charset")){
#			mysql_set_charset('utf-8', $this->db_conn_obj->db_conn);
#		}
		#    }else{
#		mysql_query("SET NAMES 'cp1251';");
#		mysql_query("SET NAMES 'utf-8';");
		#    }
	}

	function charset($charset){
		$this->query($this->prepare_query("SET CHARACTER SET '%s';", $charset));
	}

	function names($charset){
		$this->query($this->prepare_query("SET NAMES '%s';", $charset));
	}

	static function date2mysql($time){
		return gmdate('Y-m-d H:i:s', $time);
	}

	static function mysql2date($time){
		if(!$time || $time == '0000-00-00 00:00:00'){
			return FALSE;
		}
		if(preg_match('#^(\d+)-(\d+)-(\d+)\s+(\d+):(\d+):(\d+)$#', $time, $m)){
#			return gmmktime($m[4], $m[5], $m[6], $m[2], $m[3], $m[1], 0);
			return gmmktime($m[4], $m[5], $m[6], $m[2], $m[3], $m[1]);#2015.09.09 Bug fix for php 5.1+
		}
		return gmdate('Y-m-d H:i:s', $time);
	}

	function &query($query){
		if($this->debug){
			$this->logger->out_ts( "$query\n" );
			if($this->debug > 1){
				foreach(debug_backtrace() as $Line){
					$this->logger->out( serialize($Line) . "\n" );
				}
			}
			$this->logger->out( "\n" );
		}
		$this->ping();
		$qr = new mysql_query_holder($query, $this->db_conn_obj);
		return $qr;
	}

	function ping(){
		$this->db_conn_obj->ping();
#		if(mysql_ping($this->db_conn_obj->db_conn) === FALSE){
#			$this->connect();
#		}
	}

	function get_query_value($query){
		$res = $this->query( $query );
		$row = $res->get_row();
		return $row[0];
	}

	function &table($table_name, $cache = false){
		$table = new mysql_table($this, $table_name);
		return $table;
	}

	function & __get($table_name){
		$table = new mysql_table($this, $table_name);
		return $table;
	}

	function select($table, $fields_list = 0, $conditions = 0, $limit = 0, $offset = 0, $order = 0){
		$_table = $this->table($table);
		return $_table->select($fields_list, $conditions, $limit, $offset, $order);
	}

	function info(){
		return mysql_info($this->db_conn_obj->db_conn);
	}

}

endif;
