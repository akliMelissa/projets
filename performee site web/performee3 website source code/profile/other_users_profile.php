<?php 




session_start();

if (!isset($_GET['id']) || !isset($_GET['first_name']) || !isset($_GET['last_name'])) {
    // Redirect to login page if user is not authenticated
   // header("Location: login.php");
    exit();
}

$first_name = $_GET['first_name'];
$last_name = $_GET['last_name'];
$getid = $_GET['id'];
$sender_id=$_SESSION['user_id'];
include("../bd_connect.php");



// Prepare and bind the query
$stmt = $conn->prepare("SELECT * FROM talents WHERE user_id = ?");
$stmt->bind_param("i", $getid);

// Execute the query
$stmt->execute();

// Get the result set
$result = $stmt->get_result();

// Fetch the data and display it
if ($row = $result->fetch_assoc()) {
    
   $location=$row["location"];
   $phone=$row["phone_number"];
   $bio=$row["bio"];
   
    $birthdate = $row["birthdate"] ;
    $age = date_diff(date_create($birthdate), date_create('today'))->y;
    $gender=$row["gender"];
    $height=$row["height"];
    $weight=$row["weight"];
    
    $src= "data:image/jpeg;base64," . base64_encode($row["profile_picture"]) ;
  
$background_pic = $row["background_pic"]; 
$filename = "./images/talent_" . $getid . ".jpg";
file_put_contents($filename, $background_pic); 

} else {
  echo "No talent found with ID " . $getid;
}



if(isset($getid) && !empty($getid)) {
   
    /*$query = $conn->prepare('SELECT * FROM users WHERE id = ?');
    $recupuser->execute(array($getid));
    if($recupuser->mysqli_num_rows() > 0) {*/
        if(isset($_POST["send"])) {
            $message = htmlspecialchars($_POST["message"]);
            $subject = htmlspecialchars($_POST["subject"]);
            $file = $_POST["file"];
            $query = "INSERT INTO messages(subject, message_text, file, receiver_id, sender_id) VALUES ('$subject', '$message', '$file', '$getid','$sender_id')";
            $insertmessage=mysqli_query($conn,$query);
            if($insertmessage){
              
          }else{
              echo 'Fail to send your message';
          }
        } else if(isset($_POST['cancel'])) {
            $_POST = array();
            
        }
    /*} else {
        echo "User not found";
    }*/
} else {
    echo "Not found Identifier";
}




     
$sql = "SELECT rating FROM ratings WHERE user_id = $getid";
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
$rounded_rating = round($avg_rating * 2) / 2; // round to nearest half star
for ($i = 0; $i < floor($rounded_rating); $i++) {
    $star_rating .= '⭐️';
}
if ($rounded_rating - floor($rounded_rating) >= 0.5) {
    $star_rating .= '⭐️'; // add half star
}



} else {
    $num_ratings = 0;
    $avg_rating = 0;
    $star_rating =" ";

}








?>


<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="profilestyle.css">
  <link href='https://fonts.googleapis.com/css?family=Quattrocento' rel='stylesheet'>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

  <title>Document</title>

<?php
  echo "<style>"; 
echo ".section1{";
echo " background-image:url('images/talent_" . $getid . ".jpg');";  
echo "}";
echo "</style>";
?>


</head>
<body>
  <div class="section1">
  <button class="circle-button" onclick="window.location.href='../index.php'">
  <i class="fa fa-arrow-left">Back</i>
</button>
    <div class="section1-container">
      <h1 class="section-title"><?php  echo  $first_name; ?></h1>
      <h1 class="section-title1"><?php  echo  $last_name; ?></h1>
      <h2>My name is <?php  echo  $first_name." ".$last_name."<br> from ". $location."<br>".  $bio."<br>";?>  </h2>
     
      <div style="color: white ; font-size : 20;">
      
         <?php
      // Print the number of ratings and the average rating in the form of stars
           
            echo " $star_rating (" . $num_ratings . ")" ?>

          </div>


      <a href="#contact" class="contact-button">Contact Me <span class="arrow">&#8594;</span></a>
      <div class="stars">
      <?php  
      
      $i=$avg_rating;
       for($i;$i>=0.5;$i--){
        echo ' <span class="star">&#9733;</span>';
       }
        echo '<span class="number">( '.number_format($avg_rating, 1).' ) </span>';

       ?>
         
      </div>
      <?php 
       if($num_ratings==0) $note=' No ratings yet ! ';
       elseif($num_ratings==1) $note=' rating ';
       else $note=' ratings';
       echo '<span class="number"> '.$num_ratings.$note.'</span>';?>
     
        
      </div>
      </div>
    </div>
    <div class="about-me-section">
        <div class="picture-container">
        <?php echo " <img src='$src'><br>"; ?>
        </div>
        <div class="text-container">
          <div class="about-me-container">
            <h2>About Me</h2>
            <ul class="about-me-list">
            <?php if(!empty($gender)): ?>  <li><span class="point">•</span> Gender : <?php echo $gender ?></li><?php endif; ?>
            <?php if(!empty($age)): ?> <li><span class="point">•</span> Age : <?php echo $age ?> years old </li><?php endif; ?>
            <?php if(!empty($height)): ?>  <li><span class="point">•</span> Height: <?php echo $height ?> cm </li><?php endif; ?>
            <?php if(!empty($weight)): ?><li><span class="point">•</span> Weight: <?php echo $weight ?> kg</li><?php endif; ?>
            <?php if(!empty($phone_number)): ?><li><span class="point">•</span> Phone: <?php echo $phone_number?><?php endif; ?>
               
            </ul>
          </div>
          <div class="skills-container">
            <h2>Skills</h2>
            
