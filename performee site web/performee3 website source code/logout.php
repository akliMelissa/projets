<?php
session_start(); // Start the session

// Unset all of the session variables
$_SESSION = array();

// Destroy the session
session_destroy();

// Delete the session cookie
setcookie(session_name(), '', time()-3600, '/');

// Redirect to the index.php page
header("Location: index.php");
exit;
?>