<?php
session_start();
error_reporting(E_ALL);
ini_set('display_errors', 1);
include("logic.php");

$location = isset($_GET['location']) ? $_GET['location'] : '';
$role = isset($_GET['role']) ? $_GET['role'] : '';
$searchQuery = isset($_GET['search']) ? $_GET['search'] : '';

// Build the SQL query with the search and filter conditions
$sql = "SELECT * FROM job WHERE title LIKE '%$searchQuery%'";

if (!empty($location)) {
    $sql .= " AND location = '$location'";
}

if (!empty($role)) {
    $sql .= " AND role = '$role'";
}

// Execute the SQL query to fetch the results
$itemsPerPage = 9;
$page = isset($_GET['page']) ? $_GET['page'] : 1;
$offset = ($page - 1) * $itemsPerPage;
$sql .= " LIMIT $itemsPerPage OFFSET $offset";
$res = mysqli_query($conn, $sql);

// Build another SQL query to count the total number of items
$countQuery = "SELECT COUNT(*) as total FROM job WHERE title LIKE '%$searchQuery%'";

if (!empty($location)) {
    $countQuery .= " AND location = '$location'";
}

if (!empty($role)) {
    $countQuery .= " AND role = '$role'";
}

// Execute the count query and fetch the total number of items
$countResult = mysqli_query($conn, $countQuery);
$countRow = mysqli_fetch_assoc($countResult);
$totalItems = $countRow['total'];

// Calculate the total number of pages
$totalPages = ceil($totalItems / $itemsPerPage);

// Rest of your code...


?>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <title>Performee-Announcements</title>
    <meta content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="carousel-07/css/owl.carousel.min.css">
    <link rel="stylesheet" href="carousel-07/css/owl.theme.default.min.css">
    <link rel="stylesheet" href="carousel-07/css/style.css">
    <!-- Favicon -->
    <link href="img/favicon.ico" rel="icon">

    <!-- Google Web Fonts -->



    <!-- Font Awesome -->


    <!-- Libraries Stylesheet -->
    <link href="lib/owlcarousel/assets/owl.carousel.min.css" rel="stylesheet">

    <!-- Customized Bootstrap Stylesheet -->
    <link href="announcement-style.css" rel="stylesheet">

    <link href="https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700,800,900" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/style.css">

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">

</head>

<body background="images/images.jpg">
    <!-- Topbar Start -->



    <?php if (!isset($_SESSION['user_id'])) {
        // Redirect to login page if user is not authenticated
        include('header.php');
    } else {
        $getid = $_SESSION["user_id"];
        include('user_header.php');
    } ?>

    <style>
        .entete {
            display: inline-block;
            background: #343a40;
        }

        .blog-entry .meta-date span {
            display: block;
            color: #fff;
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

        .search-container button[type=submit] {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            background-color: #b87333;
            color: white;
            cursor: pointer;
        }

        h1 {
            margin-top: 5%;
            text-align: center;
            color: #b87333;
            font-size: 50px;
            font-weight: bold;

        }

        body::before {
            content: "";
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.7);
            /* Adjust the opacity to make it darker/lighter */
            z-index: -1;
            /* Ensure the pseudo-element is positioned behind other content */
        }
          

        /* Rest of your CSS styles */
    </style>


    <!-- Topbar End -->



    <!-- Search Bar -->
    <h1>Find a job </h1>
    <div class="search-container">
  <form method="GET">
    <input type="text" placeholder="Search by name..." name="search" value="<?php echo isset($_GET['search']) ? $_GET['search'] : ''; ?>">
    
    <button type="button" id="filterButton" class="btn">Filters</button>
    
    <div id="filterInputs" style="display: none;">
      <label for="location">Location:</label>
      <input type="text" name="location" value="<?php echo isset($_GET['location']) ? $_GET['location'] : ''; ?>"><br>
      
      <label for="role">Role:</label>
      <input type="text" name="role" value="<?php echo isset($_GET['role']) ? $_GET['role'] : ''; ?>"><br>
    </div>
    
    <button type="submit" class="btn">Search</button>
  </form>
</div>

<style>
    input{
        margin-bottom: 15px;
    }
  .search-container .btn {
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    background-color: #b87333;
    color: white;
    cursor: pointer;
  }
</style>

<script>
  document.getElementById("filterButton").addEventListener("click", function() {
    var filterInputs = document.getElementById("filterInputs");
    filterInputs.style.display = filterInputs.style.display === "none" ? "block" : "none";
  });
</script>


    <script src="https://kit.fontawesome.com/c0bfb983cf.js" crossorigin="anonymous"></script>
    <?php
    if (isset($_REQUEST["info"])) {
        if ($_REQUEST["info"] == "added") { ?>

            <div class="alert">
                Post has been added successfully !
            </div>

    <?php }
    }
    ?>
    <div id="content">
        <?php
        if (mysqli_num_rows($res) > 0) {
            while ($row = mysqli_fetch_assoc($res)) {
        ?>
                <div class="item">
                    <div class="blog-entry">
                        <a href="#" class="block-20 d-flex align-items-start" style="background-image: url('<?php echo $row['image'] ?>');">
                            <div class="entete">
                                <span class="day"><?php echo $row['date']; ?></span>


                            </div>
                            <div class="categ"><?php echo $row['role']; ?></div>
                        </a>
                        <div class="text border border-top-0 p-4">
                            <h3 class="heading"><a href="#"><b><?php echo $row['title']; ?></b></a></h3>
                            <p><?php echo $row['description']; ?></p>
                            <div class="d-flex align-items-center mt-4">

                                <p class="mb-0">
                                    <a href="job_details.php?job_id=<?php echo $row['job_id']; ?>" class="btn btn-primary">Read More -><span class="ion-ios-arrow-round-forward"></span></a>
                                </p>

                            </div>
                        </div>

                    </div>
                </div>
        <?php
            }
        } else {
            echo "<p>No job listings found.</p>";
        }
        ?>
    </div>
    <!--pagination-->

    <div class="pagination">
        <?php if ($page > 1) { ?>
            <a href="announcement.php?page=<?php echo $page - 1; ?>">&laquo;</a>
        <?php } else { ?>
            <span class="disabled">&laquo;</span>
        <?php } ?>

        <?php for ($i = 1; $i <= $totalPages; $i++) { ?>
            <?php if ($i == $page) { ?>
                <span class="current"><?php echo $i; ?></span>
            <?php } else { ?>
                <a href="announcement.php?page=<?php echo $i; ?>"><?php echo $i; ?></a>
            <?php } ?>
        <?php } ?>

        <?php if ($page < $totalPages) { ?>
            <a href="announcement.php?page=<?php echo $page + 1; ?>">&raquo;</a>
        <?php } else { ?>
            <span class="disabled">&raquo;</span>
        <?php } ?>
    </div>
    <?php include('footer.php') ?>

</body>

</html>