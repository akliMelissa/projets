<?php



session_start();

include '../bd_connect.php'; 
error_reporting(E_ALL);
ini_set('display_errors', 1);



// Check if user is authenticated

if (!isset($_SESSION['user_id'])) {

  // Redirect to login page if user is not authenticated

  header("Location: ../login.php");

  exit();

}



$user_id = $_SESSION['user_id'];



// check if the form has been submitted

if (isset($_POST['edit_profile']) && $_SERVER['REQUEST_METHOD'] == 'POST') {

    

     // retrieve the form data

     $first_name = trim($_POST['fname']);

     $family_name = trim($_POST['lname']);

     $password = trim($_POST['password']);

     $email = trim($_POST['email']);

     $description = trim($_POST['description']);

     $location = trim($_POST['location']);

     $phone_number = trim($_POST['phone']);

     $profile_picture = $_FILES['profile-pic']['tmp_name'];

     $profile_pic_name = $_FILES['profile-pic']['name'];



     // read profile picture file data

if (!empty($profile_picture) && is_readable($profile_picture)) {

    $profile_pic_data = file_get_contents($profile_picture);

} 

     $background_picture = $_FILES['bg-pic']['tmp_name'];

     $bg_pic_name = $_FILES['bg-pic']['name'];

     if (!empty($background_picture) && is_readable($background_picture)) {

      $bg_pic_data = file_get_contents($background_picture);

} 

    

if (isset($_POST['gender'])) {

  $gender = $_POST['gender'];

}

if (isset($_POST['gender'])) {

  $job = $_POST['job'];



  if ($job === "Other") {

    $job = mysqli_real_escape_string($conn, $_POST['other-job-text']);

  }

}



     $height = $_POST['height'];

     $weight = $_POST['weight'];

     $birthdate = $_POST['birthdate'];

    

 

     // update user table

     if (!empty($first_name) || !empty($family_name) || !empty($password) || !empty($email)) {

     $query = "UPDATE users SET ";

     if (!empty($first_name)) {

         $query .= "first_name = '$first_name', ";

     }

     if (!empty($family_name)) {

         $query .= "family_name = '$family_name', ";

     }

     if (!empty($password)) {
      
        $hashed_password = password_hash($password, PASSWORD_DEFAULT);
         $query .= "password = '$hashed_password', ";

     }

     if (!empty($email)) {

         $query .= "email = '$email', ";

     }

     

     $query = rtrim($query, ", "); // remove the trailing comma and space

     $query .= " WHERE id = '$user_id'";

     $result = mysqli_query($conn, $query);

// check if the query was successful

if ($result) {

  echo "Record updated successfully";

} else {

  echo "Error updating record: " . mysqli_error($conn);

}



    }









    $instagram = trim($_POST['instagram']);

    $facebook = trim($_POST['facebook']);

    $linkedin = trim($_POST['linkedin']);



    if (!empty($location) || !empty($phone_number) || !empty($description) ||

    !empty($birthdate) || !empty($gender) || !empty($height) || !empty($weight)||

    !empty($job)||!empty($instagram)||!empty($facebook)||!empty($linkedin)){

     // update talents table

     $query = "UPDATE talents SET ";

     if (!empty($location)) {

         $query .= "location = '$location', ";

     }

     if (!empty($phone_number)) {

         $query .= "phone_number = '$phone_number', ";

     }

     if (!empty($description)) {

         $description = mysqli_real_escape_string($conn, $description);

         $query .= "bio = '$description', ";

     }

     if (!empty($birthdate)) {

         $query .= "birthdate = '$birthdate', ";

     }

     if (!empty($gender)) {

         $query .= "gender = '$gender', ";

     }

     if (!empty($height)) {

         $query .= "height = '$height', ";

     }

     if (!empty($weight)) {

         $query .= "weight = '$weight', ";

     }

     if (!empty($job)) {

      $query .= "job= '$job', ";

      }

    if (!empty($instagram)) {

      $instagram = mysqli_real_escape_string($conn, $instagram);

        $query .= "instagram = '$instagram', ";

    }

    if (!empty($facebook)) {

      $facebook = mysqli_real_escape_string($conn, $facebook);

        $query .= "facebook = '$facebook', ";

    }

    if (!empty($linkedin)) {

      $linkedin = mysqli_real_escape_string($conn, $linkedin);

        $query .= "linkedin = '$linkedin', ";

    }

     $query = rtrim($query, ", "); // remove the trailing comma and space

     $query .= " WHERE user_id ='$user_id'";

     $result = mysqli_query($conn, $query);



     // check if the query was successful

     if ($result) {

         echo "Record updated successfully";

     } else {

         echo "Error updating record: " . mysqli_error($conn);

     }

}

     

// insert profile picture to talents table

if (!empty($profile_picture)) {

  $query = "UPDATE talents SET profile_picture = ? WHERE user_id = ?";

  $stmt = mysqli_prepare($conn, $query);

  mysqli_stmt_bind_param($stmt, "si", $profile_pic_data, $user_id);



  // Check if $profile_pic_data is not empty

  if (!empty($profile_pic_data)) {

    mysqli_stmt_execute($stmt);

    mysqli_stmt_close($stmt);

  }

}

     

 

 // insert background picture to talents table

if (!empty($background_picture)) {

  $query = "UPDATE talents SET background_pic = ? WHERE user_id = ?";

  $stmt = mysqli_prepare($conn, $query);

  mysqli_stmt_bind_param($stmt, "si", $bg_pic_data, $user_id);

  mysqli_stmt_execute($stmt);

  mysqli_stmt_close($stmt);

}

 

// insert images to images table

if (!empty($_FILES['photo']) &&isset($_FILES['photo']) && is_array($_FILES['photo']['tmp_name']) && count($_FILES['photo']['tmp_name']) > 0) {

  $photos = $_FILES['photo'];

  $num_photos = count($photos['tmp_name']);

  $query = "INSERT INTO images (user_id, name, data) VALUES (?, ?, ?)";

  $stmt = mysqli_prepare($conn, $query);



  for ($i = 0; $i < $num_photos; $i++) {

      $name = $photos['name'][$i];

      if (!empty($photos['tmp_name'][$i])) {



        $data = file_get_contents($photos['tmp_name'][$i]);

        

      mysqli_stmt_bind_param($stmt, "iss", $user_id, $name, $data);

      mysqli_stmt_execute($stmt);



      // check if the query was successful

      if (mysqli_stmt_affected_rows($stmt) > 0) {

          echo "Record updated successfully";

      } else {

          echo "Error updating record: " . mysqli_error($conn);

      }



    } else {

        // handle the case when $photos['tmp_name'][$i] is empty

    }

    

      

  }

}





// Insert videos into the videos table

if (isset($_FILES['video'])) {

  $videos = $_FILES['video'];

  $query = "INSERT INTO videos (user_id, video_path) VALUES (?, ?)"; 

  $stmt = mysqli_prepare($conn, $query);



  for ($i = 0; $i < count($videos['name']); $i++) {

      $video_name = $videos['name'][$i];

      $video_tmp_name = $videos['tmp_name'][$i];

      $video_link = '../profile/videos/' . $video_name;



      if (move_uploaded_file($video_tmp_name, $video_link)) {

          mysqli_stmt_bind_param($stmt, "is", $user_id, $video_link);

          if (mysqli_stmt_execute($stmt)) {

              echo "Record updated successfully";

          } else {

              echo "Error updating record: " . mysqli_error($conn);

          }

      } else {

          echo "Error uploading file";

      }

  }

}

 

   





     $sql = "SELECT * FROM users WHERE id='$_SESSION[user_id]'";

     $result = $conn->query($sql);

     if($result->num_rows==1)

     {

       $row = $result->fetch_assoc(); // fetch the row data

       $_SESSION["user_email"] = $row["email"];

       $_SESSION["user_first_name"] = $row["first_name"];

       $_SESSION["user_family_name"] = $row["family_name"];

     }

 

    // redirect the user to their profile page

    header('Location: ../profile/profile.php');

}





