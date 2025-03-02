<?php
// Retrieve user data from the database
session_start();
error_reporting(E_ALL);
ini_set('display_errors', 1);

include("../bd_connect.php");
$users = array();

// Retrieve all users from the database
$sql = "SELECT u.id, u.family_name, u.first_name, t.profile_picture FROM users u JOIN talents t ON u.id = t.user_id";
$result = mysqli_query($conn, $sql);
if (mysqli_num_rows($result) > 0) {
    while ($row = mysqli_fetch_assoc($result)) {
        $users[] = $row;
    }
}

$itemsPerPage = 8;

usort($users, function ($a, $b) use ($conn) {
    $id_a = $a['id'];
    $id_b = $b['id'];
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

// Get the current page number from the query parameter
$page = isset($_GET['page']) ? $_GET['page'] : 1;

// Calculate the offset for the current page
$offset = ($page - 1) * $itemsPerPage;

// Handle search form submission
if (isset($_GET['search'])) {
    $search_term = $_GET['search'];
    $search_words = explode(' ', strtolower($search_term));
    $filtered_users = array_filter($users, function ($user) use ($search_words) {
        foreach ($search_words as $word) {
            if (!strstr(strtolower($user['first_name'] . ' ' . $user['family_name']), $word)) {
                return false;
            }
        }
        return true;
    });

    // Update the usersPerPage array based on the filtered users
    $usersPerPage = array_slice($filtered_users, $offset, $itemsPerPage);

    // Update the totalItems count based on the filtered users
    $totalItems = count($filtered_users);

    // Pass the search term as a query parameter in the pagination links
    $paginationParams = '&search=' . urlencode($search_term);
} else {
    // If no search term is present, use the original users array
    $usersPerPage = array_slice($users, $offset, $itemsPerPage);
    $totalItems = count($users);

    // Empty query parameter for pagination links
    $paginationParams = '';
}

$totalPages = ceil($totalItems / $itemsPerPage);

// Sort users by number of ratings and average rating




?>

<style>
    body {
      
       
        color: #fff;
        font-size: 16px;
        line-height: 1.5;
        margin: 0;
        margin-bottom: 5%;
        margin-top: 3%;
    }

    .profiles {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        padding-left: 10%;
        padding-right: 10%;
        margin-bottom: 10%;
    }

    .container {
        margin-bottom: 40px;
        width: 90%;
        color: #aeb0b3;


    }

    .profile {
        background-color: #000;
        display: flex;
        align-items: center;
        margin-bottom: 50px;
        flex-wrap: wrap;
        justify-content: center;
        margin: 0 auto;
        padding: 15px;
        border: 1px solid #aeb0b3;
        border-radius: 2%;



    }

    .profile img {
        width: 120px;
        height: 150%;
        margin-right: 80px;
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
        padding: 03px 07px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 15px;
        font-weight: bold;
        margin-right: 10px;
        cursor: pointer;
        font-family: 'Times New Roman', Times, serif;

    }

    .b1 {
        background-color: #b87333;
        color: white;
    }

    .b2 {
        background-color: white;
        color: #d67e03;
    }

    .profile button:hover {
        background-color: white;
        color: #b87333;
    }






    .search-container button[type=submit] {
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        background-color: #b87333;
        color: white;
        cursor: pointer;
    }

    div.pagination {
        padding: 3px;
        margin: 3px;
        text-align: center;
        width: 171px;
        margin-left: auto;
        margin-right: auto;
        margin-bottom: 45px;
    }

    div.pagination a {
        padding: 2px 5px 2px 5px;
        margin: 2px;
        border: 1.3px solid #7f0206e8;
        text-decoration: none;
        /* no underline */
        color: #000;
        background-color: rgba(237, 168, 98, 0.463);
        block-size: auto;
    }

    div.pagination a:hover,
    div.digg a:active {
        border: 1px solid #ffe8a1;
        color: #000;
    }

    div.pagination span.current {
        padding: 2px 5px 2px 5px;
        margin: 2px;
        border: 1px solid #eee;
        font-weight: bold;
        background-color: #aeb0b3;
        color: #FFF;
    }


    div.pagination span.disabled {
        padding: 2px 5px 2px 5px;
        margin: 2px;
        border: 1px solid #EEE;
        color: #DDD;
        background-color: #aeb0b3;
    }

    .modal-container {
        display: none;
        position: fixed;
        z-index: 1;
        top: 0;
        width: 50%;
        height: 35%;
        overflow: auto;
        background-color: rgba(0, 0, 0, 0.6);
        text-align: center;
        margin: 23%;
        margin-top: 10%;


    }

    .modal {
        text-align: center;
        background-color: #000;
        padding: 30px;
        border: 1px solid #d67e03;
        border-radius: 05%;


    }

    input[type="submit"] {
        margin-top: 20px;
        background-color: #b87333;
        color: #ffffff;
        border: none;
        border-radius: 05%;
        padding: 10px 20px;
        font-size: 16px;
        cursor: pointer;
    }

    button[type="button"] {
        margin-top: 20px;
        background-color: #b87333;
        color: #ffffff;
        border: none;
        border-radius: 05%;
        padding: 10px 20px;
        font-size: 16px;
        cursor: pointer;
    }

    select {
        font-size: 16px;
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
        background-color: #fff;
        color: #333;
        width: 200px;
    }

    select:focus {
        outline: none;
        border-color: gold;
        box-shadow: 0 0 5px gold;
    }

    .search-container {

        align-items: center;
        margin-left: 28%;

        margin-bottom: 10%;

    }

    .search-container input {
        width: 50%;
        border: 3px solid khaki;
        padding: 11px;
        border-radius: 30px;
        margin-top: 40px;


    }

    .search-container i {
        position: absolute;
        margin-left: 845px;
        margin-top: 40px;
    }

    .talent-title {
        text-align: center;
        color: #b87333;
        font-size: 25px;

    }
    body::before {
  content: "";
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9); /* Adjust the opacity to make it darker/lighter */
  z-index: -1; /* Ensure the pseudo-element is positioned behind other content */
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


<body background="../images/images.jpg">
    <?php



    // Check if user is authenticated
    if (!isset($_SESSION['user_id'])) {
        // Redirect to login page if user is not authenticated
        include('users_header.php');
    } else {
        $getid = $_SESSION["user_id"];
        include('user_header_users.php');
    }
    ?>
    <div id="modal-container" class="modal-container">
        <div class="modal">

            <div id="name" style="font-size:30px; margin-bottom:20px; color:#d67e03;">

            </div>
            <?php if (isset($error_message)) : ?>
                <divclass="error"><?php echo $error_message; ?>
        </div>
    <?php endif; ?>
    <form   method="POST">
        <label>

            <select name="rating">
                <option value="1">1 star</option>
                <option value="2">2 stars</option>
                <option value="3">3 stars</option>
                <option value="4">4 stars</option>
                <option value="5">5 stars</option>
            </select>
        </label>

        <br><input type="submit" value="Submit">
        <button type="button" onclick="closeModal()">Cancel</button>

    </form>
    </div>
    </div>
    <div class="talent-title">
        <h1>Find A Talent</h1>
    </div>
    <div class="search-container">
        <form method="GET">
            <input type="text" placeholder="Search by name..." name="search" value="<?php echo isset($_GET['search']) ? $_GET['search'] : ''; ?>">
            <button type="submit">Search</button>
        </form>
    </div>



    <div class="profiles">
        <?php foreach ($usersPerPage as $user) : ?>
            <div class="container">
                <div class="profile">
                    <img src="data:image/jpeg;base64,<?php echo base64_encode($user['profile_picture']); ?>" alt="Profile Image">
                    <div class="info">
                        <div class="name"><?php echo $user['family_name'] . ' ' . $user['first_name']; ?></div>


                        <?php

                        $id = $user['id'];
                        $sql = "SELECT rating FROM ratings WHERE user_id =$id ";
                        $result = $conn->query($sql);

                        if ($result->num_rows > 0) {
                            // Fetch the ratings and calculate the average rating
                            $ratings = array();
                            while ($row = $result->fetch_assoc()) {
                                $ratings[] = $row['rating'];
                            }
                            $num_ratings = count($ratings);
                            $avg_rating = array_sum($ratings) / $num_ratings;

                            // Convert the average rating to a star rating
                            $star_rating = '';
                            for ($i = 0; $i < (int)$avg_rating; $i++) {
                                $star_rating .= '⭐️';
                            }
                            if ($avg_rating - (int)$avg_rating >= 0.5) {
                                $star_rating .= '⭐️';
                            }
                        } else {
                            $num_ratings = 0;
                            $star_rating = " ";
                            $avg_rating = 0;
                            echo "No ratings found.";
                        }

                        // Print the number of ratings and the average rating in the form of stars
                        echo "Number of ratings: $num_ratings <br> ";
                        echo "Average rating: $star_rating (" . number_format($avg_rating, 1) . ")"; ?>

                    </div>
                    <div class="buttons">
                        <br><button class="b1" onclick="window.location.href='other_users_profile.php?id=<?php echo $user['id']; ?>&first_name=<?php echo $user['first_name']; ?>&last_name=<?php echo $user['family_name']; ?>'">See Profile</button>
                        <button onclick="openModal('<?php echo $user['first_name']; ?>','<?php echo $user['family_name'] ?> ', '<?php echo $user['id'] ?>')">Rate this user</button>
                    </div>
                </div>
            </div>


        <?php endforeach; ?>


    </div>






    <!-- Display pagination links -->
    <div class="pagination">
        <?php
        $maxPagesToShow = 3; // Maximum number of pages to show in the pagination
        $ellipsis = false; // Flag to determine if ellipsis should be displayed

        // Calculate the range of pages to display
        $startPage = max(1, $page - floor($maxPagesToShow / 2));
        $endPage = min($startPage + $maxPagesToShow - 1, $totalPages);

        // Display ellipsis if necessary
        if ($startPage > 1) {
            echo '<a href="?page=1">1</a>';
            if ($startPage > 2) {
                echo '<span class="ellipsis">...</span>';
            }
            $ellipsis = true;
        }

        // Display page links
        for ($i = $startPage; $i <= $endPage; $i++) {
            echo '<a href="?page=' . $i . '"';
            if ($i == $page) {
                echo ' class="active"';
            }
            echo '>' . $i . '</a>';
        }

        // Display last page link
        if ($endPage < $totalPages) {
            if ($endPage < $totalPages - 1 && !$ellipsis) {
                echo '<span class="ellipsis">...</span>';
            }
            echo '<a href="?page=' . $totalPages . '">' . $totalPages . '</a>';
        }
        ?>
    </div>





    </div>


    <?php include('../footer.php') ?>
    <?php





    ?>





</body><!-- This templates was made by Colorlib (https://colorlib.com) -->

</html>
<script>
    function openModal(first_name, last_name, user_id) {
        var modal = document.getElementById("modal-container");
        modal.style.display = "block";
        var form = document.querySelector("#modal-container form");
        form.action = "rating.php?id=" + user_id;
        var nameDiv = document.querySelector("#name");
        nameDiv.textContent = "Rate " + first_name + " " + last_name;
        form.addEventListener("submit", function(event) {
            event.preventDefault();
            var xhr = new XMLHttpRequest();
            xhr.open("POST", form.action);
            xhr.onload = function() {
                if (xhr.status === 200) {
                    window.location.reload();
                } 
            };
            xhr.send(new FormData(form));
        });
    }

    function closeModal() {
        var modal = document.getElementById("modal-container");
        modal.style.display = "none";
    }
</script>