<?

function parse_htm($htm, $url){
	$data = array(
		'src_url' => $url
	);
	if(preg_match('#<title>(.*)</title>#', $htm, $m)){
		$data['title'] = preg_replace('#\s+&bull;.*#', '', $m[1]);
	}
	if(preg_match('#<div id="p\d+(.*?)<hr#ms', $htm, $m)){
		preg_match('#<p class="author">.*&raquo;(.*?)</p>#', $m[1], $p);
		
		$data['date'] = trim($p[1]);

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

$f = '14159.htm';
$f = '14161.htm';
#$h = file_get_contents('http://lizaalert.org/forum/viewtopic.php?t=14159');file_put_contents($f, $h);
$h = file_get_contents($f);
$d = parse_htm($h, '');
var_dump($d);