?>





<!DOCTYPE html>

<html>

<head>

	<title>Edit Profile</title>

	<link rel="stylesheet" type="text/css" href="edit.css">

	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

	<script>

		$(document).ready(function() {

			var photo_count = 3; // number of photo input fields initially displayed

			var video_count = 2; // number of video input fields initially displayed

			

			$('#add-photo').click(function() { // add new photo input field

				$('#photo-section').append('<label for="photo' + (photo_count+1) + '">Photo ' + (photo_count+1) + ':</label><input type="file" id="photo' + (photo_count+1) + '" name="photo' + (photo_count+1) + '" accept="image/*" required>');

				photo_count++;

			});

			

			$('#add-video').click(function() { // add new video input field

				$('#video-section').append('<label for="video' + (video_count+1) + '">Video ' + (video_count+1) + ':</label><input type="file" id="video' + (video_count+1) + '" name="video' + (video_count+1) + '" accept="video/*" required>');

				video_count++;

			});



			let titleIndex = 1;



function cloneTitleDescSection() {

  const newTitleDescSection = document.querySelector('.title-desc-section').cloneNode(true);

  const titleInput = newTitleDescSection.querySelector('.title-input');

  const descTextarea = newTitleDescSection.querySelector('textarea');

  const addTitleButton = newTitleDescSection.querySelector('.add-title');

  const removeTitleButton = newTitleDescSection.querySelector('.remove-title');



  titleIndex++;

  titleInput.id = `title${titleIndex}`;

  titleInput.value = '';

  descTextarea.id = `desc${titleIndex}`;

  descTextarea.value = '';

  addTitleButton.disabled = true;

  removeTitleButton.disabled = false;



  return newTitleDescSection;

}



function onAddTitleClick() {

  const aboutMeSection = document.querySelector('#about-me-section');

  const newTitleDescSection = cloneTitleDescSection();

  aboutMeSection.appendChild(newTitleDescSection);

}



function onRemoveTitleClick(event) {

  const titleDescSection = event.target.closest('.title-desc-section');

  titleDescSection.remove();



  const aboutMeSection = document.querySelector('#about-me-section');

  const titleDescSections = aboutMeSection.querySelectorAll('.title-desc-section');

  const hasMultipleSections = titleDescSections.length > 1;



  titleDescSections.forEach((titleDescSection, index) => {

    const titleInput = titleDescSection.querySelector('.title-input');

    const descTextarea = titleDescSection.querySelector('textarea');

    const addTitleButton = titleDescSection.querySelector('.add-title');

    const removeTitleButton = titleDescSection.querySelector('.remove-title');

    

    const titleNumber = index + 1;

    titleDescSection.querySelector('.title-label').setAttribute('for', `title${titleNumber}`);

    titleInput.id = `title${titleNumber}`;

    descTextarea.id = `desc${titleNumber}`;

    addTitleButton.disabled = false;

    removeTitleButton.disabled = !hasMultipleSections;

  });



  titleIndex--;

}



document.addEventListener('click', (event) => {

  if (event.target.matches('.add-title')) {

    onAddTitleClick();

  } else if (event.target.matches('.remove-title')) {

    onRemoveTitleClick(event);

  }

});



		});

	</script>

