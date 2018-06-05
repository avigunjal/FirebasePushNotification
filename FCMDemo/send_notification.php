<?php
require "init.php";
$message = "I am in trouble I need help!";
$title = "Security Alert";

$lat = $_POST['latitude'];
$longi = $_POST['longitude'];
$username = $_POST['username'];


$server_key = "AAAAScDkSlw:APA91bHWrFav1-sKvOTfQZ0_zT9J1oeBz9uJWdX3O4tAGFVl_wayIwxvx2Y5RUe2MoPizFRXlOW9E3p5FRvg2lSJ3fUJ5EbTGX4XIqLOYjwxtCdktKUWVFwAGK4vInShl9qma5vGm8qm";
$fcm_path = 'https://fcm.googleapis.com/fcm/send';
$sql = "select fcm_token from fcm_info";
$result = mysqli_query($con,$sql);
if(!$result){
}
$row = mysqli_fetch_row($result);
$key = $row[0];

//http request header section
$header = array(
   'Content-Type:application/json',
   'Authorization:Key=' .$server_key
);

//payload section

$fields = array(
  "to"=>"/topics/security",
  "notification"=>array("body"=>$message,"title"=>$title),
  "data"=>array("latitude"=>$lat,"longitude"=>$longi,"username"=>$username)
);

  $payload = json_encode($fields);

  echo "$payload";

  $curl_session = curl_init();
  curl_setopt($curl_session, CURLOPT_URL, $fcm_path);
  curl_setopt($curl_session, CURLOPT_POST, true);
  curl_setopt($curl_session, CURLOPT_HTTPHEADER, $header);
  curl_setopt($curl_session, CURLOPT_RETURNTRANSFER, true);
  curl_setopt($curl_session, CURLOPT_SSL_VERIFYPEER, false);
  curl_setopt($curl_session, CURLOPT_POSTFIELDS, $payload);

  $result = curl_exec($curl_session);

  curl_close( $curl_session);
  mysqli_close($con);

 ?>
