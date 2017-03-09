<?

require_once 'config.php';
require_once 'mysql/mysql_storage.php';

function scan($id){
	global $storage, $table;
	for($i = 0; $i < 10; $i++){
		$url = 'http://lizaalert.org/forum/viewtopic.php?t=' . ($i + $id);
		$htm = file_get_contents($url);
		if(stripos($htm, 'title>Информация</')){
		}else{
			if(stripos($htm, '</html>')){
				if($data = parse_htm($htm, $url)){
					try{
						$table->insert($data);
					}catch(Exception $e){
						echo $e->getMessage();
					}
				}
			}
		}
		sleep(1);
	}
}

function parse_date($s){
$month_repl = array(
	'янв' => 'jan',
	'фев' => 'feb',
	'мар' => 'mar',
	'апр' => 'apr',
	'май' => 'may',
	'июн' => 'jun',
	'июл' => 'jul',
	'авг' => 'aug',
	'сен' => 'sep',
	'окт' => 'oct',
	'ноя' => 'nov',
	'дек' => 'dec',
);
	$from = array_keys($month_repl);
	$to = array_values($month_repl);
	return date('d.m.Y H:i', strtotime(str_replace($from, $to, $s)));
}


function parse_htm($htm, $url){
	$data = array(
		'src_url' => $url
	);
	if(preg_match('#(\d+)$#', $url, $m)){
		$data['id'] = $m[1];
	}
	if(preg_match('#<title>(.*)</title>#', $htm, $m)){
		$data['title'] = preg_replace('#\s+&bull;.*#', '', $m[1]);
		if(!preg_match('#Пропал#', $data['title'])){
			return 0;
		}
	}
	if(preg_match('#<div id="p\d+(.*?)<hr#ms', $htm, $m)){
		preg_match('#<p class="author">.*&raquo;(.*?)</p>#', $m[1], $p);
		
		$data['date'] = parse_date(trim($p[1]));

		preg_match('#<div class="content">(.*?)</div#ms', $m[1], $d);
		$desc = $d[1];
		preg_match_all('#<img[^>]+#', $m[1], $m);
		foreach($m[0] as $i){
			preg_match('#src="([^"]+)#i', $i, $u);
			$u = $u[1];
			if(preg_match('#^https?://#i', $u)){
				$data['photo_url'] = $u;
			}else{
				if(preg_match('#^.(/download/file.php.id=\d+)#i', $u, $m)){
					$data['photo_url'] = 'http://lizaalert.org/forum' . $m[1];
				}
			}
		}

		$desc = preg_replace('#<br\s*/?>#', "\n", $desc);
		$desc = preg_replace('#<a[^>]+>.*?</a>#', ' ', $desc);
		$data['description'] = trim(preg_replace('#<[^>]+>#', ' ', $desc));
	}
	return $data;
}

#$f = '14159.htm';
#$f = '14161.htm';
#$h = file_get_contents('http://lizaalert.org/forum/viewtopic.php?t=14159');file_put_contents($f, $h);
#$h = file_get_contents($f);
#$d = parse_htm($h, '');
#var_dump($d);

#scan($id);
$storage = new mysql_storage(DB_HOST, DB_USER, DB_PASS, DB_NAME);
$storage->names('utf8');
$table = $storage->table('lost_humans');
$q = $storage->query("select max(id) as 'cnt' from lost_humans");
$r = $q->row();
$id = $r->by_name('cnt');
if(!$id){
	$id = 14170;
}
scan($id);