</head>

<body>



<div class="container">

		<h1>Edit Profile</h1>



		<form action="<?php $_SERVER['PHP_SELF'];  ?>"  method="post" enctype="multipart/form-data">

			<section>

				<h2>Personal Information</h2>

				<label for="fname">First Name :</label>

				<input type="text" id="fname" name="fname" >



        <label for="lname">Last Name :</label>

				<input type="text" id="lname" name="lname" >



        <label for="password">Password :</label>

				<input type="password" id="password" name="password" >



        <a href="add_skills_and_experiences.php" class="button">Add Skills and Experiences</a>

      



        <label for="gender" class="gender-label">Gender:</label>

<select id="gender" name="gender">

  <option value="male">Male</option>

  <option value="female">Female</option>

</select>



<label for="job">Job (required) :</label>

		<select name="job" id="job" >

		<option value="">--Select--</option>

		<option value="Actor">Actor</option>

		<option value="Model">Model</option>

		<option value="Singer">Singer</option>

		<option value="director">director</option>

		<option value="Dancer">Dancer</option>

		<option value="Photographer">Photographer</option>

		<option value="Other">Other</option>

	    </select>



      <div id="other-job" style="display:none;">

    <label for="other-job-text">Please specify:</label>

    <input type="text" name="other-job-text" id="other-job-text">

</div>



<script>

    // Show the "other" input field if "Other" option is selected

    document.getElementById("job").addEventListener("change", function() {

        var otherJobField = document.getElementById("other-job");

        if (this.value === "Other") {

            otherJobField.style.display = "block";

            otherJobField.getElementsByTagName("input")[0].setAttribute("required", "required");

        } else {

            otherJobField.style.display = "none";

            otherJobField.getElementsByTagName("input")[0].removeAttribute("required");

        }

    });

</script>



    <label for="height">Height:</label>

    <input type="number" id="height" name="height">



    <label for="weight">Weight:</label>

    <input type="number" id="weight" name="weight">



        <label for="location">location :</label>

				<input type="text" id="location" name="location" >



        <label for="birthdate">birthdate :</label>

				<input type="date" id="birthdate" name="birthdate" >

       



				<label for="description">Description:</label>

				<textarea id="description" name="description" ></textarea>

				<label for="profile-pic">Profile Picture:</label>

				<input type="file" id="profile-pic" name="profile-pic" accept="image/*" >

				<label for="bg-pic">Background Picture:</label>

				<input type="file" id="bg-pic" name="bg-pic" accept="image/*" >

			</section>



      <!--

			<section>

                <h2>About Me</h2>

                <div id="about-me-section">

                <div class="title-desc-section">

                <div class="title-section">

                <label for="title1" class="title-label">Title:</label>

                <input type="text" id="title1" name="title[]" class="title-input">

                </div>



                <div class="desc-section">

                <label for="desc1">Description:</label>

                <textarea id="desc1" name="desc[]" ></textarea>

                </div>



                <div class="button-section">

                 <input type="button" value="Add Title" class="add-title">

                  <input type="button" value="Remove Title" class="remove-title" disabled>

                </div>

                </div>

                </div>

            </section>





			<section>

  -->

    



				<h2>Photos</h2>

				

				<input type="file" id="photo" name="photo[]" accept="image/*" multiple >

			</section>

			<section>

				<h2>Videos</h2>

				

				<input type="file" id="video" name="video[]" accept="video/*" multiple >

			</section>

			<section>

				<h2>Contact</h2>

				<label for="instagram">Instagram:</label>

				<input type="text" id="instagram" name="instagram">

				<label for="facebook">Facebook:</label>

				<input type="text" id="facebook" name="facebook">

				<label for="linkedin">LinkedIn:</label>

				<input type="text" id="linkedin" name="linkedin">

				<label for="phone">Phone:</label>

				<input type="text" id="phone" name="phone" placeholder="without starting 0" pattern="^[0-9]{10}$">

				<label for="email">Email:</label>

				<input type="email" id="email" name="email" >

			</section>

			<input type="submit" name="edit_profile" value="Save Changes">

		</form>

	</div>







  





</body>

</html>





