<?php



/*
session_start();

$first_name = $_SESSION["user_first_name"];
$last_name = $_SESSION["user_family_name"];

include("./../bd_connect.php");

if(isset($_SESSION['user_id']) && !empty($_SESSION['user_id'])) {
    $getid = ($_SESSION['user_id']);
    $recupuser = mysqli_prepare($conn, 'SELECT * FROM users WHERE id = ?');
    mysqli_stmt_bind_param($recupuser, 'i', $getid);
    mysqli_stmt_execute($recupuser);
    $result = mysqli_stmt_get_result($recupuser);
    if(mysqli_num_rows($result) > 0) {
        if(isset($_POST['send'])) {
            $message = htmlspecialchars($_POST['message']);
            $subject = $_POST['subject'];
            $file = $_POST['file'];
            $insertmessage = mysqli_prepare($conn, 'INSERT INTO messages(subject, message_text, file, receiver_id, sender_id) VALUES (?, ?, ?, ?, ?)');
            mysqli_stmt_bind_param($insertmessage, 'ssiii', $subject, $message, $file, $_SESSION['id'], $_SESSION['id']);
            mysqli_stmt_execute($insertmessage);
            echo "Message sent.";
        } else if(isset($_POST['cancel'])) {
            $_POST = array();
            echo "Message cancelled. All fields have been cleared.";
        }
    } else {
        echo "User not found";
    }
} else {
    echo "Not found Identifier";
}
*/

/*
session_start();

$first_name = $_SESSION["user_first_name"];
$last_name = $_SESSION["user_family_name"];

include("bd_connect.php");

if(isset($_SESSION['user_id']) && !empty($_SESSION['user_id'])) {
    $getid = ($_SESSION['user_id']);
    $recupuser = mysqli_prepare($conn, 'SELECT * FROM users WHERE id = ?');
    mysqli_stmt_bind_param($recupuser, 'i', $getid);
    mysqli_stmt_execute($recupuser);
    $result = mysqli_stmt_get_result($recupuser);
    if(mysqli_num_rows($result) > 0) {
        if(isset($_POST['send'])) {
            $message = htmlspecialchars($_POST['message']);
            $subject = $_POST['subject'];
            $file = $_POST['file'];
            $insertmessage = mysqli_prepare($conn, 'INSERT INTO messages(subject, message_text, file, receiver_id, sender_id) VALUES (?, ?, ?, ?, ?)');
            mysqli_stmt_bind_param($insertmessage, 'ssiii', $subject, $message, $file, $getid, $_SESSION['id']);
            mysqli_stmt_execute($insertmessage);
            echo "Message sent.";
        } else if(isset($_POST['cancel'])) {
            $_POST = array();
            echo "Message cancelled. All fields have been cleared.";
        }
    } else {
        echo "User not found";
    }
} else {
    echo "Not found Identifier";
}*/



session_start();

if (!isset($_SESSION['user_id']) || !isset($_SESSION["user_first_name"]) || !isset($_SESSION["user_family_name"])) {
    // Redirect to login page if user is not authenticated
    header("Location: ../login.php");
    exit();
}



$getid = $_SESSION["user_id"];

include("../bd_connect.php");



// Prepare and bind the query
$stmt = $conn->prepare("SELECT * FROM recruiters WHERE user_id = ?");
$stmt->bind_param("i", $getid);

// Execute the query
$stmt->execute();

// Get the result set
$result = $stmt->get_result();

// Fetch the data and display it
if ($row = $result->fetch_assoc()) {
    $company_name = $row["company_name"];
    $location = $row["location"];
    $phone = $row["phone_number"];
    $bio = $row["bio"];
    $job = $row["activity"];
    $src = "data:image/jpeg;base64," . base64_encode($row["profile_picture"]);
    $background_pic = $row["background_pic"];
    $filename = "./images/talent_" . $getid . ".jpg";
    file_put_contents($filename, $background_pic);
} else {
    echo "No recruiter found with ID " . $getid;
}



