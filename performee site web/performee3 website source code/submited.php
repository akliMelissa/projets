<?php

session_start(); 
include("bd_connect.php");

// Include the PHPMailer library
require 'phpmailer/includes/PHPMailer.php';
require 'phpmailer/includes/SMTP.php';
require 'phpmailer/includes/Exception.php';


use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;



//sing up part -------------------------------------------------------------------------

if(isset($_POST['signup_submit'])&& $_SERVER["REQUEST_METHOD"] == "POST"){

if (isset($_POST['first_name']) && isset($_POST['family_name']) && isset($_POST['email']) && isset($_POST['password'])) {
   

// Handling form submission

    $first_name = test_input($_POST["first_name"]);
    $family_name = test_input($_POST["family_name"]);
    $email = test_input($_POST["email"]);
    $password = test_input($_POST["password"]);
    $created_at = date('Y-m-d H:i:s'); // Getting the current date and time
    
    // Validating form fields
    $error = false;
    if (empty($first_name) || empty($family_name) || empty($email) || empty($password)) {
        $_SESSION["signup_error"]  = "Please fill in all fields.";
        $error = true;
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $_SESSION["signup_error"]  = "Please enter a valid email address.";
        $error = true;
    } elseif (strlen($password) < 8) {
        $_SESSION["signup_error"]  = "Password must be at least 8 characters long.";
        $error = true;
    }
    
    // Checking if email already exists in the database
    $sql = "SELECT * FROM users WHERE email='$email'";
    $result = $conn->query($sql);
    if ($result->num_rows > 0) {
        $_SESSION["signup_error"]  = "Email already exists. Please choose a different email.";
        $error = true;
    }

      // Step 4: Hash the user's password
      $hashed_password = password_hash($password, PASSWORD_DEFAULT);
      $activation_code = md5(uniqid(rand(), true)); // Generate a unique activation code

    
    // If there are no errors, insert user information into the database
    if (!$error) {
       // $sql = "INSERT INTO users (first_name, family_name, email, password, created_at)
               // VALUES ('$first_name', '$family_name', '$email', '$hashed_password', '$created_at')";
       
        $sql = "INSERT INTO users (first_name, family_name, email, password, created_at, activation_code)
        VALUES ('$first_name', '$family_name', '$email', '$hashed_password', '$created_at', '$activation_code')";
             
            
       
               if ($conn->query($sql) === TRUE) {
                $user_id = mysqli_insert_id($conn);
/*                
                $to = $email;
                $subject = "Activate Your Account";
                $message = "Thank you for signing up! Please click the following link to activate your account:\r\n";
                $message .= "http://example.com/activate.php?email=".urlencode($email)."&code=$activation_code";
                $headers = "From: performee@example.com" . "\r\n" .
                           "Reply-To: performee@example.com" . "\r\n" .
                           "X-Mailer: PHP/" . phpversion();
               // mail($to, $subject, $message, $headers); // Send the activation email

         */      


// Create a new PHPMailer object
$mail = new PHPMailer;

// Set the email parameters
$mail->isSMTP();
$mail->Host = 'smtp.gmail.com';
$mail->SMTPAuth = true;
$mail->Username = 'boumahdimanel3@gmail.com';
$mail->Password = 'manel2003';
$mail->SMTPSecure = 'tls';
$mail->Port = 587;

// Set the email content
$mail->setFrom('boumahdimanel3@gmail.com', 'manel boumahdi');
$mail->addAddress($email);
$mail->Subject = 'Activate Your Account';
$mail->Body = "Thank you for signing up! Please click the following link to activate your account:\r\n";
$mail->Body .= "http://example.com/activate.php?email=".urlencode($email)."&code=$activation_code";

// Send the email
if (!$mail->send()) {
    echo 'Mailer Error: ' . $mail->ErrorInfo;
} else {
    echo 'Message sent!';
}

                
                

           //modify here later to display the messages !!!!!!!!!
            $_SESSION["success"] = "You have successfully signed up.";

              // Pass the user ID as a parameter to the details_profile.php page
              $_SESSION["user_id"] = $user_id ;
              $_SESSION["user_email"] = $email;
              $_SESSION["user_first_name"] = $first_name;
              $_SESSION["user_family_name"] = $family_name;
              


             header("Location: details_profile.php");
           
            exit();
        } else {
            $_SESSION["signup_error"]  = "Error: " . $sql . "<br>" . $conn->error;
        }
    }
}
}

// Function to sanitize form input
function test_input($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}



//Modify the login process to check if the user has activated their account 
//before allowing them to log in.
// You can add the following code before redirecting to the welcome page:
 // add this code later !!!!!
 /*if (!$error) {
    $sql = "SELECT * FROM users WHERE email='$email'";
    $result = $conn->query($sql);
    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        if ($user['activation_code'] == '') {
            $_SESSION["success"] = "You have successfully signed up and activated your account.";
            header("Location: user_home.php"); // Redirecting to the welcome page
            exit();
        } else {
            $_SESSION["signup_error"]  = "Please activate your account before logging in.";
        }
    }
}

*/













//sign in part ----------------------------------------------------------------------



