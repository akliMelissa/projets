<?php
session_start();
if (!isset($_SESSION['user_id'])) {
    // Redirect to login page if user is not authenticated
    include('header.php');
} else {
    
    include('user_header.php');
}
include("logic.php");





?>
<style>
    .li {
        font-size: 10px;
    }
</style>
<!doctype html>
<html class="no-js" lang="zxx">

<head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Perfomee-job </title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="manifest" href="site.webmanifest">


    <!-- CSS here -->
    <link rel="stylesheet" href="assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="assets/css/owl.carousel.min.css">
    <link rel="stylesheet" href="assets/css/flaticon.css">
    <link rel="stylesheet" href="assets/css/slicknav.css">
    <link rel="stylesheet" href="assets/css/price_rangs.css">
    <link rel="stylesheet" href="assets/css/animate.min.css">
    <link rel="stylesheet" href="assets/css/magnific-popup.css">
    <link rel="stylesheet" href="assets/css/fontawesome-all.min.css">
    <link rel="stylesheet" href="assets/css/themify-icons.css">
    <link rel="stylesheet" href="assets/css/slick.css">
    <link rel="stylesheet" href="assets/css/nice-select.css">
    <link rel="stylesheet" href="assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/4.5.6/css/ionicons.min.css">
    <link rel="stylesheet" href="carousel-07/css/style.css">
</head>

