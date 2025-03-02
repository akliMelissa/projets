<?php include("job_logic.php"); ?>
<!DOCTYPE html>
<html>

<head>
  <title>Actors</title>
  <link href="profiles.css" rel="stylesheet">
 

</head>

<body>
  
  <div class="profiles">
    <?php foreach ($users as $user) : 
     
      ?>
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

              // Convert the average rating toa star rating
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
            <br><button class="b1" onclick="window.location.href='../profile/other_users_profile.php?id=<?php echo $user['id']; ?>&first_name=<?php echo $user['first_name']; ?>&last_name=<?php echo $user['family_name']; ?>'">See Profile</button>
            <button class="b2" onclick="window.location.href='rating.php?id=<?php echo $user['id']; ?>&first_name=<?php echo $user['first_name']; ?>&last_name=<?php echo $user['family_name']; ?>'">Rate this User</button>
          </div>
        </div>
      </div>
 
<?php  endforeach; ?>
</div>


<div class="pagination">
  <a href="#" class="previous">&lsaquo; Previous</a>
  <a href="#" class="next">Next &rsaquo;</a>
</div>

<div class="footer_margin"></div>

<?php include("../footer.php") ?>



</body>

</html>