<?

if(!class_exists("mysql_exception")):

class mysql_exception extends Exception{
	function __construct($resource = 0, $message = ''){
		list($usec, $sec) = explode(" ", microtime());
		$ts = date("d.m.Y H:i:s", $sec) . substr(sprintf("%.4f", $usec), 1);
		if($resource){
			$message .= mysql_error($resource);
			$code = mysql_errno($resource);
		}else{
			$message .= mysql_error();
			$code = mysql_errno();
		}
		parent::__construct("$ts $message", $code);
	}
}

endif;
