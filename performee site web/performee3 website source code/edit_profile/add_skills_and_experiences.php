
<!DOCTYPE html>
<html>
<head>
  <title>Add Skills and Experiences</title>
  <link rel="stylesheet" type="text/css" href="add_skills_experiences.css">
</head>
<body>

<button class="circle-button" onclick="history.go(-1)">
  <i class="fa fa-arrow-left">Back</i>
</button>
<div class="container">
  <h1> Skills and Experiences</h1>
  
  <!-- Add skill form -->
  <h2>Add Skill</h2>
  <form action="add_skill.php" method="POST">
    <label for="skill_name">Skill:</label>
    <input type="text" id="skill_name" name="skill_name" placeholder="Enter a skill">
    <button type="submit">Add Skill</button>
  </form>
  
  <!-- Add experience form -->
  <h2>Add Experience</h2>
  <form action="add_experience.php" method="POST">
    <label for="start_date">Start Date:</label>
    <input type="date" id="start_date" name="start_date">
    <label for="end_date">End Date:</label>
    <input type="date" id="end_date" name="end_date">
    <label for="experience">Experience:</label>
    <textarea id="experience" name="experience" placeholder="Enter your experience"></textarea>
    <button type="submit">Add Experience</button>
  </form>
</div>
</body>
</html>