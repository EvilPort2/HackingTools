<?php
//echo 'post>>><br><pre>';print_r($_POST);print_r($_FILES);die;
require_once '../db_creds.php';
$connection = mysqli_connect($db_hostname, $db_username, $db_password, $db_database);
session_start();
date_default_timezone_set('UTC');
$target_list = "";
$fail = false;
// Check if sush SMS temoplate exists
$cnt = 0;

function error($text) {
	
	$fail = true;
	$_SESSION['message'] = "<div class='alert alert-danger alert-dismissable'><button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>Error! $text.</div>";
	header('Location:  ../../dagah_vSMSes.html');
	exit;
}

if (!empty($_POST['smsId'])) {

//  $sql = 'SELECT COUNT(sms_templates.id) AS cnt FROM sms_templates WHERE userId = ' . $_SESSION['userid'] . ' AND id = ?';
//	$stmt = mysqli_stmt_init($connection);
//  mysqli_stmt_prepare($stmt, $sql);
//	mysqli_stmt_bind_param($stmt, 'i', $smsId);
//	mysqli_stmt_execute($stmt);
//	mysqli_stmt_store_result($stmt);
	
	$smsId = (int) $_POST['smsId'];
	$sql = 'SELECT COUNT(sms_templates.id) AS cnt FROM sms_templates WHERE userId = ' . $_SESSION['userid'] . ' AND id = ' . $smsId;
	$query = mysqli_query($connection, $sql);
	$result = $query->fetch_assoc();
	$cnt = $result['cnt'];
	
	if (!$cnt) {
		
		error('Wrong SMS template id');
	}
	
	$isNew = false;
} else {
	
	$isNew = true;
}
// Check integrity of post
$characterpattern = '/[^A-Za-z0-9\,\!\_ \.\-\\\\]/';
if (empty($_POST['smsText'])) {
	
	error('Empty SMS template text');
} else if (preg_match($characterpattern, $_POST['smsText'])) {
        // Someone is mucking with SMS
        error('SMS Text contains unallowed characters. Limit to alphanumeric, spaces, and comma, exclaimation points, underscore, dash, or backslash');


} else {
	
	$smsText = $_POST['smsText'];
}

$sql = ($isNew ? 'INSERT INTO' : 'UPDATE') . ' sms_templates SET text = ?, userId = ' . $_SESSION['userid'] . ($isNew ? '' : ' WHERE id = ' . $smsId);
//echo '<pre>';print_r($_POST);echo $sql;die;
$stmt = mysqli_stmt_init($connection);
mysqli_stmt_prepare($stmt, $sql);
mysqli_stmt_bind_param($stmt, 's', $smsText);
mysqli_stmt_execute($stmt);
mysqli_stmt_store_result($stmt);



if (!$stmt) {

	error('Could not save SMS template');
}

if ($isNew) {
	
	$id = mysqli_insert_id($connection);
} else {
	
	$id = $smsId;
}

//echo '<pre>___';print_r([$smsId, $id]);die;

// save notification
$Comment = "SMS template " . ($isNew ? 'added' : 'updated') . ", id = " . $id;
$Type = "sms";
$userID = $_SESSION['userid'];
$stmt = mysqli_stmt_init($connection);
mysqli_stmt_prepare($stmt, 'INSERT INTO notifications (Comment, Time, Type, UserID) VALUES (?,(now() - interval 4 hour),?,?)');
mysqli_stmt_bind_param($stmt, 'ssi',$Comment,$Type,$userID);
mysqli_stmt_execute($stmt);
mysqli_stmt_store_result($stmt);
$_SESSION['message'] = "<div class='alert alert-success alert-dismissable'><button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>SMS " . ($isNew ? 'Added' : 'Updated') . "</div>";

header('Location:  ../../dagah_vSMSes.html');
