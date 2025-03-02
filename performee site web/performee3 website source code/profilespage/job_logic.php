<?php

// Retrieve user data from the database
session_start();
include("../bd_connect.php");
$users = array();
$current_url = $_SERVER['REQUEST_URI'];
if (strpos($current_url, 'actors.php') !== false) {
    $getjob='Actor';
}
elseif (strpos($current_url, 'singers.php') !== false){
    $getjob='Actor';

}
elseif (strpos($current_url, 'dancers.php') !== false){
    $getjob='Dancer';

}
elseif (strpos($current_url, 'models.php') !== false){
    $getjob='Model';

}
elseif (strpos($current_url, 'Tv.php') !== false){
    $getjob='TV host';

}
$stmt = $conn->prepare("SELECT u.id, u.family_name, u.first_name, t.profile_picture FROM users u JOIN talents t ON u.id = t.user_id WHERE t.job = ?");
$stmt->bind_param("s", $getjob);


// Execute the query
$stmt->execute();


// Get the result set
$result = $stmt->get_result();
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $users[] = $row;
    }
}


// Handle search form submission
if (isset($_GET['search'])) {
    $search_term = $_GET['search'];
    $search_words = explode(' ', strtolower($search_term));
    $filtered_users = array_filter($users, function($user) use ($search_words) {
        foreach ($search_words as $word) {
            if (!strstr(strtolower($user['first_name'] . ' ' . $user['family_name']), $word)) {
                return false;
            }
        }
        return true;
    });
    $users = array_values($filtered_users);
}

// Sort users by number of ratings and average rating
usort($users, function ($a, $b) use ($conn) {
    $id_a = $a['id'];
    $id_b =$b['id'];
    $sql_a = "SELECT COUNT(*) AS num_ratings_a, AVG(rating) AS avg_rating_a FROM ratings WHERE user_id = $id_a";
    $sql_b = "SELECT COUNT(*) AS num_ratings_b, AVG(rating) AS avg_rating_b FROM ratings WHERE user_id = $id_b";
    $result_a = mysqli_query($conn, $sql_a);
    $result_b = mysqli_query($conn, $sql_b);
    $num_ratings_a = $avg_rating_a = $num_ratings_b = $avg_rating_b = 0;
    if (mysqli_num_rows($result_a) > 0) {
        $row_a = mysqli_fetch_assoc($result_a);
        $num_ratings_a = $row_a['num_ratings_a'];
        $avg_rating_a = $row_a['avg_rating_a'];
    }
    if (mysqli_num_rows($result_b) > 0) {
        $row_b = mysqli_fetch_assoc($result_b);
        $num_ratings_b = $row_b['num_ratings_b'];
        $avg_rating_b = $row_b['avg_rating_b'];
    }
    if ($num_ratings_a != $num_ratings_b) {
        return $num_ratings_b - $num_ratings_a; // Sort by number of ratings in descending order
    } else {
        return $avg_rating_b - $avg_rating_a; // Sort by average rating in descending order
    }
});

 ?>
 <style>
  



   






</style>


<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <link href="https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700,800,900" rel="stylesheet">
    <link href="../css/search_page.css" rel="stylesheet" />

   
    <link href="profiles.css" rel="stylesheet">




  </head>


  <body>
  <?php
 


 // Check if user is authenticated
 if (!isset($_SESSION['user_id'])) {
   // Redirect to login page if user is not authenticated
   include('header.php') ;
 }
   else{
	$getid = $_SESSION["user_id"];
	include('user_header.php');
   }
 ?>


<div class="search-container">
      <form method="GET">
        <input type="text" placeholder="Search by name..." name="search" value="<?php echo isset($_GET['search']) ? $_GET['search'] : ''; ?>">
        <button type="submit">Search</button>
      </form>
    </div>

  </body>
</html>   

