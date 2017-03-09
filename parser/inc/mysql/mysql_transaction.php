<?

if(!class_exists("mysql_transaction")):

class mysql_transaction{
	var $storage;
	var $transaction;
	var $auto_commit = false;

	function __construct(&$storage){
		$this->storage = &$storage;
		$this->transaction = false;
	}

	function __destruct(){
		if($this->auto_commit){
			$this->commit();
		}else{
			$this->rollback();
		}
		$this->transaction = false;
#		$this->storage = 0;
	}

	function begin(){
		$this->start();
	}

	function start(){
		$qr = $this->storage->query("BEGIN");
		$this->transaction = true;
	}

	function commit(){
		if($this->transaction){
			$qr = $this->storage->query("COMMIT");
			$this->transaction = false;
		}
	}

	function rollback(){
		if($this->transaction){
			$qr = $this->storage->query("ROLLBACK");
			$this->transaction = false;
		}
	}

}

endif;
