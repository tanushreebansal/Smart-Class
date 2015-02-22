<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Upload ID Photos</title>
</head>
<body>
<form method="post" action="<?php echo htmlentities($_SERVER['PHP_SELF']); ?>" enctype="multipart/form-data">
<br />
Select Image File:
<input type="file" name="userfile"  size="40">
<input type="hidden" name="MAX_FILE_SIZE" value="10000000">
<select name="image_ctgy">
<option value="student">Student</option>
<option value="faculty">Faculty</option>
<option value="staff">Staff</option>
</select>
<br />
<input type="submit" value="Submit">
 
</form>

<?php

/*** check if a file was submitted ***/
if(!isset($_FILES['userfile'], $_POST['image_ctgy']))
    {
    echo '<p>Please select a file</p>';
    }
else
    {
    try {
        upload();
        echo '<p>Thank you for submitting</p>';
        }
    catch(PDOException $e)
        {
		echo '<h4>'.$e->getMessage().'</h4>';
        }
    catch(Exception $e)
        {
        echo '<h4>'.$e->getMessage().'</h4>';
        }
    }

/**
 *
 * the upload function
 * 
 * @access public
 *
 * @return void
 *
 */
function upload()
{
	/*** check if a file was uploaded ***/
	if(is_uploaded_file($_FILES['userfile']['tmp_name']) && getimagesize($_FILES['userfile']['tmp_name']) != false)
	{
		/*** an array of allowed categories ***/
		$cat_array = array("student", "faculty", "staff");
    if(filter_has_var(INPUT_POST, "notset") !== false || in_array($_POST['image_ctgy'], $cat_array) !== false)
        {
        $image_ctgy = filter_input(INPUT_POST, "image_ctgy", FILTER_SANITIZE_STRING);
        }
    else
        {
        throw new Exception("Invalid Category");
        }
    /***  get the image info. ***/
    $size = getimagesize($_FILES['userfile']['tmp_name']);

    /*** assign our variables ***/
    $image_type   = $size['mime'];
    $imgfp        = fopen($_FILES['userfile']['tmp_name'], 'rb');
    $image_width  = $size[0];
    $image_height = $size[1];
    $image_size   = $size[3];
	
    $filename  = $_FILES['userfile']['name'];
	$temp = explode( '.', $filename );
	$ext = array_pop( $temp );
	$image_name = implode( '.', $temp );
    $maxsize      = 99999999;

    /***  check the file is less than the maximum file size ***/
    if($_FILES['userfile']['size'] < $maxsize )
        {
       
        /*** connect to db ***/
        $dbh = new PDO("mysql:host=localhost;dbname=smart_classroom", 'root', '');

        /*** set the error mode ***/
        $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        /*** prepare the sql ***/
        $stmt = $dbh->prepare("INSERT INTO id_pics (image_id,image_type ,image, image_height, image_width, image_ctgy,image_name)
        VALUES (? ,?, ?, ?, ?, ?,?)");
        $stmt->bindParam(1, $image_id);
		$stmt->bindParam(2, $image_type);
        $stmt->bindParam(3, $imgfp, PDO::PARAM_LOB);
        $stmt->bindParam(4, $image_height, PDO::PARAM_INT);
        $stmt->bindParam(5, $image_width,  PDO::PARAM_INT);
        $stmt->bindParam(6, $image_ctgy);
		$stmt->bindParam(7, $image_name);
        

        /*** execute the query ***/
        $stmt->execute();
        }
    else
        {
    /*** throw an exception is image is not of type ***/
    throw new Exception("File Size Error");
        }
    }
else
    {
    // if the file is not less than the maximum allowed, print an error
    throw new Exception("Unsupported Image Format!");
    }
}
?>
<br/>
<a href="view.php">View all uploaded images</a>
</body>
</html>