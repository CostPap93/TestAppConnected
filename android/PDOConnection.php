<?php
$servername = "localhost";
$username = "root";
$password = "";

try{
$conn = new PDO ("mysql:host=$servername;dbname=android_db",$username,$password);
$conn -> setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION);
}
catch(Exception $e){
	die(print_r($e->getMessage()));
}