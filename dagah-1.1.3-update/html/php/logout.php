<?php
    require_once 'db_creds.php';
    session_start();
    $connection = mysqli_connect($db_hostname, $db_username, $db_password, $db_database);
    // check connection
    if (mysqli_connect_errno()) {
        $message = "Failed to connect to MySQL: " . mysqli_connect_error();
    }
    $Comment = "Logout " . $_SESSION['username'];
    $userID = $_SESSION['userid'];
    $Type = "logout";
    $userID = $_SESSION['userid'];
    $stmt_not = mysqli_stmt_init($connection);
    mysqli_stmt_prepare($stmt_not, 'INSERT INTO notifications (Comment, Time, Type, UserID) VALUES (?,(now() - interval 4 hour),?,?)');
    mysqli_stmt_bind_param($stmt_not, 'ssi',$Comment,$Type,$userID);
    mysqli_stmt_execute($stmt_not);
    mysqli_stmt_store_result($stmt_not);

    // destroy
    $_SESSION = array();
    setcookie(session_name(), '', time()-2592000, '/');
    session_destroy();
    header('Location:  ../login.html');
?>
