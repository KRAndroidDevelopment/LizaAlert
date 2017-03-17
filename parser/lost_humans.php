<?
header("Cache-control: private, no-cache, no-store");
header("Content-Type: text/xml; charset=utf-8");

$cache_file = dirname(__FILE__) . '/lost_humans.xml';
$time = time();
$mtime = @filemtime($cache_file);
if($time - $mtime > 60 * 5){
	touch($cache_file, $time, $time);

	require_once 'config.php';
	require_once 'mysql/mysql_storage.php';

	$storage = new mysql_storage(DB_HOST, DB_USER, DB_PASS, DB_NAME);
	$storage->names('utf8');
	$table = $storage->table('lost_humans');
	$q = $table->select(0, 0, 1, 0, "id desc");
	$r = $q->row();
	$xml = '<'.'?xml version="1.0" encoding="utf-8"?'.'>';
	$mtime = date('d.m.Y H:i:s', $time);
	$xml .= <<<EOS
<root><last_update>$mtime</last_update><entry><id>{$r->by_name('id')}</id><src_url>{$r->by_name('src_url')}</src_url><photo_url>{$r->by_name('photo_url')}</photo_url><date>{$r->by_name('date')}</date><description>{$r->by_name('description')}</description></entry></root>
EOS;
	file_put_contents($cache_file, $xml);
}
echo file_get_contents($cache_file);
