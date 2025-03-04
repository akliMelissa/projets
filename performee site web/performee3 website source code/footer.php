<!DOCTYPE html>
<html>
<head>

<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
<style>

footer{
  position: absolute;
  
  left: 0;
  right: 0;
  background: #111;
  height: auto;
  width: 100vw;
  padding-top: 40px;
  color: #fff;
}
.footer-content{
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  text-align: center;
}
.footer-content h3{
  font-size: 2.1rem;
  color: #d29a33;
  font-weight: 500;
  text-transform: capitalize;
  line-height: 3rem;
}
.footer-content p{
  max-width: 500px;
  margin: 10px auto;
  line-height: 28px;
  font-size: 14px;
  color: #cacdd2;
}
.socials{
  list-style: none;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 1rem 0 3rem 0;
}
.socials li{
  margin: 0 10px;
}
.socials a{
  text-decoration: none;
  color: #fff;
  border: 1.1px solid white;
  padding: 5px;

  border-radius: 50%;

}
.socials a i{
  font-size: 1.1rem;
  width: 20px;


  transition: color .4s ease;

}
.socials a:hover i{
  color: aqua;
}

.footer-bottom{
  background: #000;
  width: 100vw;
  padding: 20px;
padding-bottom: 40px;
  text-align: center;
}
.footer-bottom p{
float: left;
  font-size: 14px;
  word-spacing: 2px;
  text-transform: capitalize;
}
.footer-bottom p a{
color:#d29a33;
font-size: 16px;
text-decoration: none;
}
.footer-bottom span{
  text-transform: uppercase;
  opacity: .4;
  font-weight: 200;
}
.footer-menu{
float: right;

}
.footer-menu ul{
display: flex;
}
.footer-menu ul li{
padding-right: 10px;
display: block;
}
.footer-menu ul li a{
color: #cfd2d6;
text-decoration: none;
}
.footer-menu ul li a:hover{
color: #27bcda;
}

@media (max-width:500px) {
.footer-menu ul{
display: flex;
margin-top: 10px;
margin-bottom: 20px;
}
}

.footer_margin{
background-color: #d29a33;
height: 1px;
}
</style>
</head>

<body>
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
          <li><a href="mailto:infoperformee@gmail.com"><i class="fa fa-google-plus"></i></a></li>
          <li><a href="#"><i class="fa fa-linkedin-square"></i></a></li>
         

      </ul>
  </div>
  <div class="footer-bottom">
      <p>copyright &copy; <a href="copyright.php">Performee</a>  </p>
              <div class="footer-menu">
                <ul class="f-menu">
                  <li><a href="">Home</a></li>
                  <li><a href="">Contact</a></li>
                  <li><a href="">Blog</a></li>
                </ul>
              </div>
  </div>

</footer>

</body>
</html>