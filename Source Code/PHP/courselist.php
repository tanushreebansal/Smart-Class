<?php
error_reporting(E_ALL ^ E_NOTICE ^ E_WARNING);

$dbhost = "localhost"; 
$dbuser = "root";
$dbpass = "";
$dbdb = "smart_classroom";

$connect = mysql_connect($dbhost, $dbuser, $dbpass) or die("connection error");
mysql_select_db($dbdb)or die("database selection error");

$uid = $_POST['uid'];
$result["course_no"] = '';
$result["course_name"] = '';

$query = mysql_query("SELECT Course_No
					  FROM enrollment_info
					  WHERE sid ='$uid'");


while($row = mysql_fetch_assoc($query))
{					  
			$cno = $row["Course_No"];
			$result["course_no"] = $result["course_no"].$cno."@";	
			$query_course = mysql_query("SELECT Course_Name
										  FROM course_info
										  WHERE Course_No = '$cno'");
	        $row_course = mysql_fetch_assoc($query_course);
			$result["course_name"] = $result["course_name"].$row_course["Course_Name"]."@";
}					  


echo json_encode($result);
mysql_close();
?>