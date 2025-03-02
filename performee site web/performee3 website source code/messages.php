<?php
session_start();
include("bd_connect.php");

if (!isset($_SESSION['user_id']) || !isset($_SESSION["user_first_name"]) || !isset($_SESSION["user_family_name"])) {
    // Redirect to login page if user is not authenticated
    header("Location: login.php");
    exit();
}

// Retrieve the current session ID
$current_user_id = $_SESSION['user_id'];

// Pagination variables
$page = isset($_GET['page']) ? $_GET['page'] : 1;
$records_per_page = 10;
$offset = ($page - 1) * $records_per_page;

// Check if a search query is submitted
if (isset($_GET['search'])) {
    $search = $_GET['search'];

    // Modify the SQL query to include the search condition
    $sql = "SELECT * FROM messages join users on messages.sender_id=users.id  WHERE receiver_id = $current_user_id AND (users.first_name LIKE '%$search%' OR users.family_name LIKE '%$search%') ORDER BY timestamp DESC LIMIT $offset, $records_per_page";
    $result = mysqli_query($conn, $sql);
} else {
    // Retrieve all the messages where the receiver ID is equal to the current user ID
    $sql = "SELECT * FROM messages join users on messages.sender_id=users.id WHERE receiver_id = $current_user_id ORDER BY timestamp DESC LIMIT $offset, $records_per_page";
    $result = mysqli_query($conn, $sql);
}


?>

<!DOCTYPE html>
<html>

<head>
    <title>Messages</title>
    <style>
        body {
            background-image: url('images/images.jpg');
        }

        .message-list {
            background-color: black;
            border: 1px solid #CCCCCC;
            padding: 10px;
            margin-bottom: 10px;
            border-radius: 10px;
            width: 70%;
            margin: 0 auto;
        }

        .message-list .message {
            margin-bottom: 10px;
            padding-bottom: 10px;
            border-bottom: 1px solid #CCCCCC;
        }

        .read-message {
            background-color: #F0F0F0;
        }

        .unread-message {
            background-color: #E6EFFF;
        }

        .no-messages {
            text-align: center;
            padding: 10px;
            background-color: #FFFFFF;
            border: 1px solid #CCCCCC;
            margin-bottom: 10px;
            border-radius: 10px;
            width: 70%;
            margin: 0 auto;
        }

        .pagination {
            text-align: center;
            margin-top: 10px;
        }



        .reply-button {
            text-align: center;
            margin-top: 10px;
        }
        .search{
            margin-bottom: 08%;
        }

        .search input {
            width: 500px;
            border: 3px solid khaki;
            padding: 17px;
            border-radius: 40px;
            margin-top: 40px;
            margin-left: 23%;
            

        }
        input[type="submit"] {
        margin-top: 20px;
        background-color: #b87333;
        color: #ffffff;
        border: none;
        border-radius: 05%;
        padding: 10px 20px;
        font-size: 16px;
        cursor: pointer;
        width:10%;
        margin-left:01%;
    }

        
    </style>
    <script>
        function showMessage(messageId) {
            // Redirect to the message content page
            window.location.href = 'get_message_content.php?message_id=' + messageId;
        }
    </script>
</head>

<body>
    <h1>Messages</h1>
    <div class="search">
        <form action="messages.php" method="GET">
            <input type="text" name="search" placeholder="Search by sender name">
            <input type="submit" value="Search">
        </form>
    </div>
    <?php

    // Check if a message has been clicked
    if (isset($_GET['message_id'])) {
        $message_id = $_GET['message_id'];

        // Redirect to the message content page
        header("Location: get_message_content.php?message_id=" . $message_id);
        exit();
    }

    // Display the list of messages
    if (mysqli_num_rows($result) > 0) {
    ?>
        <div class="message-list">
            <?php
            while ($row = mysqli_fetch_assoc($result)) {
                $message_id = $row['message_id'];
                $sender_id = $row['sender_id'];
                $subject = $row['subject'];
                $date = $row['timestamp'];
                $is_read = $row['is_read'];

                // Style for read and unread messages
                $message_class = ($is_read == 1) ? "read-message" : "unread-message";
            ?>
                <div class="message <?php echo $message_class; ?>">
                    <h4><a href="#" onclick="showMessage(<?php echo $message_id; ?>); return false;"><?php echo $subject; ?></a></h4>
                    <p>From: <?php echo $row['first_name'].' '.$row['family_name']; ?></p>
                    <p>Date: <?php echo $date; ?></p>
                </div>
            <?php
            }
            ?>
        </div>
    <?php
    } else {
    ?>
        <div class="no-messages">
            <p>No messages found.</p>
        </div>
    <?php
    }

    // Calculate the total number of messages
    $sql = "SELECT COUNT(*) AS total FROM messages WHERE receiver_id = $current_user_id";
    $result = mysqli_query($conn, $sql);
    $row = mysqli_fetch_assoc($result);
    $total_messages = $row['total'];

    // Calculate the total number of pages
    $total_pages = ceil($total_messages / $records_per_page);

    ?>

    <div class="pagination">
        <?php
        // Display the pagination links
        for ($i = 1; $i <= $total_pages; $i++) {
            echo "<a href='messages.php?page=" . $i . "'>" . $i . "</a> ";
        }
        ?>
    </div>




    <div id="modal" style="display: none;">

        <div class="modal-content">
            <span class="close" onclick="hideMessage()">&times;</span>
            <h2><?php echo $subject; ?></h2>
            <p>From: <?php echo $row['first_name'].' '.$row['family_name']; ?></p>
            <p>Date: <?php echo $date; ?></p>
            <p><?php echo $message; ?></p>
            <div class="reply-button">
                <button onclick="location.href='profile/other_users_profile.php?id=<?php echo $sender_id; ?>'">Reply</button>
            </div>
        </div>
        <button onclick="hideMessage()">Close</button>
    </div>
    </div>

</body>

</html>