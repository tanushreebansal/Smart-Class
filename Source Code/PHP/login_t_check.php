<html>
<body>
<form action="home.php" method="post">
<?php
session_start();
	$lname = $_POST['LOGIN_NAME'];
	$pwd = $_POST['PASSWORD'];
	
	$pass_encrypt=md5($pwd);
	
	$con = mysqli_connect("localhost","root","","smart_classroom");
	
	$sql = "SELECT * FROM user_info WHERE sid='".$lname."' and pswd='".$pass_encrypt."'";
	$result = mysqli_query($con,$sql);
	if (mysqli_num_rows($result) != 0){
		header('Location: home.php');
	}
	else{
		$error = 'Username password did not match';
		include_once('login_t.php');
	}
	if($_SERVER['REQUEST_METHOD']=='POST')
   {
   $LOGIN_NAME=$_POST['LOGIN_NAME'];
$_SESSION['favcolor'] = $LOGIN_NAME;  
}
?>
</body>
</html>