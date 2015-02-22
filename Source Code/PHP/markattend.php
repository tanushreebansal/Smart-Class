<?php
error_reporting(E_ALL ^ E_NOTICE ^ E_WARNING);

$dbhost = "localhost"; 
$dbuser = "root";
$dbpass = "";
$dbdb = "smart_classroom";

$connect = mysql_connect($dbhost, $dbuser, $dbpass) or die("connection error");
mysql_select_db($dbdb)or die("database selection error");

$sid = $_POST['sid'];
$date = $_POST['date'];
$code = $_POST['code'];


$sql="INSERT INTO attendance_sheet (Course_No, Date, sid)
VALUES
('".$code."','".$date."','".$sid."')";

if (!mysql_query($sql))
  {
 $response["op"] = "invalid";
  }
 else
 {
  $response["op"] = "valid";
 }
echo json_encode($response);


mysql_close();




?>