<?php
error_reporting(E_ALL ^ E_NOTICE ^ E_WARNING);

$dbhost = "localhost"; 
$dbuser = "root";
$dbpass = "";
$dbdb = "smart_classroom";

$connect = mysql_connect($dbhost, $dbuser, $dbpass) or die("connection error");
mysql_select_db($dbdb)or die("database selection error");

$sid = $_POST['sid'];
$oldpass = $_POST['oldpass'];
$newpass = $_POST['newpass'];

$oldpass_encrypt=md5($oldpass);
$newpass_encrypt=md5($newpass);

$sql="UPDATE user_info SET pswd = '$newpass_encrypt' WHERE sid = '$sid' AND pswd= '$oldpass_encrypt'";

if (!mysql_query($sql))
  {
  file_put_contents('file.txt',msql_error());
  }
echo "password updated";

mysql_close();




?>