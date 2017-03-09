<?

if(!class_exists("mysql_table")):

require_once dirname(realpath(__FILE__)) . '/mysql_transaction.php';

class mysql_table{
	var $storage;
	var $table;

	function __construct(&$storage, $table, $cache = false){
		$this->storage = &$storage;
		$this->table = $table;
		$this->cache = $cache;
		$this->cached_queries = array();
	}

	function &insert(&$params){ #IS MUST BE HASH
		list($fields, $values) = $this->storage->prepare_insert( $params );
		$query = sprintf("INSERT INTO `%s` (%s) VALUES(%s)", $this->table, join(",", $fields), join(",", $values));
		$qr = $this->query($query);
		return $qr;
	}

#	function &select($fields = 0, $condition = 0, $limit = 0, $offset = 0, $order = 0){
	function escape($value){
		return $this->storage->escape($value);
	}

	function &select($fields_list = 0, $conditions = 0, $limit = 0, $offset = 0, $order = 0){
		if($fields_list){
			if(is_array($fields_list)){
				$ff = array();
				foreach($fields_list as $field){
					$ff[] = "`" . $this->storage->escape($field) . "`";
				}
				$fields_list = join(',', $ff);
			}
		}else{
			$fields_list = '*';
		}
		$query = sprintf("SELECT %s FROM `%s`", $fields_list, $this->table);
		if($conditions){
			if(is_array($conditions)){
				$pairs = array();
				foreach($conditions as $k => $v){
					if(is_array($v)){
					}else{
						$v = $conditions[$k];
					}
					$pairs[] = sprintf("`%s` = '%s'", $this->escape($k), $this->escape($v));
#					$pairs[] = sprintf("`%s` = '%s'", mysql_real_escape_string($k), mysql_real_escape_string($v));
				}
				if(count($pairs)) $query .= " WHERE " . join(' AND ', $pairs);
			}else{
				$query .= " WHERE $conditions";
			}
		}
		if($order){
			$query .= " ORDER BY $order";
		}
		if($limit){
			$query .= " LIMIT $offset, $limit";
		}

		if($this->cache){
			if(isset($this->cached_queries[$query])){
				$result = &$this->cached_queries[$query];
				$result->reset();
				return $this->cached_queries[$query];
			}
			$result = &$this->query($query);
			$this->cached_queries[$query] = &$result;
			return $result;
		}
		return $this->query($query);
#		$qr = $this->storage->select($this->table, $fields, $condition, $limit, $offset, $order);
#		return $qr;
	}

	function &update($condition, $params, $limit = 0, $order = 0){
		if(is_array($condition)){ #IS MUST BE HASH
			$pairs = $this->storage->prepare_update($condition);
			$where = join(' AND ', $pairs);
		}else{
			$where = $condition;
		}
		if(is_array($params)){
			$pairs = $this->storage->prepare_update( $params );
			$update_exp = join(",", $pairs);
		}else{
			$update_exp = $params;
		}
		$query = sprintf("UPDATE `%s` SET %s WHERE %s", $this->table, $update_exp, $where);
		if($order){
			$query .= " ORDER BY $order";
		}
		if($limit){
			$query .= " LIMIT $limit";
		}
		$qr = $this->query($query);
		return $qr;
	}

	function &delete($condition){
		if(is_array($condition)){ #IS MUST BE HASH
			$pairs = $this->storage->prepare_update($condition);
			$where = join(' AND ', $pairs);
		}else{
			$args = func_get_args();
			$where = $this->storage->prepare_query($args);
		}
		$query = sprintf("DELETE FROM `%s` WHERE %s", $this->table, $where);
		$qr = $this->storage->query($query);
		return $qr;
	}

	function &replace(&$params){ #IS MUST BE HASH
		list($fields, $values) = $this->storage->prepare_insert( $params );
		$query = sprintf("REPLACE INTO `%s` (%s) VALUES(%s)", $this->table, join(",", $fields), join(",", $values));
		$qr = $this->storage->query($query);
		return $qr;
	}

	function &query($query){
		$qr = $this->storage->query($query);
		return $qr;
	}

	function &truncate(){
		$qr = $this->storage->query(sprintf("TRUNCATE `%s`", $this->table));
		return $qr;
	}

	function &transaction(){
		$transaction = new mysql_transaction($this->storage);
		return $transaction;
	}

}

endif;
