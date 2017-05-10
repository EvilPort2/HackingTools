<?php

require_once 'db_creds.php';

function detect_bom_encoding($str) {
    if ($str[0] == chr(0xEF) && $str[1] == chr(0xBB) && $str[2] == chr(0xBF)) {
        return 'UTF-8';
    }
    else if ($str[0] == chr(0x00) && $str[1] == chr(0x00) && $str[2] == chr(0xFE) && $str[3] == chr(0xFF)) {
        return 'UTF-32BE';
    }
    else if ($str[0] == chr(0xFF) && $str[1] == chr(0xFE)) {
        if ($str[2] == chr(0x00) && $str[3] == chr(0x00)) {
            return 'UTF-32LE';
        }
        return 'UTF-16LE';
    }
    else if ($str[0] == chr(0xFE) && $str[1] == chr(0xFF)) {
        return 'UTF-16BE';
    }
}

$connection_upload = mysqli_connect($db_hostname, $db_username, $db_password, $db_database);
session_start();
date_default_timezone_set('UTC');
$target_list = "";
$fail = false;
// Check the user input
$characterpattern = '/[^A-Za-z0-9 \/\_.\-\\\\]/';
if (preg_match($characterpattern, $_POST['phoneGroup'])) {
	// Someone mucking with PhoneGroup
	$fail = true;
	$_SESSION['message'] = "<div class='alert alert-danger'<button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>Error! Phone Group contains illegal characters</div>";
	exit;
}
if (isset($_FILES['fileToUpload'])) {

	$useFile = true;
} elseif (!empty($_POST['phoneGroupArea'])) {

	$useFile = false;
} else {

	$fail = true;
	$_SESSION['message'] = "<div class='alert alert-danger'<button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>Please, choose file or enter phone numbers in text area</div>";
	header('Location:  ../dagah_vPhone.html');
}
// don't procerss - no text file found
if ($useFile && $_FILES["fileToUpload"]["type"] != "text/plain") {

	$fail = true;
	$_SESSION['message'] = "<div class='alert alert-danger alert-dismissable'><button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>Error! Not a text file.</div>";
	header('Location:  ../dagah_vPhone.html');
} elseif (!$useFile && !trim($_POST['phoneGroupArea'])) {

	$fail = true;
	$_SESSION['message'] = "<div class='alert alert-danger alert-dismissable'><button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>Error! No phone numbers in text area.</div>";
	header('Location:  ../dagah_vPhone.html');
} else {

	// process text
	$count = 0;
	if ($useFile) {

		//$file = fopen($_FILES['fileToUpload']['tmp_name'], 'r');
		$numbers = file($_FILES['fileToUpload']['tmp_name']);

	} else {

		$numbers = explode("\n", $_POST['phoneGroupArea']);
	}

	if (!is_array($numbers)) {

		$fail = true;
		$_SESSION['message'] = "<div class='alert alert-danger alert-dismissable'><button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>Error! Wrong input data format</div>";
		header('Location:  ../dagah_vPhone.html');
	}

	$userID = $_SESSION['userid'];
	foreach ($numbers as $number) {

		// replace all illegal characters
		$characterpattern = '/[^A-Za-z0-9@\_\-\., ]/';
		$number = trim(preg_replace($characterpattern, '', $number));
		if (!$number) {
			
			continue;
		}
		
		++ $count;
		// all good -- process file
		//$number = fgets($file);
		$phoneGroup_upload = $_POST['phoneGroup'];
		$target_array = explode(",", $number);
		if (count($target_array) < 2) {

			$fail = true;
			$_SESSION['message'] = "<div class='alert alert-danger alert-dismissable'><button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>Error! Input data line " . $count . " is wrong.</div>";
			break;
		}
		$number_upload = trim($target_array[0]);
		$number_label = trim($target_array[1]);
		
		$stmt_upload = mysqli_stmt_init($connection_upload);
		mysqli_stmt_prepare($stmt_upload, 'INSERT INTO phonebook (Name, Number, PhoneGroup, UserID) VALUES (?, ?, ?, ?)');
		mysqli_stmt_bind_param($stmt_upload,'sisi', $number_label, $number_upload, $phoneGroup_upload, $userID);
		mysqli_stmt_execute($stmt_upload);
		mysqli_stmt_store_result($stmt_upload);

		$target_list .= $number_upload . ", " . $number_label . "\r\n"; 
	}
	fclose($file);

	if (!$fail) {
	   // copy phone output to target directory with PhoneGroup name

		$path = $_SESSION['dagah_dir'] . $_SESSION['target_dir'] . $_POST['phoneGroup'];

		if (!file_put_contents($path, $target_list)) {

			$fail = true;
			$_SESSION['message'] = "<div class='alert alert-danger alert-dismissable'><button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>Error! Input file could not be copied to " . $path . ". </div>";
		}
	}

	if (!$fail) {
		// save notification
		$Comment = "Phone Uploaded " . $_POST['phoneGroup'];
		$Type = "phone";
		$userID = $_SESSION['userid'];
		$stmt_not = mysqli_stmt_init($connection_upload);
		mysqli_stmt_prepare($stmt_not, 'INSERT INTO notifications (Comment, Time, Type, UserID) VALUES (?,(now() - interval 4 hour),?,?)');
		mysqli_stmt_bind_param($stmt_not, 'ssi',$Comment,$Type,$userID);
		mysqli_stmt_execute($stmt_not);
		mysqli_stmt_store_result($stmt_not);
		$_SESSION['message'] = "<div class='alert alert-success alert-dismissable'><button aria-hidden='true' data-dismiss='alert' class='close' type='button'>�</button>Added New Numbers</div>";
	}

	header('Location:  ../dagah_vPhone.html');
}