if (isset($getid) && !empty($getid)) {

    /*$query = $conn->prepare('SELECT * FROM users WHERE id = ?');
    $recupuser->execute(array($getid));
    if($recupuser->mysqli_num_rows() > 0) {*/
    if (isset($_POST["send"])) {
        $message = htmlspecialchars($_POST["message"]);
        $subject = htmlspecialchars($_POST["subject"]);
        $file = $_POST["file"];
        $query = "INSERT INTO messages(subject, message_text, file, receiver_id, sender_id) VALUES ('$subject', '$message', '$file', '$getid','$getid')";
        $insertmessage = mysqli_query($conn, $query);
        if ($insertmessage) {
        } else {
            echo 'Fail to send your message';
        }
    } else if (isset($_POST['cancel'])) {
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
    $star_rating = " ";
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
    <link href="announcement-style.css" rel="stylesheet">
    <link rel="stylesheet" href="../carousel-07/css/owl.carousel.min.css">
    <link rel="stylesheet" href="../carousel-07/css/owl.theme.default.min.css">
    <link rel="stylesheet" href="../carousel-07/css/style.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js">
    </script>

    <style>
         .about-me-section {
    background-color: black;
    color: white;
    padding: 50px;
    display: block;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0px;
    border: none;
    outline: none;
  } 
    </style>


    <title>Document</title>

    <?php
    echo "<style>";
    echo ".section1{";
    echo " background-image:url('images/talent_" . $getid . ".jpg');";
    echo "}";
    echo "</style>";
    ?>
    <style>
        .text_container{
            text-align: center;
        }
    </style>

</head>

<body>
    <div class="section1">
        <div class="section1-container">
            <h1 class="section-title"><?php echo  $company_name; ?></h1>

            <h2><?php echo $job . "<br> We are " . $company_name . " <br> from " . $location . "<br>" .  $bio . "<br>"; ?> </h2>




            <a href="#contact" class="contact-button">Contact Us <span class="arrow">&#8594;</span></a>
            <div class="stars">
                <?php

                $i = $avg_rating;
                for ($i; $i >= 0.5; $i--) {
                    echo ' <span class="star">&#9733;</span>';
                }
                echo '<span class="number">( ' . number_format($avg_rating, 1) . ' ) </span>';

                ?>

            </div>

            <?php
            if ($num_ratings == 0) $note = ' No ratings yet ! ';
            elseif ($num_ratings == 1) $note = ' rating ';
            else $note = ' ratings';
            echo '<span class="number"> ' . $num_ratings . $note . '</span>'; ?>

        </div>
    </div>
    <div class="about-me-section">
        
        <div class="text-container">
            <div class="about-me-container">
                <h2 >Work With Us</h2>

            </div>

        </div>
        <div id="content">
            <?php
            $stmt1 = $conn->prepare("SELECT * FROM job WHERE user_id = ?");
            $stmt1->bind_param("i", $getid);
            
            // Execute the query
            $stmt1->execute();
            
            // Get the result set
            $res = $stmt1->get_result();
            while ($row = mysqli_fetch_assoc($res)) { ?>
                <div class="item">
                    <div class="blog-entry">
                        <a href="#" class="block-20 d-flex align-items-start" style="background-image: url('carousel-07/images/<?php echo $row['image']; ?>');">
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
                                    <a href="../job_details.php?job_id=<?php echo $row['job_id']; ?>" class="btn btn-primary">Read More -><span class="ion-ios-arrow-round-forward"></span></a>
                                </p>
                                <p class="ml-auto meta2 mb-0">

                                    <a href="#">Apply</a>

                                </p>
                            </div>
                        </div>

                    </div>
                </div>
            <?php } ?>



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
                $user_id = $_SESSION['user_id'];
                $query = "SELECT * FROM images WHERE user_id = '$user_id'";
                $result = mysqli_query($conn, $query);

                if (mysqli_num_rows($result) == 0) {
                    echo "No images had been added .";
                } else {
                    while ($row = mysqli_fetch_assoc($result)) {
                        echo '<img src="data:image/jpeg;base64,' . base64_encode($row['data']) . '" alt="' . $row['name'] . '">';
                    }
                }
                ?>
            </div>




            <div class="videos tab">
                <?php
                //display videos 
                $user_id = $getid;
                $query = "SELECT * FROM videos WHERE user_id = '$user_id'";
                $result = mysqli_query($conn, $query);

                if (mysqli_num_rows($result) == 0) {
                    echo "No videos had been added .";
                } else {
                    while ($row = mysqli_fetch_assoc($result)) {
                        echo '<div class="video-wrapper">
          <iframe width="320" height="240" src="data:video/mp4;base64,' . base64_encode($row['data']) . '" frameborder="0" allowfullscreen></iframe>
          <button class="play-button" onclick="togglePlay(this)">Play</button>
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
                    <li><a href="#">Link 1</a></li>
                    <li><a href="#">Link 2</a></li>
                    <li><a href="#">Link 3</a></li>
                </ul>
            </div>
        </div>
        <br><br><br>
        <hr color="#7B3B0E">
    </div>


    <div class="contact2" id="contact">
        <div class="contact-form">
            <h2>Send a message to <?php echo  $company_name; ?></h2>
            <form action="" method="POST">
                <input type="text" name="subject" id="subject" placeholder="Subject" required>
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
        $(document).ready(function() {
            $('form').submit(function(e) {
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
                    success: function(data) {
                        $('form')[0].reset(); // reset form after successful submission
                        alert('Message sent successfully!');
                    },
                    error: function() {
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