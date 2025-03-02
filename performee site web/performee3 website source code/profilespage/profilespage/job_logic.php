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
    body {
  background-image: url("../images/images.jpg");
  background-size: cover;
  background-position: center;
  color: #fff;
  font-family: Arial, Helvetica, sans-serif;
  font-size: 16px;
  line-height: 1.5;
  margin: 0;
  margin-bottom: 5%;
  margin-top: 3%;
}

.container{
    margin-bottom: 40px;
}

    .profile {
        background-color:#171717;
        display: flex;
        align-items: center;
        margin-bottom: 50px;    
        flex-wrap: wrap;
        justify-content: center;
        margin: 0 auto;
         width: 70%;
         padding:15px;
         border-left: 2px solid #d29a00;
         border-right: 1px solid #d29a00;
         
    }
    .profile img {
        width: 100px;
        height: 100px;
        border-radius: 50%;
        margin-right: 20px;
    }
    .profile .name {
        font-size: 24px;
        font-weight: bold;
    }
    .profile .buttons {
        margin-left: auto;
    }
    .profile button {
        border: none;
        padding: 10px 20px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 18px;
        font-weight: bold;
        margin-right: 10px;
        cursor: pointer;

    }

    .b1{
        background-color: #d67e03;
        color: white;
    }
    .b2{
        background-color: white;
        color:#d67e03;
    }
    
    .profile button:hover {
        background-color: white;
        color:#d29a00;
    }


    .search-container {
  display: flex;
  justify-content: center;
  width:100%;
  margin-bottom: 30px;
}

.search-container input[type=text] {
  padding: 10px;
  margin-right: 10px;
  border: none;
  border-radius: 5px;
  
}

.search-container button[type=submit] {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background-color: #d67e03;
  color: white;
  cursor: pointer;
}




</style>


<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <link href="https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700,800,900" rel="stylesheet">
    <link href="../css/search_page.css" rel="stylesheet" />

    <link rel="stylesheet" type="text/css" href="../css/style.css">





  </head>


  <body>
  <?php
 


 // Check if user is authenticated
 if (!isset($_SESSION['user_id'])) {
   // Redirect to login page if user is not authenticated
   include('../header.php') ;
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

