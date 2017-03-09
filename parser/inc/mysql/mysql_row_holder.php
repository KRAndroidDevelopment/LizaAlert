<?

if(!class_exists("mysql_row_holder")):

class mysql_row_holder{
	public $query_holder, $db_conn;
	protected $row, $index;

	function __construct(&$row, &$query_holder){
		$this->row = &$row;
		$this->query_holder = &$query_holder;
		$this->reset();
	}

	function __destruct(){
		unset($this->query_holder);
		unset($this->row);
	}

	function count(){
		return count($this->row);
	}

	function by_name($name){
		$index = $this->query_holder->get_index($name);
		return $this->by_index($index);
	}

	function by_index($index){
		if(array_key_exists($index, $this->row)){
			return $this->row[$index];
		}
		throw new Exception("Invalid Field Index: '$index'\n" . var_export($this->row, TRUE));
	}

	function reset(){
		$this->index = 0;
	}

	function each(){
		if($this->index >= $this->count()) return FALSE;
		$value = $this->row[$this->index];
		$key = $this->field_name($this->index);
		$this->index++;
		return array($key, $value);
	}

	function field_name($index){
		return $this->query_holder->fields[$index]->name;
	}

	function hash(){
		$this->reset();
		$hash = array();
		while($pair = $this->each()){
			list($k, $v) = $pair;
			$hash[$k] = $v;
		}
		return $hash;
	}

	function to_a(){
		return $this->hash();
	}

	function __get($field_name_or_index){
		if(preg_match('#^_?(\d+)$#', $field_name_or_index, $m)){
			return $this->by_index((int)$m[1]);
		}
		return $this->by_name($field_name_or_index);
	}

#### XZ METHODS BELOW

	function lengths(){
		trigger_error("OBSOLETE METHOD", E_USER_ERROR);
		if(($res = mysql_fetch_lengths($this->query_holder->res)) === FALSE){
			throw new mysql_exception($this->query_holder->db_conn_obj->db_conn);
		}
		return $res;
	}

	function get_field_type($index){
		trigger_error("OBSOLETE METHOD", E_USER_ERROR);
		mysql_field_flags($this->query_holder->res, $index);
	}

	function get(){
		trigger_error("OBSOLETE METHOD", E_USER_ERROR);
		return mysql_fetch_object($this->res);
	}

	function get_hash($type = MYSQL_ASSOC){
		trigger_error("OBSOLETE METHOD", E_USER_ERROR);
		return mysql_fetch_array($this->res, $type);
	}

	function get_fields(){
		trigger_error("OBSOLETE METHOD", E_USER_ERROR);
		return mysql_fetch_assoc($this->res);
	}

	function fields(){
		trigger_error("OBSOLETE METHOD", E_USER_ERROR);
		$fields = array();
		for($i = 0; $i < mysql_num_fields($this->res); $i++){
			$fields[] = mysql_field_name($this->res, $i);
		}
		return $fields;
	}

	function get_row_obj(){
		trigger_error("OBSOLETE METHOD", E_USER_ERROR);
		return mysql_fetch_object($this->res);
	}

	function get_insert_id(){
		trigger_error("OBSOLETE METHOD", E_USER_ERROR);
		return mysql_insert_id($this->db_conn_obj->db_conn);
	}


}

endif;
