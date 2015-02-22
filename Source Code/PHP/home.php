<html>
<head>
  <title>Smart Classroom</title>
<form action="AttendanceDisplay.php" method="post">
 <style type="text/css">
  body {
    padding-left: 3.5em;
    padding-top: 3.5em;
	font-family: Georgia, "Arial",
          Times, serif;
    color: blue;
    background-color: #fff8dc}
	body
{
background-position:550px 10px ;
background-image:url('bits-goa.png') ;
background-repeat:no-repeat;
}
body {
      position: absolute;
      overflow: auto;
      left: 500px;
      right: 500px;
      top: 350px;
      bottom: 10px;
      border: 5px solid blue;
    }

	
  
  </style><body>
<script type="text/javascript">

  function doSubmit(form) {
    var urls = form['url'];
    var i = urls && urls.length;
    while (i--) {
      if (urls[i].checked) {
        window.location = urls[i].value;
      }
    }
    return false;
  }

  </script>

<?php
session_start();
$lname=$_SESSION['favcolor'];
$con = mysqli_connect("localhost","root","","smart_classroom");
$sql = "SELECT * FROM course_info WHERE Instructor='".$lname."'";
	$result = mysqli_query($con,$sql);
	while($row = mysqli_fetch_array($result))
	{	
	$course1=$row['Course_No'];
	$coursename=$row['Course_Name'];
	$_SESSION['CourseName']=$coursename;
	echo '<br> </br>';
	echo "<a href='http://localhost/Pervasive_Project/AttendanceDisplay.php?var=$course1'>$course1</a>";
	$query="SELECT Course_Name from course_info WHERE Course_No='".$course1."' ";
	$r = mysqli_query($con,$query);
	while($row1 = mysqli_fetch_array($r))
	{
	echo $row1['Course_Name'];
	}
	}
	$query2="SELECT Course_Name from course_info WHERE Course_No='".$course1."' ";
	$r2 = mysqli_query($con,$query2);
	$row1[] = mysqli_fetch_array($r);
	//echo $row1['Course_Name'];
	if(isset($_GET['log']) && ($_GET['log']=='out')){
        //if the user logged out, delete any SESSION variables
	session_destroy();
	
        //then redirect to login page
	header('location:login_t.php');
}
	?>
<div style="position:absolute; left:787px; top:-358px;">
<p> <a href="?log=out">Logout</a> </p>
</body>
</html>