// Handling form submission
if (isset($_POST['signin_submit'])&& $_SERVER["REQUEST_METHOD"] == "POST") {
    $email = test_input($_POST["email"]);
    $password = test_input($_POST["password"]);
    
    // Validating form fields
    $error = false;
    if (empty($email) || empty($password)) {
        $_SESSION["signin_error"] = "Please enter your email and password.";
        $error = true;
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $_SESSION["signin_error"]= "Please enter a valid email address.";
        $error = true;
    }
    
    // Checking if email exists in the database
    $sql = "SELECT * FROM users WHERE email='$email'";
    $result = $conn->query($sql);
    if ($result->num_rows == 0) {
        $_SESSION["signin_error"]= "Email not found. Please try again or sign up.";
        $error = true;
    } else {
        $row = $result->fetch_assoc();
        $hashed_password = $row["password"];
        if (!password_verify($password, $hashed_password)) {
            $_SESSION["signin_error"]= "Incorrect password. Please try again.";
            $error = true;
        }
    }
    
    // If there are no errors, store user information in session and redirect to the welcome page
    if (!$error) {
        $_SESSION["user_id"] = $row["id"];
        $_SESSION["user_email"] = $row["email"];
        $_SESSION["user_first_name"] = $row["first_name"];
        $_SESSION["user_family_name"] = $row["family_name"];

        
        $id=$row["id"];
        $sql = "SELECT * FROM users WHERE id='$id'";
        $result = $conn->query($sql);
        if ($result->num_rows == 0) {
        header("Location: details_profile.php"); // Redirecting to the welcome page
        exit();}
        else{
            header("Location: index.php"); 
        }
    }
}











/*

// Sign up function

function signup($first_name,$family_name,$email, $password) {
    global $conn;

    // Step 3: Check if the email already exists in the database
    $sql = "SELECT email FROM Users WHERE email = '$email'";
    $result = $conn->query($sql);

    
    if ($result->num_rows > 0) {
         
        $result="<div style='color:red; font-size:12px; text_align:left;'>Email already exists!</div>";
        echo '<script>  window.location.href = "login.php"; 
            alert("Email already exists!")
             </script>';
    }
    else{
      

        // Step 5: Insert the user's data into the "Users" table
        $sql = "INSERT INTO Users (first_name, family_name, email, password, created_at) VALUES ('$first_name', '$family_name', '$email', '$hashed_password','$created_at')";

        if ($conn->query($sql) == TRUE) {
            return true;

        } else {
            echo "Error: " . $sql . "<br>" . $conn->error;
            return false ; 
        }
    }
} 


 

//main code 

    // Step 2: Validate user input
if (!empty($_POST['first_name']) && !empty($_POST['family_name']) && !empty($_POST['email']) && !empty($_POST['password'])) {
    $first_name = $_POST['first_name'];
    $family_name = $_POST['family_name'];
    $email = filter_var($_POST['email'], FILTER_SANITIZE_EMAIL);
    $password = $_POST['password'];

// Sign up
if (signup($first_name,$family_name,$email, $password)) {
    header("Location:user_home.php");
} 
else {
    echo '<script>
    window.location.href = "login.php";
    alert("Sign up failed, try again")
    </script>';
}
}

else {
    echo "<script>alert('Please fill all the fields!')</script>"; 
}  
    
}
}

*/


/*


// Login function
function login($email, $password) {
    global $conn;
    $sql = "SELECT * FROM users WHERE email = '$email'";
    $result = mysqli_query($conn, $sql);
    if (mysqli_num_rows($result) == 1) {
        $row = mysqli_fetch_assoc($result);
        if (password_verify($password, $row['password'])) {
            $_SESSION["user_id"]=$row['user_id'];
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
}



if(isset($_POST['login_submit'])){

    
$email = $_POST['email'];
$password = $_POST['password'];


if (login($email, $password)) {
    header("Location:user_home.php");
} 

else {
    echo '<script>
    window.location.href = "../../login.php";
    alert("Login failed")
    </script>';
}
    
}


 

/*

if(isset($_POST['tasjil_submit'])){
    $ism = $_POST['ism'];
    $laqab = $_POST['laqab'];
    $mostakhdim = $_POST['mostakhdim'];
    $kalimat_sir = $_POST['kalimat_sir'];
    $Re_kalimat_sir = $_POST['Re_kalimat_sir'];

    if($kalimat_sir == $Re_kalimat_sir){
        $sql = "INSERT INTO `mostakhdim`( `ism`, `laqab`, `mostakhdim`, `kalimat_sir`) VALUES ('$ism','$laqab','$mostakhdim','$kalimat_sir')";
        $result = mysqli_query($conn, $sql);
        if(isset($result)){
            echo'<h1>fvokdfjbnfdbjfn</h1>';
        }
        else{
            echo '<script>
            window.location.href = "../../index.php";
            alert("Registration failed ta3 connection")
            </script>';
        }

    }
    else{
        echo '<script>
        window.location.href = "../../index.php";
        alert("Registration failed")
        </script>';
    }
}

if(isset($_POST['tasjil_safeha_submit'])){
    $ism_safeha = $_POST['ism_safeha'];
    $mostakhdim_safeha = $_POST['mostakhdim_safeha'];
    $kalimat_sir_safeha = $_POST['kalimat_sir_safeha'];
    $Re_kalimat_sir_safeha = $_POST['Re_kalimat_sir_safeha'];

    if($kalimat_sir_safeha == $Re_kalimat_sir_safeha){
        $sql = "INSERT INTO `safeha`(`ism_safeha`, `mostakhdim_safeha`, `kalimat_sir_safeha`) VALUES ('$ism_safeha','$mostakhdim_safeha','$kalimat_sir_safeha')";
        $result = mysqli_query($conn, $sql);
        if(isset($result)){
            echo'<h1>mostakhdim_safeha</h1>';
        }
        else{
            echo '<script>
            window.location.href = "../../index.php";
            alert("Registration failed connection")
            </script>';
        }

    }
    else{
        echo '<script>
        window.location.href = "../../index.php";
        alert("Registration failed")
        </script>';
    }
}


*/

?>