

<?php



include("bd_connect.php");

session_start();
  if((isset($_SESSION['user_id'])))
  {
    $getid=$_SESSION['user_id'];
    
  }
  else {
    echo "No ID";
  }





?>


<!DOCTYPE html>
<html>
<head>


<style>


@import url('https://fonts.googleapis.com/css2?family=Great+Vibes&family=Space+Grotesk&family=Spectral&display=swap');
html,body,header{
  margin: 0;
  padding: 0;
  border: 0;
  outline: 0;
}


* {
  box-sizing: border-box;
}

/* Style the header with a grey background and some padding */
.header {
  overflow: hidden;
  height: 85px;
  background-color: #080808;
  width: 100%;
  -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=50)";
  filter: alpha(opacity=80);
  -moz-opacity: 0.80;
  -khtml-opacity: 0.8;
 
  
}

/* Style the header links */
.header_right a {
  float: left;
  color: #b87333;
  text-align: center;
  padding: 12px;
  text-decoration:none;
  font-size: 20px; 
  line-height: 18px;
  border-radius: 1px;
  
}

/* Style the logo link (notice that we set the same value of line-height and font-size to prevent the header to increase when the font gets bigger */
.header .logo img{
 height:75px;
 width:265px;
 

}

.header_right{
  float:left;
   position:absolute;
   right :20% ;
   top:15px;

}
/* Change the background color on mouse-over */
.header_right a:hover {
  background-color: #fafafa;
  color: #b87333;
  opacity: 70%;
  transition: all .3s;
}





/* Add media queries for responsiveness - when the screen is 500px wide or less, stack the links on top of each other */ 
@media screen and (max-width: 500px) {
  .header_right a {
    float: none;
    display: block;
    text-align: left;
  }
  .header_right {
    float: none;
  }
}




.user-icon
{
    height: 50px;
    width:50px;
    border-radius: 50%;
}




.user-icon
{
    position: absolute;
    right:2%;
    top:-55px;
   
    float: right;
} 

.dropdown:hover .user_card {
  display: block;
}


</style>


</head>
<body>

<div class="header">
        <a href="index.php" class="logo">
            <img src="../images/logo.png">
        </a>
        <div class="header_right">
            
            <a class="active" href="../index.php">Home</a> 
            <a href="#contact">About us</a> 
            <a href="addjob.php">Post a job</a> 
            <a href="#contact">Find a talent </a> 
          
        </div>
        <div class="dropdown">
        <?php echo " <img src='data:image/jpeg;base64," . base64_encode($row["profile_picture"]) . "' class='user-icon'><br>"; ?>

         
        <?php include('profile_card.php'); ?>
        </div>
        
    </div>

</body>
</html>