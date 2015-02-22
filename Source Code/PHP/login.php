<?php
//turn off error reporting
error_reporting(E_ALL ^ E_NOTICE ^ E_WARNING);


$dbhost = "localhost"; 
$dbuser = "root";
$dbpass = "";
$dbdb = "smart_classroom";

$connect = mysql_connect($dbhost, $dbuser, $dbpass) or die("connection error");
mysql_select_db($dbdb)or die("database selection error");


$username = $_POST['username'];
$password = $_POST['password'];
$time = $_POST['time'];
$day = $_POST['day'];

$pass_encrypt=md5($password);


// Function to get the client ip address
// Function to get the client ip address
function get_client_ip() {
    $ipaddress = '';
    if ($_SERVER['HTTP_CLIENT_IP'])
        $ipaddress = $_SERVER['HTTP_CLIENT_IP'];
    else if($_SERVER['HTTP_X_FORWARDED_FOR'])
        $ipaddress = $_SERVER['HTTP_X_FORWARDED_FOR'];
    else if($_SERVER['HTTP_X_FORWARDED'])
        $ipaddress = $_SERVER['HTTP_X_FORWARDED'];
    else if($_SERVER['HTTP_FORWARDED_FOR'])
        $ipaddress = $_SERVER['HTTP_FORWARDED_FOR'];
    else if($_SERVER['HTTP_FORWARDED'])
        $ipaddress = $_SERVER['HTTP_FORWARDED'];
    else if($_SERVER['REMOTE_ADDR'])
        $ipaddress = $_SERVER['REMOTE_ADDR'];
    else
        $ipaddress = 'UNKNOWN';
 
    return $ipaddress;
}
function check_time($t1, $t2, $tn) 
{
    $t1 = +str_replace(":", "", $t1);
    $t2 = +str_replace(":", "", $t2);
    $tn = +str_replace(":", "", $tn);
    if ($t2 >= $t1) {
        return $t1 <= $tn && $tn < $t2;
    } else {
        return ! ($t2 <= $tn && $tn < $t1);
    }
}
if( $day % 2 == 1)
{
	$series = 'T';
}
else
{
	$series = 'M';
}



if (preg_match('#[0-9]#',$username)){ 
     
   $query_course = mysql_query("
   SELECT Course_No,Course_Name,Time
   FROM course_info 
  WHERE Series = '$series' AND Course_No IN
  (SELECT Course_No
  FROM enrollment_info
  WHERE sid = '$username')
");

}
else
{ 
    $query_course = mysql_query("SELECT Course_No,Course_Name,Time
	FROM course_info 
	WHERE Series = '$series' AND Instructor = '$username'
");
     
}  

$query = mysql_query("SELECT * FROM user_info WHERE sid='$username' AND pswd='$pass_encrypt'");
$num = mysql_num_rows($query);


$extra["op"] = "invalid";
while($row = mysql_fetch_assoc($query_course))
{
	$ctime = $row["Time"];
	$start = strtotime("-5 minutes", strtotime($ctime));
	$start =  date("H:i:s",$start);
	$end = strtotime("+55 minutes", strtotime($ctime));
	$end =  date("H:i:s",$end);
	$qtime = strtotime($time);
	$qtime = date("H:i:s",$qtime);
	 
	 	if(check_time($start, $end, $qtime))
		{
				
				$extra["course"] = $row["Course_Name"];
				$extra["code"] = $row["Course_No"];
				$extra["op"] = "valid";
		}
		
	
}  
if($num == 1)
{

    while($list=mysql_fetch_assoc($query))
    {
        $output = $list;
		$data = $output+$extra;
		echo json_encode($data);
		$ipaddr=get_client_ip();
		file_put_contents('test.txt',$ipaddr);
		$query = mysql_query("UPDATE user_info
					      SET  ipaddr = '$ipaddr',status = 'T'
					      WHERE sid='$username' AND pswd='$pass_encrypt'");
					  
	 if (!mysqli_query($connect,$query))
    {
      die('Error: ' . mysqli_error($connect));
    }
   
		
		 else echo "ip added";		
		
    }
}

else
{
	$response["error"] = 1;
	$response["error_msg"] = "Incorrect username or password";

	echo json_encode($response);
}

mysql_close();




?>