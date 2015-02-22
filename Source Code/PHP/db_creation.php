<html>
<body>
<?php
	$host = "localhost"; 
	$user = "root"; 
	$pass = ""; 
	$db = "smart_classroom"; 
	$con = mysqli_connect($host, $user, $pass);
	
	
	$sql="CREATE DATABASE smart_classroom";
if (mysqli_query($con,$sql))
  {
  echo "Database my_db created successfully";
  }
else
  {
  echo "Error creating database: " . mysqli_error($con);
  }
$conn = mysqli_connect($host, $user, $pass,$db);
  $sql="CREATE TABLE user_info(sid VARCHAR(30),PRIMARY KEY(sid),sname VARCHAR(30),degree VARCHAR(50),pswd VARCHAR(40),status CHAR(1),ipaddr VARCHAR(30))";

// Execute query
if (mysqli_query($conn,$sql))
  {
  echo "Table user_info created successfully";
  }
else
  {
  echo "Error creating table: " . mysqli_error($conn);
  }
  
  $sql="CREATE TABLE id_pics(image_id TINYINT(3) NOT NULL AUTO_INCREMENT,PRIMARY KEY(image_id),image_type VARCHAR(25),image LONGBLOB,image_height INT(5),image_width INT(5),image_ctgy VARCHAR(25),image_name VARCHAR(30),FOREIGN KEY (image_name) REFERENCES user_info(sid))";

// Execute query
if (mysqli_query($conn,$sql))
  {
  echo "Table id_pics created successfully";
  }
else
  {
  echo "Error creating table: " . mysqli_error($conn);
  }

 $sql="CREATE TABLE course_info(Course_No CHAR(30),Course_Name CHAR(30),Series CHAR(1),Time TIME,Instructor VARCHAR(30),valid  CHAR(1),port INT,PRIMARY KEY(Course_No),FOREIGN KEY (Instructor) REFERENCES user_info(sid))";

// Execute query
if (mysqli_query($conn,$sql))
  {
  echo "Table course_info created successfully";
  }
else
  {
  echo "Error creating table: " . mysqli_error($conn);
  } 
   $sql="CREATE TABLE enrollment_info(Course_No CHAR(30),sid VARCHAR(30),PRIMARY KEY(Course_No,sid),
   FOREIGN KEY (Course_No) REFERENCES course_info(Course_No),FOREIGN KEY (sid) REFERENCES user_info(sid))";

// Execute query
if (mysqli_query($conn,$sql))
  {
  echo "Table enrollment_info created successfully";
  }
else
  {
  echo "Error creating table: " . mysqli_error($conn);
  }
  
 $sql="CREATE TABLE attendance_sheet(Course_No CHAR(30) NOT NULL,Date DATE,sid VARCHAR(30),PRIMARY KEY(Course_No,Date,sid),FOREIGN KEY (sid,Course_No) REFERENCES enrollment_info(sid,Course_No))";

// Execute query
if (mysqli_query($conn,$sql))
  {
  echo "Table attendance_sheet created successfully";
  }
else
  {
  echo "Error creating table: " . mysqli_error($conn);
  }
  
$sql="INSERT INTO user_info VALUES
('2010C6PS656G','Tanushree Bansal','Msc (Tech.) Information Systems','202cb962ac59075b964b07152d234b70','0','')";
  if (!mysqli_query($conn,$sql))
  {
  die('Error: ' . mysqli_error($conn));
  }
else echo "1 record added \n";


$sql="INSERT INTO user_info VALUES
('2012H140039G','Divya Vipin','M.E. Embedded Systems','202cb962ac59075b964b07152d234b70','0','')";
  if (!mysqli_query($conn,$sql))
  {
  die('Error: ' . mysqli_error($conn));
  }
else echo "1 record added \n";


$sql="INSERT INTO user_info VALUES
('srev','Sreejith V.','faculty','202cb962ac59075b964b07152d234b70','0','')";
  if (!mysqli_query($conn,$sql))
  {
  die('Error: ' . mysqli_error($conn));
  }
else echo "1 record added \n";


$sql="INSERT INTO course_info VALUES
('CSG541','Pervasive Computing','T','11:00:00','srev','0')";
  if (!mysqli_query($conn,$sql))
  {
  die('Error: ' . mysqli_error($conn));
  }
else echo "1 record added \n";

$sql="INSERT INTO enrollment_info VALUES('CSG541','2010C6PS656G')";
  if (!mysqli_query($conn,$sql))
  {
  die('Error: ' . mysqli_error($conn));
  }
else echo "1 record added \n";

$sql="INSERT INTO enrollment_info VALUES('CSG541','2012H140039G')";
  if (!mysqli_query($conn,$sql))
  {
  die('Error: ' . mysqli_error($conn));
  }
else echo "1 record added \n";


$sql="INSERT INTO enrollment_info VALUES('CSG541','srev')";
  if (!mysqli_query($conn,$sql))
  {
  die('Error: ' . mysqli_error($conn));
  }
else echo "1 record added \n";


  ?>