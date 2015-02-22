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
$mysqldate = date( 'Y-m-d', $date );


$query = mysql_query("SELECT u.sname,u.degree,i.image
					  FROM id_pics i,user_info u
					  WHERE i.image_name = u.sid AND u.sid ='$sid'");
$num = mysql_num_rows($query);



if($num == 1)
{

    while($list=mysql_fetch_assoc($query))
    {
        $output = $list;
		$output['image']=base64_encode($output['image']);
        echo json_encode($output);
    }
}

else
{
	$response["error"] = 1;
	$response["error_msg"] = "No such record found";

	echo json_encode($response);
}


mysql_close();




?>