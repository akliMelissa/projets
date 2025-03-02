
67
Mail
Chat
Spaces
Meet
Compose
Labels
Inbox
67
Starred
Snoozed
Sent
Drafts
14
More
 
Labels
Labels

1 of 2,284


Print all
In new window
performee
Inbox

Saib, Lyna (Student)
Attachments
3:03 PM (4 minutes ago)

to me


5
 Attachments
  •  Scanned by Gmail

 
 
Reply
Forward
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

// Delete the profile
if (isset($_POST['delete_profile'])) {
    // Delete the user's data from the database
    $sql = "DELETE FROM users WHERE id='$user_id'";
    $result = mysqli_query($conn, $sql);

    if ($result) {
        // Profile deleted successfully, redirect to index.php
        header("Location: login.php");
        exit();
    } else {
        echo "<p>Error deleting profile: " . mysqli_error($conn) . "</p>";
    }
}

?>

<!DOCTYPE html>
<html>
<head>
    <title>Settings & Privacy</title>
    <style>
        body {
          
        background: url("images/images.jpg");
        }
        .container {
            margin: auto;
            margin-top:3%;
            width: 80%;
            padding: 20px;
        background-color: #808076;
            box-shadow: 0px 0px 10px #888888;
            border-radius: 5px;
        }
        h1 {
            text-align: center;
            margin-bottom: 30px;
        }
        label {
            display: block;
            margin-bottom: 10px;
            font-weight: bold;
        }
        input[type="text"],
        input[type="password"],
        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            margin-bottom: 20px;
            box-sizing: border-box;
            font-size: 16px;
        }
        button[type="submit"],
        button[type="button"] {
        background-color: #B87333;
            color: white;;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            margin-right: 10px;
        }
        button[type="submit"]:hover,
        button[type="button"]:hover {
            background-color:#B87310 ;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Settings & Privacy</h1>
        <form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">
            <label for="fname">First Name</label>
            <input type="text" name="fname" id="fname" required>
            <label for="lname">Last Name</label>
            <input type="text" name="lname" id="lname" required>
            <label for="email">Email</label>
            <input type="text" name="email" id="email" required>
            <label for="password">Password</label>
            <input type="password" name="password" id="password" required>
            <button type="submit">Save Changes</button>
            <button type="button" onclick="deleteProfile()">Delete Profile</button>
        </form>
        <script>
            function deleteProfile() {
                if (confirm("Are you sure you want to delete your profile?")) {
                    // Submit the form to delete the profile
                    document.getElementById("deleteForm").submit();
                }
            }
        </script>
        <?php
            if ($_SERVER["REQUEST_METHOD"] == "POST") {
                $fname = $_POST["fname"];
                $lname = $_POST["lname"];
                $email = $_POST["email"];
                $password = $_POST["password"];

                // Escape special characters to prevent SQL injection
                $fname = mysqli_real_escape_string($conn, $fname);
                $lname = mysqli_real_escape_string($conn, $lname);
                $email = mysqli_real_escape_string($conn, $email);
                $password = mysqli_real_escape_string($conn, $password);

                // Update the user's data in the database
                $sql = "UPDATE users SET first_name='$fname', family_name='$lname', email='$email', password='$password' WHERE id='$user_id'";
                $result = mysqli_query($conn, $sql);

                if ($result) {
                    echo "<p>Settings updated successfully!</p>";
                } else {
                    
                }

                // Close the database connection
                mysqli_close($conn);
            }
        ?>

        <!-- Delete Profile Form -->
        <form id="deleteForm" method="post">
            <input type="hidden" name="delete_profile" value="1">
        </form>
    </div>
</body>
</html>