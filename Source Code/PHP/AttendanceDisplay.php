<html>
<head>
  <title>Smart Classroom:Attendance Display</title>
<form action="AttendanceDisplay.php" method="post">
 <style type="text/css">
  body {
		font-family:  Arial;
		color: black;
		background-color: #fff8dc}
		#up_finished {
			font-size: 10px;
		}
		p.margin
{
margin-top:10px;
margin-bottom:50px;
margin-right:50px;
margin-left:550px;
}
	
</style>

<table border="1"style="margin-left:80px;"style="display: inline-block;">

<?php
	session_start();
	$host = "localhost"; 
	$user = "root"; 
	$pass = ""; 
	$db = "smart_classroom"; 
	$con = mysqli_connect($host, $user, $pass,$db);
	$sel=$_GET['var'];
	echo "<p class=margin>";
	echo "<font size=6 face=Arial margin> ".$sel.	"</font>";
	$query="SELECT * FROM course_info WHERE Course_No='".$sel."'";
	$result=mysqli_query($con,$query);
	while ($row = mysqli_fetch_array($result))
	{
		echo "<font size=6 face=Arial> ".$row['Course_Name']."</font>";
	}
	echo"</p>";
	echo	"<font size=4> <p><br><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ATTENDANCE SHEET</font>";
	$course_no=$sel;
	$sql1="SELECT * from enrollment_info WHERE Course_No='".$course_no."'";
	$sql2="SELECT DISTINCT Date from attendance_sheet WHERE Course_No='".$course_no."'";
	$result1 = mysqli_query($con,$sql1);
	$result2 = mysqli_query($con,$sql2);
	$present=0;
	if (!$result1)
	{
		die(mysqli_error($con));
	}
	echo "<p><tr>
	<td><b>StudentID</td>";
	while($row2 = mysqli_fetch_array($result2))
	{
		echo "<td><b> ".$row2['Date']."</td>";
	}
	echo "</tr>";
	$sql3="SELECT sid from  enrollment_info WHERE Course_No='".$course_no."'";
	$result3 = mysqli_query($con,$sql3);
	$count = mysqli_num_rows($result3)-1;
	while($row3 = mysqli_fetch_array($result3))
	{
		if(preg_match('#[0-9]#',$row3['sid']))
		{
			echo "<tr> <td><b>".$row3['sid']."</td>";
			$sql4="SELECT Distinct Date FROM attendance_sheet WHERE Course_No = '$course_no'";
			$result4= mysqli_query($con,$sql4);
			while($row4 = mysqli_fetch_array($result4))
			{
				$query="SELECT * FROM attendance_sheet where Date='".$row4['Date']."' AND sid = '".$row3['sid']."' AND Course_No = '".$course_no."' ";
				$result= mysqli_query($con,$query);
				$num = mysqli_num_rows($result);
				if($num == 1)
				{
					echo "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;P</td>";
					$present=$present+1;
				}
				else
				echo "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A</td>";
			}
		}
		echo "</tr>";
	}	
	
?>

</table>
<table border="1"style="margin-left:80px;"style="display: inline-block;">
<?php
	echo	"<font size=4> <p><br><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;STATISTICS</font>";
	echo "<tr>";
	$sql5="SELECT Distinct Date FROM attendance_sheet WHERE Course_No = '$course_no'";
	$result5= mysqli_query($con,$sql5);
	while($row5 = mysqli_fetch_array($result5))
	{
		echo "<td><b> ".$row5['Date']."</td>";
	}
	echo "</tr>";
	echo "<tr>";
	$sql7="SELECT Distinct Date FROM attendance_sheet WHERE Course_No = '$course_no'";
	$result7= mysqli_query($con,$sql7);
	$present1 = mysqli_num_rows($result7);
	echo "</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TOTAL NUMBER OF STUDENTS ENROLLED IN THE COURSE=".$count;		
	echo "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PERCENTAGE OF STUDENTS PRESENT:<br>";
	while($row7 = mysqli_fetch_array($result7))
	{
		$sql6="SELECT * FROM attendance_sheet WHERE Date=date'".$row7['Date']."'AND Course_No='$course_no'";
		$result6= mysqli_query($con,$sql6);
		$present = mysqli_num_rows($result6);
		$percent=($present/$count)*100;
		echo "<td>&nbsp;&nbsp;&nbsp;&nbsp; ".round($percent, 2)."%</td>";
	}
	echo "</tr>";
	if(isset($_GET['log']) && ($_GET['log']=='out')){
    //if the user logged out, delete any SESSION variables
	session_destroy();
	//then redirect to login page
	header('location:login_t.php');
	}
	?>
	<div style="position:absolute; left:1300px; top:5px;">
<p> <a href="?log=out">Logout</a> </p>
</body>
</html>
