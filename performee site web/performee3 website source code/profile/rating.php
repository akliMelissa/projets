<?php


session_start();

if (!isset($_GET['id'])) {
    // Redirect to the home page or an error page if the user ID is missing
    header("Location:users.php");
    exit();
}
$rater_id=$_SESSION['user_id'];
// Get the user ID from the GET parameter
$user_id = $_GET['id'];

// Retrieve the user data from the database
include("../bd_connect.php");

$sql = "SELECT * FROM ratings WHERE user_id = ?";
$stmt = mysqli_prepare($conn, $sql);
mysqli_stmt_bind_param($stmt, "i", $user_id);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

$row=mysqli_fetch_assoc($result);
$num_rating=$row['num_ratings'];


$sql = "SELECT * FROM users WHERE id = ?";
$stmt = mysqli_prepare($conn, $sql);
mysqli_stmt_bind_param($stmt, "i", $user_id);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

if (mysqli_num_rows($result) > 0) {
    $user = mysqli_fetch_assoc($result);
} else {
    // Redirect to an error page if the user with the given ID is not found in the database
    header("Location: error.php");
    exit();
}

// Check if the user has already rated this user
if (isset($_SESSION['rated_users']) && in_array($user_id, $_SESSION['rated_users'])) {
    // Redirect to an error page or display an error message if the user has already rated this user
    echo '<script>alert("You have already rated this user.");</script>';
}


// Handle the form submission
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    // Get the rating value from the form data
    $rating = $_POST['rating'];
    $num_rating=$num_rating+1;
     // Check if the user has already rated this user
     if (isset($_SESSION['rated_users']) && in_array($user_id, $_SESSION['rated_users'])) {
        // Update the existing rating in the database
        $sql = "UPDATE ratings SET rating = ? WHERE user_id = ? AND rater_id= ? ";
        $stmt = mysqli_prepare($conn, $sql);
        mysqli_stmt_bind_param($stmt, "iii", $rating, $user_id,$rater_id);
        mysqli_stmt_execute($stmt);
    // Save the rating to the database
     }

    else {
        // Save the new rating to the database
        $sql = "INSERT INTO ratings (user_id, rating, num_ratings,rater_id) VALUES (?, ?, ?, ?)";
        $stmt = mysqli_prepare($conn, $sql);
        mysqli_stmt_bind_param($stmt, "iiii", $user_id, $rating, $num_rating,$rater_id);
        mysqli_stmt_execute($stmt);

        // Add the user ID to the list of rated users in the session
        if (!isset($_SESSION['rated_users'])) {
            $_SESSION['rated_users'] = array();
        }
        $_SESSION['rated_users'][] = $user_id;
    }
        //  Redirect to the users.php page 
        header("Location: users.php");




        exit();
    } else {
        // Display an error message if the query fails
       $error_message = "An error occurred while saving the rating. Please try again later.";
    }


// Display the rate form
?>