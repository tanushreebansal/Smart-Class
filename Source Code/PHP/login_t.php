<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
  <title>Smart Classroom</title>
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

	
  
  </style>
</head>

<body>
<form action="login_t_check.php" method="post">
<?php
echo 'Login name: <input type="text" name="LOGIN_NAME" > <br>';
echo "<br></br>"; 
echo 'Password:';
echo "&nbsp;&nbsp;&nbsp;&nbsp;";

echo  '<input type="password" name="PASSWORD"> <br>';
if(isSet($error))
	echo $error;
	echo "<br></br>";
	echo "&nbsp;&nbsp;&nbsp;&nbsp;";
echo "&nbsp;&nbsp;&nbsp;&nbsp;";
echo "&nbsp;&nbsp;&nbsp;&nbsp;";
echo "&nbsp;&nbsp;&nbsp;&nbsp;";
echo "&nbsp;&nbsp;&nbsp;&nbsp;";
	?>

<input type="submit" value="LOG IN">
</form>
</form>
</form>
</div>
</div>

</body>
</html>


