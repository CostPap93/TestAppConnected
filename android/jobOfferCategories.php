<?php
include("PDOConnection.php");

define("ACTION_SHOW","show");
define("RESULT_SUCCESS",0);
define("RESULT_ERROR",1);
define("RESULT_USER_EXISTS",2);

$action = $_POST["action"];
$result = RESULT_ERROR;

if(isset($action)){
	
	
	if(ACTION_SHOW == $action){
		showOfferCategories($conn);
	}
}

function showOfferCategories($conn){
	$query = "SELECT jacat_id,jacat_title FROM JobAdCategories";
	$stmt = $conn->prepare($query);
	$stmt->execute();
	$i = 1;
	do {
		$rowset = $stmt->fetchAll(PDO::FETCH_ASSOC);
		echo '{"joboffercategories":' .(json_encode($rowset))."}";

	} while ($stmt->nextRowset());

}
?>