<?php
session_start();
include ("bd_connect.php");
error_reporting(E_ALL);
ini_set('display_errors', 1);
// Check if user is authenticated
if (!isset($_SESSION['user_id'])) {
    // Redirect to login page if user is not authenticated
    header("Location: login.php");
    exit();
}


$user_id = $_SESSION['user_id'];

// Check if the user has submitted the form
if (isset($_POST['submit'])) {
    $location = mysqli_real_escape_string($conn, $_POST['location']);
    $phone_number = mysqli_real_escape_string($conn, $_POST['phone_number']);
	$job = mysqli_real_escape_string($conn, $_POST['job']);
    $company_name=mysqli_real_escape_string($conn, $_POST['company_name']);
    $email = mysqli_real_escape_string($conn, $_POST['email']);

    // If phone_number, location, and skills are empty, set them to NULL
    $phone_number = !empty($phone_number) ? "'$phone_number'" : "NULL";
    $location = !empty($location) ? "'$location'" : "NULL";
    $company_name = !empty($company_name) ? "'$company_name'" : "NULL";
   
	if ($job === "Other") {
        $job = mysqli_real_escape_string($conn, $_POST['other-job-text']);
    }
    // Set default profile and background pictures
    $default_picture = file_get_contents("default_profile_pic.png");
   


   
// Insert user information into talents table
$sql = "INSERT INTO recruiters (user_id, company_name,location, phone_number,email,profile_picture,activity)
        VALUES (?, ?, ?, ?, ? ,?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("issssss", $user_id,$company_name, $location, $phone_number, $email,$default_picture,$job);
if ($stmt->execute()) {
    // Redirect to the profile page
    if (strpos($_SERVER['HTTP_REFERER'], 'details_recruiter.php') !== false) {
        // Redirect the user back to the previous page
        header("Location: addjob.php");
    }
   else
   { 
    header("Location: index.php?id=$user_id");
   }
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
		<h1> To help you find talents </h1>
        <label for="company_name">Company Name :</label>
	    <input type="text" name="company_name" id="company_name" placeholder="Enter your company's name">
        <label for="email">Company Email :</label>
	    <input type="text" name="email" id="email" placeholder="Enter your company's email">
        <label for="job"> Activity (required) :</label>
		<select name="job" id="job" required>
		<option value="">--Select--</option>
		<option value="Agency">Agency</option>
		<option value="Production House">Production House</option>
		<option value="Director">Director</option>
		<option value="Event Organizer">Event Organizer</option>
		<option value="Freelancer">Freelancer</option>
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
		
</body>
	</form>
	
</html>