<body>
    <!-- Preloader Start -->

    <!-- Preloader Start -->


    <main>



        <!-- job post company Start -->
        <div class="job-post-company pt-120 pb-120">
            <div class="container">

                <div class="single-job-items mb-50">

                    <div class="job-items">


                        <div class="job-tittle">
                            <?php
                             
                            $row = mysqli_fetch_assoc($result);
                            $id = $row['user_id'];
                            $imgData = $row['image'];
	                        $dataURI = 'data:image/jpeg;base64,'.base64_encode($imgData);
                            // Prepare and bind the query
                 
                                $stmt = $conn->prepare("SELECT * FROM recruiters WHERE user_id = ?");
                                $stmt->bind_param("i", $id);
                                $stmt->execute();


                                // Get the result set
                                $result = $stmt->get_result();
                                if ($row1 = $result->fetch_assoc()) {
                                    $post_by = $row1['company_name'];
                                    $location = $row1['location'];
                                }
                            
                            ?>
                            <a href="#">
                                <h4><?php echo $row['title']; ?></h4>
                            </a>
                            <ul>
                                <li>
                                    <a href="profile/recruiter_profile.php?id=<?php echo $row['user_id']; ?>"><?php echo $post_by;  ?></a>
                                </li>
                                <li><i class="fas fa-map-marker-alt"></i><?php echo $location; ?></li>

                            </ul>
                        </div>
                    </div>
                </div>

                <div class="row justify-content-between">

                    <!-- Left Content -->
                    <div class="col-xl-7 col-lg-8">

                        <!-- job single -->

                        <!-- job single End -->

                        <div class="job-post-details">
                            <div class="post-details1 mb-50">
                                <!-- Small Section Tittle -->
                                <div class="small-section-tittle">
                                    <h4>Job Description</h4>
                                </div>
                                <p><?php echo $row['description'];
                                    ?></p>
                            </div>
                            <div class="post-details2  mb-50">
                                <!-- Small Section Tittle -->
                                <div class="small-section-tittle">
                                    <h4>Requirements</h4>
                                </div>
                                <ul>
                                    <li><?php echo $row['gender'];
                                        ?></li>
                                    <li>Age : <?php echo $row['age'];
                                                ?> </li>


                                </ul>
                            </div>
                            <div class="post-details2  mb-50">
                                <!-- Small Section Tittle -->
                                <div class="small-section-tittle">
                                    <h4>Experience</h4>
                                </div>
                                <ul>
                                    <li><?php echo $row['experience'];
                                        ?></li>


                                </ul>
                            </div>
                        </div>

                    </div>
                    <!-- Right Content -->
                    <div class="col-xl-4 col-lg-4">
                        <div class="job-img">
                            <img src='<?php echo $row['image'] ?>'>
                        </div>
                        <div class="post-details3  mb-50">
                            <div class="small-section-tittle">
                                <h4>Overview</h4>
                            </div>

                            <ul>
                                <li>Posted date : <span><?php echo $row['date_added'];
                                                        ?></span></li>
                                <li>Location : <span><?php echo $row['location'];
                                                        ?></span></li>
                                <li>Role : <span> <?php echo $row['role'];
                                                    ?></span></li>

                                <li>Application date : <span><?php echo $row['deadline'];
                                                                ?></span></li>
                            </ul>
                            <div class="apply-btn2">
                                <a href="profile/recruiter_profile.php?id=<?php echo $row['user_id']; ?>#contact " class="btn" >Apply Now</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
        </div>
        <!-- job post company End -->

    </main>
    <footer>
        <!-- Footer Start-->
        <div class="footer_margin"></div>

        <footer>
            <div class="footer-content">
                <h3>Performee</h3>
                <p>
                    <i>
                        At Performee, we believe that everyone has the potential
                        to be a star. Our platform empowers individuals to unleash
                        their inner performer and share their unique talents with
                        the world
                    </i>
                </p>
                <ul class="socials">

                    <li><a href="#"><i class="fa fa-facebook"></i></a></li>
                    <li><a href="#"><i class="fa fa-google-plus"></i></a></li>
                    <li><a href="#"><i class="fa fa-linkedin-square"></i></a></li>

                    <!--  
        <li><img src="images/facebook_icon-removebg-preview.png"></li>
        <li><img src="images/insta_icon-removebg-preview.png"></li>
        <li><img src="images/mail_icon-removebg-preview.png"></li>
        -->

                </ul>
            </div>
            <div class="footer-bottom">
                <p>copyright &copy; <a href="#">Performee</a> </p>
                <div class="footer-menu">
                    <ul class="f-menu">
                        <li><a href="">Home</a></li>
                        <li><a href="">Contact</a></li>
                        <li><a href="">Blog</a></li>
                    </ul>
                </div>
            </div>

        </footer>
        <!-- Footer End-->
    </footer>

    <!-- JS here -->

    <!-- All JS Custom Plugins Link Here here -->
    <script src="./assets/js/vendor/modernizr-3.5.0.min.js"></script>
    <!-- Jquery, Popper, Bootstrap -->
    <script src="./assets/js/vendor/jquery-1.12.4.min.js"></script>
    <script src="./assets/js/popper.min.js"></script>
    <script src="./assets/js/bootstrap.min.js"></script>
    <!-- Jquery Mobile Menu -->
    <script src="./assets/js/jquery.slicknav.min.js"></script>

    <!-- Jquery Slick , Owl-Carousel Plugins -->
    <script src="./assets/js/owl.carousel.min.js"></script>
    <script src="./assets/js/slick.min.js"></script>
    <script src="./assets/js/price_rangs.js"></script>

    <!-- One Page, Animated-HeadLin -->
    <script src="./assets/js/wow.min.js"></script>
    <script src="./assets/js/animated.headline.js"></script>
    <script src="./assets/js/jquery.magnific-popup.js"></script>

    <!-- Scrollup, nice-select, sticky -->
    <script src="./assets/js/jquery.scrollUp.min.js"></script>
    <script src="./assets/js/jquery.nice-select.min.js"></script>
    <script src="./assets/js/jquery.sticky.js"></script>

    <!-- contact js -->
    <script src="./assets/js/contact.js"></script>
    <script src="./assets/js/jquery.form.js"></script>
    <script src="./assets/js/jquery.validate.min.js"></script>
    <script src="./assets/js/mail-script.js"></script>
    <script src="./assets/js/jquery.ajaxchimp.min.js"></script>

    <!-- Jquery Plugins, main Jquery -->
    <script src="./assets/js/plugins.js"></script>
    <script src="./assets/js/main.js"></script>

</body>

</html>