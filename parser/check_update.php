<?

require_once 'config.php';
require_once 'mysql/mysql_storage.php';

$storage = new mysql_storage(DB_HOST, DB_USER, DB_PASS, DB_NAME);
$table = $storage->table('lost_humans');
$q = $storage->query("select max(id) as 'cnt' from lost_humans");
$r = $q->row();
$id = $r->by_name('cnt');
if(!$id){
	$id = 14170;
}
echo $id;
