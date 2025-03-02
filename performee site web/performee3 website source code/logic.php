<?php

$conn = mysqli_connect("localhost", "root", "", "Performee");
error_reporting(E_ALL);
ini_set('display_errors', 1);
$default_background = file_get_contents("default_profile_pic.png");

if (!$conn) {
    echo "Connection not established";
}


// Check if user is authenticated
if(!empty( $_SESSION['user_id'])){
$user_id = $_SESSION['user_id'];
}



$sql = "SELECT * FROM job";
$res = mysqli_query($conn, $sql);
if (isset($_POST["new_post"])) {
    if(empty($_POST["title"]) || empty($_POST["date"]) || empty($_POST["location"]) || empty($_POST["roles"]) || empty($_POST["gender"])){
        header("Location:addjob.php?info=missing");
        exit();
    } else {
        // Get form data
        $title = $_POST["title"];
        $descrip = $_POST["description"];
        $location = $_POST["location"];
        $date = $_POST["date"];
        $deadline = $_POST["deadline"];
        $gender = $_POST["gender"];
        $role = $_POST["roles"];
        $experience = $_POST["experience"];
        $age = $_POST["age"];
        
        // Check for file upload errors
        if ($_FILES['img']['error'] !== UPLOAD_ERR_OK) {
            switch($_FILES['img']['error']) {
                case UPLOAD_ERR_INI_SIZE:
                case UPLOAD_ERR_FORM_SIZE:
                    $error = 'File too large';
                    break;
                case UPLOAD_ERR_PARTIAL:
                    $error = 'File upload incomplete';
                    break;
                case UPLOAD_ERR_NO_FILE:
                    $error = 'No file uploaded';
                    break;
                case UPLOAD_ERR_NO_TMP_DIR:
                case UPLOAD_ERR_CANT_WRITE:
                case UPLOAD_ERR_EXTENSION:
                    $error = 'Server error';
                    break;
                default:
                    $error = 'Unknown error';
                    break;
            }
            header("Location:addjob.php?info=" . $error);
            exit();
        }
        
        // Get the image data
        $image = $_FILES['img']['tmp_name'];
        $imgData = file_get_contents($image);
        $uploadDir = 'uploads/';
        $imageFile = $uploadDir . basename($_FILES['img']['name']); 
        move_uploaded_file($_FILES['img']['tmp_name'], $imageFile);
        
        // Prepare the SQL query
        $sql_query = "INSERT INTO job(title, user_id, description, date, deadline, location, role, gender, age, experience, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        $stmt = mysqli_prepare($conn, $sql_query);
        mysqli_stmt_bind_param($stmt, "sisssssssss", $title, $user_id, $descrip, $date, $deadline, $location, $role, $gender, $age, $experience, $imageFile);
        
        // Execute the prepared statement
        if (mysqli_stmt_execute($stmt)) {
            header("Location: Announcement.php?info=added");
            exit();
        } else {
            echo "Error: " . mysqli_error($conn);
        }
        
        // Close the prepared statement
        mysqli_stmt_close($stmt);
    }
}
    

if (isset($_GET["job_id"])) {
    $job_id = $_GET["job_id"];
    $q = "SELECT * FROM job WHERE job_id=$job_id";
    $result = mysqli_query($conn, $q);
    
}
