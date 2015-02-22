<?php
error_reporting(E_ALL ^ E_NOTICE ^ E_WARNING);

$dbhost = "localhost"; 
$dbuser = "root";
$dbpass = "";
$dbdb = "smart_classroom";

$connect = mysql_connect($dbhost, $dbuser, $dbpass) or die("connection error");
mysql_select_db($dbdb)or die("database selection error");

$uid = $_POST['uid'];
$code = $_POST['code'];
$date = $_POST['date'];

if (preg_match('#[0-9]#',$uid))
{ 
     
  $query = mysql_query("SELECT DISTINCT *
					  FROM attendance_sheet
					  WHERE Course_No ='$code'");
$total = mysql_num_rows($query);

$query1 = mysql_query("SELECT *
					  FROM attendance_sheet
					  WHERE Course_No ='$code' AND sid = '$uid'");
$present = mysql_num_rows($query1);

$response["total"] = $total;
$response["present"] = $present;

}
else
{ 
   $query = mysql_query("SELECT *
					  FROM enrollment_info
					  WHERE Course_No ='$code'");
$total = mysql_num_rows($query);

$query1 = mysql_query("SELECT *
					  FROM attendance_sheet
					  WHERE Course_No ='$code' AND Date = '$date'");
$present = mysql_num_rows($query1);

$response["total"] = $total;
$response["present"] = $present;
     
}  


echo json_encode($response);



mysql_close();




?>