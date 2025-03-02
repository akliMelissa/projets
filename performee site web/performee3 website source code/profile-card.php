
<?php

$first_name=$_SESSION["user_first_name"];
$family_name=$_SESSION["user_family_name"];

// Prepare and bind the query
$stmt = $conn->prepare("SELECT * FROM talents WHERE user_id = ?");
$stmt->bind_param("i", $getid);


// Execute the query
$stmt->execute();


// Get the result set
$result = $stmt->get_result();

// Fetch the data and display it
if ($row = $result->fetch_assoc()) {
 

} else 
{
  $stmt = $conn->prepare("SELECT * FROM recruiters WHERE user_id = ?");
  $stmt->bind_param("i", $getid); 
  $stmt->execute();


// Get the result set
$result = $stmt->get_result(); 
$row = $result->fetch_assoc();
}

?>


<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
   
<style>
   
   @import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@300;700&display=swap");
* {
    padding: 0;
    margin: 0;
    box-sizing: border-box;
}

.header
{
    overflow: visible;
}


.user_card {
  
    display: none;
    padding: 15px;
    position: absolute;
    top:-20px;
    right:5px;
    width: 250px;
    background: #222;
    border-radius: 5px;
    text-align: center;
    box-shadow: 0 10px 15px rgba(0, 0, 0, 0.7);
    user-select: none;
    z-index: 3;
    
}

.user_name{
    text-align: center;
    background-color:#282828 ;
    margin-top: -15px;
    margin-left: 50px;
}


.profile {
    position: absolute;
    width: 70px;
    height:70px;
    left:8px;
    float:left;
    border-radius: 50%;
    border: 2px solid #222;
    background: #222;
    padding: 2px;
}

.profile-name {
    font-size: 15px !important;
    color: goldenrod !important ;
}

.about {

    line-height: 1.6;
    font-size:13px;
    color:white;
    opacity:0.8;
}


.card_ul {
  list-style-type: none;
  margin: 0;
  margin-bottom: 20px;
  margin-top:20px;
  padding: 0;
  width: 100%;
  background-color:#282828;
}

.card_ul  li a  {
  display: block;
  color: white;
  padding: 8px 16px;
  text-decoration: none;
   opacity:0.9;
   font-size: 12px;
 
}

/* Change the link color on hover */
.card_ul  li a:hover {
  background-color: #181818;
  color :gray;
}

.icon{
    position: absolute;
    height: 20px;
    width: 20px;
  left:40px;
   
}


.copy_rights
{
   color :goldenrod;
   font-size: 12px;


}




</style>
</head>
<body>



<div class="user_card">
        <div >
            <a href="profile/profile.php">        
    <?php echo " <img src='data:image/jpeg;base64," . base64_encode($row["profile_picture"]) . "' class='profile'><br>"; ?>
</a>
        </div>

        <section class="user_name">
        <h3 class="profile-name"><?php echo $family_name." ".$first_name; ?></h3>
        <p class="about">

 <?php

      
$sql = "SELECT rating FROM ratings WHERE user_id =$getid ";
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
    $num_ratings=0;
    $star_rating=" "; 
    $avg_rating=0;

}

      // Print the number of ratings and the average rating in the form of stars
      if($num_ratings==0){ echo "No ratings yet!";}
      else{
        if($num_ratings==1) $note=" rating"; 
        else $note=" ratings";

        echo " $star_rating (" . $avg_rating . " )";
        echo '<br>'.$num_ratings.$note;
      }
           
             ?>
            
        <br></p>
        </section>


         <ul class="card_ul" >
           <li><a href="edit_profile/edit_profile.php"> Edit profile<img class="icon" src="images/edit-user-24.png"></a></li>
          <li><a href="messages.php">Messages<img class="icon" src="images/message-24.png"></a></li>
          <li><a href="profile/recruiter_profile.php">Posts<img class="icon" src="images/message-24.png"></a></li>
          <li><a href="settings.php"> Settings & privacy<img class="icon" src="images/settings-icon.png"></a></li>
          <li><a href="logout.php">Log out<img class="icon" src="images/logout-icon.png"></a></li>
          </ul>
    
        <div calss="card_buttom"> 
       <!-- <a>Privacy ·</a> <a> Terms ·</a>  <a>Cookies ·</a> -->
        <p class="copy_rights"><a href="copyright.php"> Performee © 2023</a></p></div>
    </div>



</body>
</html>