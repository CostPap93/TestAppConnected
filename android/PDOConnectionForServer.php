<?php
try{
$conn = new PDO ("sqlsrv:Server=Server;Database=DatalabsEdu","JobadsUser","1Dat@labs!");
$conn -> setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION);
echo "sss";
}
catch(Exception $e){
	die(print_r($e->getMessage()));
}