<?php
$host = "localhost";
$user = "root";
$pass = "";
$db_name = "fcmdb";

$con = mysqli_connect($host,$user,$pass,$db_name);
if(!$con){
echo "Error while connecting to database";
}
?>
