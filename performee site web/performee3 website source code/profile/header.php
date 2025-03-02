

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
 .header a {
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

.header-right{
  margin-top: 10px;
  float:left;
}
/* Change the background color on mouse-over */
.header-right a:hover {
  background-color: #fafafa;
  color: #b87333;
  opacity: 70%;
  transition: all .3s;
}

/* Style the active/current link*/


/* Float the link section to the right */
.header-right {
  padding-top: 15px;;
  float: right;
  padding: 10px;
}

/* Add media queries for responsiveness - when the screen is 500px wide or less, stack the links on top of each other */ 
@media screen and (max-width: 500px) {
  .header a {
    float: none;
    display: block;
    text-align: left;
  }
  .header-right {
    float: none;
  }
}



</style>


</head>
<body>

<div class="header">
        <a href="../index.php" class="logo">
            <img src="../images/logofinal.jpg">
        </a>
        <div class="header-right">
            <a class="active" href="../index.php">Home</a>
            <a href="#contact">About us</a>
            <a href="login.php">Log in</a>
            <a href="login.php">Sign up</a>
        </div>
    </div>


</body>
</html>