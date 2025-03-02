<!DOCTYPE html>
<html>
<?php include("bd_connect.php");
include("logic.php");
?>

<head>
	<!--title and favicon -->
	<title>Performee</title>
	<link rel="icon" type="image/x-icon" href="images/logofinal.jpg">


	<link rel="stylesheet" type="text/css" href="css/style.css">
	<meta charset="utf-8">
	<meta name="author" content="templatemo">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<link href="https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700,800,900" rel="stylesheet">
	<link rel="stylesheet" href="carousel-07/css/owl.carousel.min.css">
	<link rel="stylesheet" href="carousel-07/css/owl.theme.default.min.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/4.5.6/css/ionicons.min.css">
	<link rel="stylesheet" href="carousel-07/css/style.css">


	<style>
		.categ {

			color: #df9100;
			background: #000000d3;
			text-align: justify;
			display: flex;
			padding: 5px;
			margin-right: 0px;
			margin-left: auto;



		}

		.see-more {
			position: relative;
			text-align: right;
			font-size: 20px;
			left: 90%;
			color: #ffc107;
		}

		.see-more:hover {
			color: white;
		}

		.entete {
			color: #ffffff;
			width: 13%;
			background: gray;
			text-align: justify;
			padding-left: 5px;


		}
		
	</style>
</head>

<body>

	<?php

	session_start();

	// Check if user is authenticated
	if (!isset($_SESSION['user_id'])) {
		// Redirect to login page if user is not authenticated
		include('header.php');
	} else {
		$getid = $_SESSION["user_id"];
		include('user_header.php');
	}
	?>

	<div class="main-banner">
		<video autoplay loop muted plays-inline class="back-video">
			<source src="video/video.mp4" type="video/mp4">
		</video>
		<div class="header-text">

			<div class="col-lg-4">
				<style>
					@import url('https://fonts.googleapis.com/css2?family=Koulen&display=swap');
				</style>
				<p2>Exploit</p2>
				<p1>


					your talents

				</p1>

				<p> The world is your stage, and your unique abilities

					are your ticket to success.</p>


				<div class="buttons">
					<div class="border-button">
						<a href="Announcement.php">Find a job</a>
					</div>
					<div class="main-button">
						<a href="profile/users.php">Find a talent</a>
					</div>
				</div>
			</div>
		</div>
	</div>



	<div class="talents-jobs">
		<div class="gallery">

			<div class="section-heading">
				<div class="line-dec"></div>
				<h2>Browse Through Our <em>Performers</em> Here.</h2>
			</div>
			<div class="gallery-image">
				<a href="profilespage/singers.php">
					<div class="img-box">
						<img src="images/singer.jpg" alt="" />
						<div class="transparent-box">
							<div class="caption">
								<p>Singers</p>

							</div>
						</div>
					</div>
				</a>
				<a href="profilespage/actors.php">
					<div class="img-box">
						<img src="images/actor.jpg" alt="" />
						<div class="transparent-box">
							<div class="caption">
								<p>Actors</p>

							</div>
						</div>
					</div>
				</a>
				<a href="profilespage/models.php">
					<div class="img-box">
						<img src="images/model.jpg" alt="" />
						<div class="transparent-box">
							<div class="caption">
								<p>Models</p>

							</div>
						</div>
					</div>
				</a>
				<a href="profilespage/Tv.php">
					<div class="img-box">
						<img src="images/tv.png" alt="" />
						<div class="transparent-box">
							<div class="caption">
								<p>TV Hosts</p>
								<!--<p class="opacity-low">Landscape</p>-->
							</div>
						</div>
					</div>
					</a>
					<a href="profilespage/dancers.php">
					
					<div class="img-box">
						<img src="images/dancer.jpg" alt="" />
						<div class="transparent-box">
							<div class="caption">
								<p>Dancers</p>

							</div>
						</div>
					</div>
					</a>
				

			</div>
		</div>


		<div class="Jobs">
			<div class="section-heading">

				<h2>Browse Through Featured <em>Jobs</em> Here.</h2>

			</div>
			<a class="see-more" href="Announcement.php">see more</a>
			<section class="ftco-section">
				<div class="container">
					<div class="row">
						<div class="col-md-12">
							<div class="featured-carousel owl-carousel">
								<?php
								
								
								$i = 0;


								while ($row = mysqli_fetch_assoc($res)) {
									if ($i >= 6) {
										break;
									}
									$i++;
									$date = date_create($row['date']);
								?>
									<div class="item">
										<div class="blog-entry">
											<a href="job_details.php?job_id=<?php echo $row['job_id']; ?>" class="block-20 d-flex align-items-start" style="background-image: url('<?php echo $row['image'] ?>');">
												<div class="entete">
													<span class="day"><?php echo date_format($date, 'd'); ?></span>
													<span class="month"><?php echo date_format($date, 'M'); ?></span>
													<span class="year"><?php echo date_format($date, 'Y'); ?></span>

												</div>
												<div class="categ"><?php echo $row['role']; ?></div>
											</a>
											<div class="text border border-top-0 p-4">
												<h3 class="heading"><a href="#"><b><?php echo $row['title']; ?></b></a></h3>
												<p><?php echo $row['description']; ?></p>
												<div class="d-flex align-items-center mt-4">

													<p class="mb-0">
														<a href="job_details.php?job_id=<?php echo $row['job_id']; ?>" class="btn btn-primary">Read More <span class="ion-ios-arrow-round-forward"></span></a>
													</p>
													
												</div>
											</div>

										</div>
									</div>
								<?php } ?>
							</div>
						</div>
					</div>
				</div>
			</section>

			<script src="carousel-07/js/jquery.min.js"></script>
			<script src="carousel-07/js/popper.js"></script>
			<script src="carousel-07/js/bootstrap.min.js"></script>
			<script src="carousel-07/js/owl.carousel.min.js"></script>
			<script src="carousel-07/js/main.js"></script>
		</div>



		<?php include('footer.php') ?>





</body>
