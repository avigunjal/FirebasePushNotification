<?php
require "init.php";
$fcm_token = $_POST['fcm_token'];

$sql = "insert into fcm_info values('$fcm_token');";

if(!isset($_POST['fcm_token'])){
  $message  = array('code'=>"400",'message'=> "Parameter not set");
echo json_encode($message);
}else {

if(mysqli_query($con,$sql))
{
  $message  = array('code'=>"200",'message'=> "Successfull");
 echo json_encode($message);
}
else {
  $message  = array('code'=>"400",'message'=> "Failure");
echo json_encode($message);
}
}

mysqli_close($con);
?>
