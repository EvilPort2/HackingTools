<?php

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

if (empty($_GET['smsId'])) {
	
	error('Wrong SMS template id');
} else {
	
	$smsId = (int) $_GET['smsId'];
	$sql = 'SELECT COUNT(sms_templates.id) AS cnt FROM sms_templates WHERE userId = ' . $_SESSION['userid'] . ' AND id = ' . $smsId;
	$query = mysqli_query($connection, $sql);
	$result = $query->fetch_assoc();
	$cnt = $result['cnt'];
	
	if (!$cnt) {
		
		error('Wrong SMS template id');
	}
}

$sql = 'DELETE FROM sms_templates WHERE userId = ? AND id = ?';

$stmt = mysqli_stmt_init($connection);
mysqli_stmt_prepare($stmt, $sql);
mysqli_stmt_bind_param($stmt, 'ii', $_SESSION['userid'], $smsId);
mysqli_stmt_execute($stmt);
mysqli_stmt_store_result($stmt);

if (!$stmt) {

	error('Could not delete SMS template');
}

// save notification
$Comment = "SMS template deleted, id = " . $smsId;
$Type = "sms";
$userID = $_SESSION['userid'];
$stmt = mysqli_stmt_init($connection);
mysqli_stmt_prepare($stmt, 'INSERT INTO notifications (Comment, Time, Type, UserID) VALUES (?,(now() - interval 4 hour),?,?)');
mysqli_stmt_bind_param($stmt, 'ssi',$Comment,$Type,$userID);
mysqli_stmt_execute($stmt);
mysqli_stmt_store_result($stmt);
$_SESSION['message'] = "<div class='alert alert-success alert-dismissable'><button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>SMS Deleted</div>";

header('Location:  ../../dagah_vSMSes.html');