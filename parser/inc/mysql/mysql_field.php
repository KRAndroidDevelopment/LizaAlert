<?

if(!class_exists("mysql_field")):

class mysql_field{
	public $row;

	function __construct(&$row){
		$this->row = &$row;
	}

	function __destruct(){
		unset($this->row);
	}

	function length(){
		mysql_field_len();
	}

	function type(){
		mysql_field_len();
	}

	function table(){
		mysql_field_len();
	}

	function name(){
		mysql_field_len();
	}

}

endif;
