<?php
error_reporting(E_ALL ^ E_NOTICE ^ E_WARNING);

$dbhost = "localhost"; 
$dbuser = "root";
$dbpass = "";
$dbdb = "smart_classroom";

$connect = mysql_connect($dbhost, $dbuser, $dbpass) or die("connection error");
mysql_select_db($dbdb)or die("database selection error");


$code = $_POST['code'];
$uid = $_POST['uid'];
$valid_val = $_POST['setVal'];
$port = $_POST['serverport'];

if (preg_match('#[0-9]#',$uid))
{ 
	$query = mysql_query("SELECT Instructor,valid
					      FROM course_info
					      WHERE Course_No ='$code'");

	$val =mysql_fetch_assoc($query);

	if($val["valid"] == '1')
	{
		$instructor = $val["Instructor"];
		$query = mysql_query("SELECT ipaddr
					  FROM user_info
					  WHERE sid ='$instructor'");

		$result =mysql_fetch_assoc($query);
		
		
		$query2 = mysql_query("SELECT port
					  FROM course_info
					  WHERE Course_No ='$code'");

		$result2 =mysql_fetch_assoc($query2);
		$res["ipaddr"] = $result["ipaddr"];
		$res["port"]=$result2["port"];
		file_put_contents('port.txt',$res["port"]);
		echo json_encode($res);
	}
	else
	{
		$result["ipaddr"] = "error";
		$result["port"] = "error";
		echo json_encode($result);
	}
}
else
{

	$query = mysql_query("UPDATE course_info
					      SET valid = '$valid_val', port = '$port'
					      WHERE Course_No ='$code'");
					  
	 if (!mysqli_query($connect,$query))
    {
      die('Error: ' . mysqli_error($connect));
    }
     else echo "valid updated";		

  

}



mysql_close();




?>