<?php
session_start();

include("logic.php");
error_reporting(E_ALL);
ini_set('display_errors', 1);

$sql = "SELECT * FROM recruiters WHERE user_id='$user_id'";
$result = $conn->query($sql);
if ($result->num_rows == 0) {
    header("Location: details_recruiter.php");
    exit();
}







?>
<!doctype html>
<html class="no-js" lang="zxx">

<head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="manifest" href="site.webmanifest">
    <link rel="shortcut icon" type="image/x-icon" href="assets/img/favicon.ico">

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
    <title>Performee</title>
    <link rel="icon" type="image/x-icon" href="images/logofinal.jpg">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/4.5.6/css/ionicons.min.css">
    <link rel="stylesheet" href="carousel-07/css/style.css">


</head>

<body>
    <!-- Preloader Start -->
    <div id="preloader-active">
        <div class="preloader d-flex align-items-center justify-content-center">
            <div class="preloader-inner position-relative">
                <div class="preloader-circle"></div>
                <div class="preloader-img pere-text">
                    <img src="assets/img/logo/logo.png" alt="">
                </div>
            </div>
        </div>
    </div>
    <!-- Preloader Start -->


    <main>
        <?php if (!isset($_SESSION['user_id'])) {
            // Redirect to login page if user is not authenticated
            include('header.php');
        } else {
            $getid = $_SESSION["user_id"];
            include('user_header.php');
        }
        ?>

        <!-- job post company Start -->
        <div class="job-post-company pt-120 pb-120">
            <div class="container">
                <div class="single-job-items mb-50">
                    <div class="job-tittle">
                        <?php

                        if (isset($_REQUEST["info"])) {
                            if ($_REQUEST["info"] == "missing") { ?>

                                <div style="color:brown; font-size:30px;" class="alert">
                                    Please enter the required fields
                                </div>

                        <?php }
                        }
                        ?>
                        <h4>Add a job </h4>
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
                                    <h4>Overview</h4>
                                </div>
                                <form method="POST"  enctype="multipart/form-data">
                                    <label for="title">Title *</label>
                                    <div>
                                        <input type="text" name="title">
                                    </div>



                                    <label for="description">Description</label>
                                    <div>
                                        <textarea name="description" rows="3" cols="45"></textarea>
                                    </div>

                                    <label>Date:</label>


                                    <input type="date" name="date"><br>

                                    <label for="deadline">Application Deadline:</label>

                                    <input type="date" name="deadline"><br>

                                    <label for="location">Location *: </label>
                                    <select name="location" size="5">
                                        <option value="01">Adrar</option>
                                        <option value="02">Chlef</option>
                                        <option value="03">Laghouat</option>
                                        <option value="04">Oum El Bouaghi</option>
                                        <option value="05">Batna</option>
                                        <option value="06">Bejaïa</option>
                                        <option value="07">Biskra</option>
                                        <option value="08">Béchar</option>
                                        <option value="09">Blida</option>
                                        <option value="10">Bouira</option>
                                        <option value="11">Tamanrasset</option>
                                        <option value="12">Tebessa</option>
                                        <option value="13">Tlemcen</option>
                                        <option value="14">Tiaret</option>
                                        <option value="15">Tizi Ouzou</option>
                                        <option value="16">Alger</option>
                                        <option value="17">Djelfa</option>
                                        <option value="18">Djijel</option>
                                        <option value="19">Sétif</option>
                                        <option value="20">Saïda</option>
                                        <option value="21">Skikda</option>
                                        <option value="22">Sidi Bel Abbès</option>
                                        <option value="23">Annaba</option>
                                        <option value="24">Guelma</option>
                                        <option value="25">Constantine</option>
                                        <option value="26">Médéa</option>
                                        <option value="27">Mostaganem</option>
                                        <option value="28">M'Sila</option>
                                        <option value="29">Mascara</option>
                                        <option value="30">Ouargla</option>
                                        <option value="31">Oran</option>
                                        <option value="32">El Bayadh</option>
                                        <option value="33">Illizi</option>
                                    </select>




                            </div>




                            <div class="post-details2  mb-50">
                                <!-- Small Section Tittle -->
                                <div class="small-section-tittle">
                                    <h4>Requirements</h4>
                                </div>


                                <label for="roles">Choose a role *:</label>


                                <select name="roles" id="roles">
                                    <option value="actor">Actor</option>
                                    <option value="singer">Singer</option>
                                    <option value="dancer">Dancer</option>
                                    <option value="dancer">TV host</option>
                                    <option value="dancer">Musician</option>


                                </select>

                                <hr>

                                <label for="gender">Gender *:</label><br>
                                <input type="radio" name="gender" <?php if (isset($gender) && $gender == "female") echo "checked"; ?> value="female">Female<br>
                                <input type="radio" name="gender" <?php if (isset($gender) && $gender == "male") echo "checked"; ?> value="male">Male<br>

                                <hr>
                                <label for="age">Age *:</label>
                                <select name="age" id="age">
                                    <option value="actor">0-5</option>
                                    <option value="singer">5-10</option>
                                    <option value="dancer">10-15</option>
                                    <option value="dancer">15-20</option>
                                    <option value="dancer">20-25</option>
                                    <option value="dancer">25-30</option>
                                    <option value="dancer">30-40</option>
                                    <option value="dancer">Older than 40 </option>


                                </select>




                            </div>
                            <div class="post-details2  mb-50">
                                <!-- Small Section Tittle -->
                                <div class="small-section-tittle">
                                    <h4> Experience</h4>
                                </div>

                                <div>
                                    <textarea name="experience" rows="5" cols="45"></textarea>
                                </div>

                            </div>
                            <div class="submit_button">
                                <button id="sub_btn" type="submit" value="Submit" name="new_post">Submit</button>
                            </div>

                        </div>
                    </div>
                    <!-- Right Content -->
                    <div class="col-xl-4 col-lg-4">
                        <script>
                            function readURL(input) {
                                if (input.files && input.files[0]) {
                                    var reader = new FileReader();

                                    reader.onload = function(e) {
                                        $('#imgg')
                                            .attr('src', e.target.result);
                                    };

                                    reader.readAsDataURL(input.files[0]);
                                } 
                            }
                        </script>
                         <div class="job-img">
                            <input id="jobimg" type="file" name="img" style="display: none;" onchange="readURL(this);">
                            <a onclick="$('input[id=jobimg]').click();" style="color:#b87333">Upload Photo</a>
                            <img id="imgg" >
                            </form>
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
    <!-- Footer Start-->
   <?php include('footer.php');?>
    <!-- Footer End-->
   

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