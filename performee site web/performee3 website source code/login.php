<?php
include("submited.php") ;
?>




<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/login.css">

</head>

<body>



   
<div class="container" id="container">
	<div class="form-container sign-up-container">

		<form action="<?php $_SERVER['PHP_SELF'];  ?> " method="POST">
			<h1 id="createaccount">Create Account</h1>
			<div class="social-container">
				<!--<a href="#" class="social"><i class="fab fa-facebook-f"></i></a>
				<a href="#" class="social"><i class="fab fa-google-plus-g"></i></a>
				<a href="#" class="social"><i class="fab fa-linkedin-in"></i></a>-->
			</div>
			<a href="#">Use your email for registration</a>

			<?php   
			if (isset($_SESSION["signup_error"] )) {  
				 echo "<div style='color:red; font_size:10px;'>". $_SESSION["signup_error"] ."</div>";
				 unset($_SESSION["signup_error"] );
                 }     
			?>

			<input type="text" name="first_name" placeholder="First Name" />
			<input type="text" name="family_name" placeholder="Last Name" />
			<input type="email" name="email" placeholder="Email" />
			<input type="password" name="password" placeholder="Password" />
			<button type="submit" name="signup_submit" >Sign Up</button>
			

		</form>
   </div>
	<div class="form-container sign-in-container">

		<form action="<?php $_SERVER['PHP_SELF']; ?>" method="POST">
			<h1 id="createaccount">Log in</h1>
			<?php
if (isset($_SESSION["signin_error"])) {
	echo "<div style='color:red;'>". $_SESSION["signin_error"] ."</div>";
    unset($_SESSION["signin_error"] );
}
?>
			
			<a href="#">use your account</a>
			<input type="email" name="email" placeholder="Email" />
			<input type="password" name="password" placeholder="Password" />
			<a href="javascript:void(0)" onclick="forgotPassword()">Forgot your password?</a>
			<button name="signin_submit">Sign In</button>
		</form>
	</div>
	<div class="overlay-container">
		<div class="overlay">
			<div class="overlay-panel overlay-left">
				<h1>Welcome Back!</h1>
				<p>To keep connected with us please login with your personal info</p>
				<button class="ghost" id="signIn">Sign In</button>
			</div>
			<div class="overlay-panel overlay-right">
				<h1>Hello, Performer!</h1>
				<p>Enter your personal details and start journey with us</p>
				<button class="ghost" id="signUp">Sign Up</button>
			</div>
		</div>
	</div>
 </div>





   <script src="login.js"></script>

<script>
	function forgotPassword() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            // success
            console.log(xhr.responseText);
        }
    };
    xhr.open('POST', 'forgot_password.php', true);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send('forgot_password=1');
}
</script>

<?php
if(isset($_POST['forgot_password'])) {
    // Get user input
    $email = $_POST['email'];
    // Generate a unique activation code
    $activation_code = md5(uniqid(rand(), true));
    // Store activation code in database
    $query = "UPDATE users SET activation_code = '$activation_code' WHERE email = '$email'";
    // Execute the query
    // Send password reset email with activation link
    $to = $email;
    $subject = "Password Reset Request";
    $message = "Hello,\n\nWe received a request to reset your password. Please click the link below to reset your password:\n\n";
    $message .= "http://example.com/reset_password.php?email=" . urlencode($email) . "&code=" . urlencode($activation_code) . "\n\n";
    $message .= "If you did not make this request, please ignore this email.\n\n";
    $message .= "Best regards,\nYour Company";
    $headers = "From: Your Company <noreply@example.com>\r\n";
    $headers .= "Reply-To: noreply@example.com\r\n";
    $headers .= "Content-type: text/plain; charset=UTF-8\r\n";
    mail($to, $subject, $message, $headers);
}
?>



 
   

</body>


</html>