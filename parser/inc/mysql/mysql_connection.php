<?

if(!class_exists("mysql_connection")):

require_once dirname(realpath(__FILE__)) . '/mysql_exception.php';

class mysql_connection{
	public $db_conn;
	public $host, $login, $password;

	function __construct($host, $login, $password){
		$this->stated = microtime(true);
		list($this->host, $this->login, $this->password) = array($host, $login, $password);
		$this->connect();
	}

	function connect(){
		if(($this->db_conn = mysql_connect($this->host, $this->login, $this->password, TRUE)) == FALSE){
			throw new mysql_exception($this->db_conn);
		}
	}

	function __destruct(){
		$this->finished = microtime(true);
		if( mysql_close($this->db_conn) === FALSE){
			throw new mysql_exception($this->db_conn);
		}
		$this->db_conn = FALSE;
	}

	function ping(){
		if(mysql_ping($this->db_conn) === FALSE){
			$this->connect();
		}
	}

	function escape($value){
		if(($v = mysql_real_escape_string($value, $this->db_conn)) === FALSE){
			throw new mysql_exception($this->db_conn);
		}
		return $v;
	}

	function processed(){
		return mysql_affected_rows($this->db_conn);
	}

}

endif;
