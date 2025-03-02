<?php 
// Database connection parameters
$host = "localhost";
$username = "root";
$password = "";
$database = "performee-3";
// Connect to the database
$conn = mysqli_connect($host, $username, $password, $database);
// Check if the connection was successful
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
?>