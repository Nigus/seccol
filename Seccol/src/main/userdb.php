<?php
	if (!empty($_SERVER['HTTPS']) && ('on' == $_SERVER['HTTPS'])) {
		$uri = 'https://callman.cloudapp.net';
	} else {
		$uri = 'http://securecall.cloudapp.net';
	}
	$uri .= $_SERVER['HTTP_HOST'];
	header('Location: '.$uri.'/var/www/android_api_login');
	exit;
?>
