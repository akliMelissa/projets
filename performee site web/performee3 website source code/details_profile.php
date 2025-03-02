<?php
session_start();
include 'bd_connect.php';

// Check if user is authenticated
if (!isset($_SESSION['user_id'])) {
    // Redirect to login page if user is not authenticated
    header("Location: login.php");
    exit();
}

$user_id = $_SESSION['user_id'];

// Check if the user has submitted the form
if (isset($_POST['submit'])) {
    $birthdate = mysqli_real_escape_string($conn, $_POST['birthdate']);
    $gender = mysqli_real_escape_string($conn, $_POST['gender']);
    $location = mysqli_real_escape_string($conn, $_POST['location']);
    $phone_number = mysqli_real_escape_string($conn, $_POST['phone_number']);
	$job = mysqli_real_escape_string($conn, $_POST['job']);

    // If phone_number, location, and skills are empty, set them to NULL
    $phone_number = !empty($phone_number) ? "'$phone_number'" : "NULL";
    $location = !empty($location) ? "'$location'" : "NULL";
   
	if ($job === "Other") {
        $job = mysqli_real_escape_string($conn, $_POST['other-job-text']);
    }
    // Set default profile and background pictures
    $default_picture = file_get_contents("def_profile_pic.png");
    $default_background = file_get_contents("default_profile_pic.png");

// Insert user information into talents table
$sql = "INSERT INTO talents (user_id, birthdate, gender, location, phone_number, profile_picture, background_pic,job)
        VALUES (?, ?, ?, ?, ?, ?, ?,?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("isssssss", $user_id, $birthdate, $gender, $location, $phone_number, $default_picture, $default_background,$job);
if ($stmt->execute()) {
    // Redirect to the profile page
    header("Location: index.php?id=$user_id");
    exit();
} else {
    $_SESSION["capture_info_error"] = "Error: " . $sql . "<br>" . $conn->error;
}
}


// Function to sanitize form input
function test_input($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Capture User Information</title>
	<style>
		.error {
			color: red;
		}
	</style>
	<link rel="stylesheet" type="text/css" href="details_profile.css">
	<link href="https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700,800,900" rel="stylesheet">
</head>
<body>
	<!-- HTML form for capturing missing user information -->
	
	<form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">
	    <?php
	    if (isset($_SESSION["capture_info_error"])) {
	        echo '<div class="error">' . $_SESSION["capture_info_error"] . '</div>';
	        unset($_SESSION["capture_info_error"]);
	    }
	    ?>
		<h1> To Share Your Talent With Us  </h1>
	    <label for="birthdate">Birthdate (required) :</label>
	    <input type="date" name="birthdate" id="birthdate" required>

	    <label for="gender">Gender (required) :</label>
	    <select name="gender" id="gender" required>
	        <option value="">--Select--</option>
	        <option value="M">Male</option>
	        <option value="F">Female</option>
	        <option value="O">Other</option>
	    </select>

		<label for="job">Job (required) :</label>
		<select name="job" id="job" required>
		<option value="">--Select--</option>
		<option value="Actor">Actor</option>
		<option value="Model">Model</option>
		<option value="Singer">Singer</option>
		<option value="director">director</option>
		<option value="Dancer">Dancer</option>
		<option value="TV host">TV host</option>
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

	    <label for="location">Location:</label>
	    <input type="text" name="location" id="location" placeholder="Enter your location">

	    <label for="phone_number">Phone Number:</label>
	    <input type="tel" name="phone_number" id="phone_number" placeholder="Enter your phone number" >


	    <button type="submit" name="submit">Submit</button>
		<a href="details_recruiter.php" >I'm looking for a talent</a>
</body>
	</form>
	
</html>