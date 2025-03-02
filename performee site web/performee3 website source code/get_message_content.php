<?php
session_start();
include("bd_connect.php");
error_reporting(E_ALL);
ini_set('display_errors', 1);

if (!isset($_SESSION['user_id']) || !isset($_SESSION["user_first_name"]) || !isset($_SESSION["user_family_name"])) {
  // Redirect to login page if user is not authenticated
  header("Location: login.php");
  exit();
}

// Retrieve the current session ID
$current_user_id = $_SESSION['user_id'];

// Check if a message ID is provided in the query string
if (isset($_GET['message_id'])) {
  $message_id = $_GET['message_id'];

  // Retrieve the message details
  $sql = "SELECT * FROM messages join users on messages.sender_id=users.id WHERE message_id = $message_id AND receiver_id = $current_user_id";
  $result = mysqli_query($conn, $sql);

  // Check if a message was found
  if (mysqli_num_rows($result) > 0) {
    $row = mysqli_fetch_assoc($result);
    $sender_id = $row['sender_id'];
    $subject = $row['subject'];
    $message = $row['message_text'];
    $date = $row['timestamp'];
    $first_name =  $row['first_name'];
    $family_name = $row['family_name'];

    // Mark the message as read
    $sql = "UPDATE messages SET is_read = 1 WHERE message_id = $message_id";
    mysqli_query($conn, $sql);

    // Display the message content
?>
    <!DOCTYPE html>
    <html>

    <head>
      <title>Message</title>
      <style>
        body {
          background-image: url('images/images.jpg');
        }

        .modal {

          position: fixed;
          z-index: 1;
          left: 0;
          top: 0;
          width: 100%;
          height: 100%;
          overflow: auto;
          background-color: rgba(0, 0, 0, 0.4);
        }

        .modal-content {
          background-color: #fefefe;
          margin: 15% auto;
          padding: 20px;
          border: 1px solid #888;
          width: 60%;
          border-radius: 5px;
        }

        .close {
          position: absolute;
          top: 10px;
          right: 20px;
          font-size: 30px;
          font-weight: bold;
          cursor: pointer;
        }

        .message-subject {
          cursor: pointer;
        }

        button {
          margin-top: 20px;
          background-color: #b87333;
          color: #ffffff;
          border: none;
          border-radius: 05%;
          padding: 10px 20px;
          font-size: 16px;
          cursor: pointer;
        }
      </style>
    </head>

    <body>
      <div id="modal" class="modal">
        <div class="modal-content">
          <span style="color:#fefefe" class="close" onclick="hideMessage()">&times;</span>
          <h2><?php echo $subject; ?></h2>
          <p>From: <?php echo $row['first_name'].' '.$row['family_name']; ?></p>
          <p>Date: <?php echo $date; ?></p>
          <p><?php echo $message; ?></p>
          <div class="reply-button">
            <button onclick="window.location.href='profile/other_users_profile.php?id=<?php echo $row['id']; ?>&first_name=<?php echo $row['first_name']; ?>&last_name=<?php echo $row['family_name']; ?>#contact'">Reply</button>
          </div>
        </div>
      </div>
    </body>

    </html>
<?php
  } else {
    echo "Message not found.";
  }
} else {
  echo "Invalid message ID.";
}
?>

<script>
  function hideMessage() {
    // Hide the modal
    document.getElementById('modal').style.display = 'none';
    window.location.href = 'messages.php';

  }
</script>