<?php
// Prepare a query to retrieve the skills of the user
$stmt = $conn->prepare('SELECT * FROM skills WHERE user_id = ?');
$stmt->bind_param('i', $getid);
$stmt->execute();
$stmt->store_result();

// Display the skills as a bullet list
if ($stmt->num_rows > 0) {
    echo '<ul class="skills-list">';
    $stmt->bind_result($skill_id, $user_id, $skill_name);
    while ($stmt->fetch()) {
        echo '<li><span class="point">•</span> ' . htmlspecialchars($skill_name) . '</li>';
    }
    echo '</ul>';
} else {
    echo 'No skills found for this user ' ;
}

// Close the statement and the database connection
$stmt->close();
?>
          </div>
        </div>
        <div class="experience-container">

<h2>Experience</h2>
<?php 
// Prepare a query to retrieve the experiences of the user
$stmt = $conn->prepare('SELECT * FROM experiences WHERE user_id = ?');
$stmt->bind_param('i', $getid);
$stmt->execute();
$stmt->store_result();

// Display the experiences as a bullet list
if ($stmt->num_rows > 0) {
    echo '<ul class="experience-list">';
    $stmt->bind_result($id, $user_id, $experiance,$start_date , $end_date);
    while ($stmt->fetch()) {
        echo '<li><span class="point">•</span> ' . htmlspecialchars($start_date) . ' to ' . htmlspecialchars($end_date) .'<br>'. htmlspecialchars($experiance) . '</li>';
    }
    echo '</ul>';
} else {
    echo 'No experiences found for this user ';
}
// Close the statement and the database connection
$stmt->close();


?>


</div>
      </div>
      <div class="media-section">
        <h2>Media</h2>
        <hr color="#7B3B0E"><br>
        <ul class="media-list">
          <li><a href="#" class="active" data-tab="photos">Photos</a></li>
          <li><a href="#" data-tab="videos">Videos</a></li>
          <li><a href="#" data-tab="links">Links</a></li>
        </ul>
        <div class="media-content">
        <div class="photos tab active">
  <?php
  //display images 
  if(isset($_SESSION['user_id']))  $user_id = $_SESSION['user_id'];
    $query = "SELECT * FROM images WHERE user_id = '$user_id'";
    $result = mysqli_query($conn, $query);

    if (mysqli_num_rows($result) > 0) {
      while ($row = mysqli_fetch_assoc($result)) {
      echo '<img src="data:image/jpeg;base64,'.base64_encode($row['data']).'" alt="'.$row['name'].'">';
    }} 
      else {echo "No images had been added .";}
  ?>
</div>




<div class="videos tab">
  <?php
  // Display videos 
  $user_id = $getid;
  $query = "SELECT * FROM videos WHERE user_id = '$user_id'";
  $result = mysqli_query($conn, $query);

  if (mysqli_num_rows($result) == 0) {
    echo "No videos had been added.";
  } else {
    while ($row = mysqli_fetch_assoc($result)) {
      echo '<div class="video-wrapper">
          <video width="320" height="240" controls>
            <source src="' . $row['video_link'] . '" type="video/mp4">
            Your browser does not support the video tag.
          </video>
        </div>';
    }
  }
  ?>
