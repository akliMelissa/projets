<?php
session_start();
include '../bd_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
  $user_id = $_SESSION['user_id'];
  $start_date = $_POST['start_date'];
  $end_date = $_POST['end_date'];
  $experience = $_POST['experience'];

  // Use a prepared statement to insert the experience into the database
  $stmt = $conn->prepare("INSERT INTO experiences (user_id, start_date, end_date, experience) VALUES (?, ?, ?, ?)");
  $stmt->bind_param("isss", $user_id, $start_date, $end_date, $experience);
  if (!$stmt->execute()) {
    // There was an error inserting the experience into the database
    $error = $stmt->error;
    echo 'Error inserting experience: ' . $error;
    exit;
  }

  // Redirect back to the HTML page
  header('Location: add_skills_and_experiences.php');
  exit;
}
?>
