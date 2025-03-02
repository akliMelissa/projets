<?php
session_start();
include '../bd_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
  $user_id = $_SESSION['user_id'];
  $skill_name = $_POST['skill_name'];

  // Use a prepared statement to insert the skill into the database
  $stmt = $conn->prepare("INSERT INTO skills (user_id, skill) VALUES (?, ?)");
  $stmt->bind_param("is", $user_id, $skill_name);
  if (!$stmt->execute()) {
    // There was an error inserting the skill into the database
    $error = $stmt->error;
    echo 'Error inserting skill: ' . $error;
    exit;
  }

  // Redirect back to the HTML page
  header('Location: add_skills_and_experiences.php');
  exit;
}
?>