</div>
    
    
         















        <!--  <div class="videos tab">

            <iframe src="https://www.youtube.com/watch?v=D5aJNFWsWew&list=PLBw9d_OueVJS_084gYQexJ38LC2LEhpR4&index=2" frameborder="0" allowfullscreen></iframe>
            <iframe src="https://www.youtube.com/watch?v=D5aJNFWsWew&list=PLBw9d_OueVJS_084gYQexJ38LC2LEhpR4&index=2" frameborder="0" allowfullscreen></iframe>
            <iframe src="https://www.youtube.com/watch?v=D5aJNFWsWew&list=PLBw9d_OueVJS_084gYQexJ38LC2LEhpR4&index=2" frameborder="0" allowfullscreen></iframe>
          </div>-->











          <div class="links tab">
            <ul>
            <?php if(!empty($facebook)):?><li><a href="<?php echo $facebook ?>">Facebook</a></li><?php endif; ?>
            <?php if(!empty($instagram)):?><li><a href="<?php echo $instagram ?>">Instagram</a></li><?php endif; ?>
            <?php if(!empty($linedin)):?><li><a href="<?php echo $linkedin ?>">Linedin</a></li><?php endif; ?>
            </ul>
          </div>
        </div>
        <br><br><br><hr color="#7B3B0E">
      </div>
      

      <div class="contact2" id="contact">
      <div class="contact-form">
        <h2>Send a message to <?php  echo  $first_name." ".$last_name; ?></h2>
        <form action="" method="POST">
          <input type="text" name="subject" id="subject" placeholder="Subject" required >
          <textarea name="message" id="message" placeholder="Message" required></textarea>
          <div class="file-upload">
            <label for="file">
              <img src="images/material-symbols_attach-file-add.png" alt="Add File">Add file
            </label>
            <input type="file" name="file" id="file">
          </div>
          <div class="buttons">
            <button type="submit" name="send" id="send">Send</button>
            <button type="button" name="cancel" id="cancel">Cancel</button>
          </div>
        </form>
      </div>
      </div>


<script>

$(document).ready(function(){
        $('form').submit(function(e){
        e.preventDefault(); // prevent form submission and page refresh
        
        // check if subject and message fields are not empty
        if ($('#subject').val() === '' || $('#message').val() === '') {
            alert('Please fill in both the subject and message fields.');
            return false; // prevent the form from being submitted
        }
        
        var formData = new FormData(this);
        
        $.ajax({
            url: '',
            type: 'POST',
            data: formData,
            success: function(data){
                $('form')[0].reset(); // reset form after successful submission
                alert('Message sent successfully!');
            },
            error: function(){
                alert('Failed to send your message.');
            },
            cache: false,
            contentType: false,
            processData: false
                        });
                     });
                   });

</script>


      
      
   <!-- <div class="section2">
        <div class="section2-left">
          <img src="verticzl.png" alt="Your Image">
        </div>
        <div class="section2-middle">
          <div class="section2-about">
            <h2>About me</h2>
            <ul>
                <li>Gender : female</li>
                <li>Age : 21</li>
                <li>Height : 171 cm</li>
                <li>Weight : 50 kg</li>
                <li>Hair color : brown</li>
            </ul>
          </div>
          <div class="section2-skills">
            <h2>Skills</h2>
            <ul>
              <li>Skill 1</li>
              <li>Skill 2</li>
              <li>Skill 3</li>
              <li>Skill 4</li>
            </ul>
          </div>
        </div>
        <div class="section2-right">
          <div class="section2-experience">
            Experience
          </div>
        </div>
      </div> -->
      
      
      
      <script>
  // Get all the tabsand tab content
  const tabs = document.querySelectorAll('.media-list a');
  const tabContent = document.querySelectorAll('.media-content .tab');

  // Add event listener for each tab
  tabs.forEach(clickedTab => {
    clickedTab.addEventListener('click', () => {
      // Remove the active class from all tabs
      tabs.forEach(tab => {
        tab.classList.remove('active');
      });

      // Add the active class to the clicked tab
      clickedTab.classList.add('active');

      // Hide all tab content
      tabContent.forEach(tab => {
        tab.style.display = 'none';
      });

      // Get the data-tab attribute of the clicked tab
      const activeTab = clickedTab.getAttribute('data-tab');

      // Show the corresponding tab content
      document.querySelector('.media-content .' + activeTab).style.display = 'block';
    });
  });
</script>




<script>
function togglePlay(button) {
  var videoWrapper = button.parentElement;
  var video = videoWrapper.querySelector('iframe');
  if (video.getAttribute('autoplay') === null) {
    video.setAttribute('autoplay', '');
    button.innerText = 'Pause';
  } else {
    video.removeAttribute('autoplay');
    button.innerText = 'Play';
  }
}
</script>





</body>
</html>


  