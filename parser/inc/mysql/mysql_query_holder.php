<?

if(!class_exists("mysql_query_holder")):

require_once dirname(realpath(__FILE__)) . '/mysql_exception.php';
require_once dirname(realpath(__FILE__)) . '/mysql_row_holder.php';

class mysql_query_holder{
	public $res, $query, $db_conn_obj, $fields, $fields_names, $start_time, $end_time, $exec_time;

	function __construct($query, &$db_conn_obj){
		list($this->query, $this->db_conn_obj) = array($query, &$db_conn_obj);
		$this->fields = FALSE;
		$db_conn_obj->ping();
		$this->start_time = microtime(TRUE);
		$this->is_select = preg_match('#^\s*SELECT#i', $query);
		if(($this->res = mysql_query($query, $db_conn_obj->db_conn)) == FALSE){
			throw new mysql_exception($this->db_conn_obj->db_conn, "QUERY: $query\n");
		}
		$this->end_time = microtime(TRUE);
		$this->exec_time = $this->end_time - $this->start_time;
		$this->is_res = (gettype($this->res) == "resource");
	}

	function __destruct(){
		if($this->is_res){
			if(!mysql_free_result($this->res)){
				throw new mysql_exception($this->res);
//				throw new Exception(mysql_error());
			}
			$this->res = FALSE;
		}
		$this->query = 0;
#		unset($this->query);
		$this->db_conn_obj = 0;
#		unset($this->db_conn_obj);
	}

	function row($hash = FALSE){
		if($this->is_res){
			if($row = mysql_fetch_row($this->res)){
				$this->get_fields_info($row);
				$r = new mysql_row_holder($row, $this);
				if($hash){
					return $r->hash();
				}       	
				return $r;
			}
		}
		return FALSE;
	}

	function rows(){
		$rows = array();
		while($row = $this->row()){
			$rows[] = $row;
		}
		return $rows;
	}

	function hash_rows(){
		$rows = array();
		while($row = $this->row()){
			$rows[] = $row->hash();
		}
		return $rows;
	}

	function get_index($field_name){
		if(array_key_exists($field_name, $this->fields_names)){
			$index = $this->fields_names[$field_name];
			return $index;
		}
		throw new Exception("Invalid Field Name: '$field_name'\nQUERY: " . $this->query);
	}

	function get_fields_info(&$row){
		if(!$this->fields){
			$this->fields = array();
			$this->fields_names = array();
			foreach($row as $index => $value){
				$this->fields[$index] = FALSE;
				if($field = mysql_fetch_field($this->res, $index)){
					$this->fields[$index] = $field;
					$this->fields_names[$field->name] = $index;
				}else{
					throw new mysql_exception($this->db_conn_obj->db_conn);
				}
			}
		}
		return $this->fields;
	}

	function processed(){
		return $this->db_conn_obj->processed();
	}

	function count(){
		if($this->is_res){
			if($this->is_select){
				return mysql_num_rows($this->res);
			}
			return mysql_affected_rows($this->res);
		}
		return $this->res;
	}

	function get_insert_id(){
		return mysql_insert_id($this->db_conn_obj->db_conn);
	}

	function current(){
	}

	function each($hash = FALSE){
		return $this->row($hash);
	}

	function next(){
	}

	function prev(){
	}

	function reset(){
	}

## METHODS BELOW ARE OBSOLETE !!!

	function get_all(){
		$rows = array();
		while($row = $this->get_row()){
			$rows[] = $row;
		}
		return $rows;
	}

	function get_all_as_hash(){
		$rows = array();
		while($row = $this->get_hash()){
			$rows[] = $row;
		}
		return $rows;
	}

	function get_all_as_map(){
		$rows = array();
		while($row = $this->get_row()){
			list($k, $v) = $row;
			$rows[$k] = $v;
		}
		return $rows;
	}

	function get_value(){
		if(($row = $this->get_row()) === FALSE){
			return FALSE;
		}
		list($v) = $row;
		return $v;
	}

	function get_row(){
		return mysql_fetch_row($this->res);
	}

	function get_obj(){
		return mysql_fetch_object($this->res);
	}

	function get_hash($type = MYSQL_ASSOC){
		return mysql_fetch_array($this->res, $type);
	}

	function get_fields(){
		return mysql_fetch_assoc($this->res);
	}

	function fields(){
		$fields = array();
		for($i = 0; $i < mysql_num_fields($this->res); $i++){
			$fields[] = mysql_field_name($this->res, $i);
		}
		return $fields;
	}

	function get_row_obj(){
		return mysql_fetch_object($this->res);
	}

}

endif;
