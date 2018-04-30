<?php
include("PDOConnection.php");
include("jobOfferCategories.php");

define("ACTION_SHOW_OFFERS","showOffers");
define("ACTION_SHOW_OFFERS_FROM_CAT","showOffersFromCategory");


if(isset($action)){
	$action = $_POST["action"];
	if(ACTION_SHOW_OFFERS == $action){
			showOffer($conn);
	}else{
			$jacat_id= $_POST["jacat_id"];
			showOfferFromCategories($conn,$jacat_id);
		
	}
}

function showOfferFromCategories($conn,$jacat_id){
	$query = "SELECT jad_id,jad_catid,jad_title,jad_date,jad_downloaded FROM (JobAds INNER JOIN JobAdCategories ON JobAds.jad_catid=JobAdCategories.jacat_id) WHERE JobAds.jad_catid=?  ORDER BY JobAds.jad_date DESC";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$jacat_id);
	$stmt->execute();
	$i = 1;
	do {
		$rowset = $stmt->fetchAll(PDO::FETCH_ASSOC);
		echo '{"offers":' .(json_encode($rowset))."}";

	} while ($stmt->nextRowset());
	
}
function showOffer($conn){
	$query = "SELECT jad_id,jad_catid,jad_title,jad_date,jad_downloaded FROM (JobAds INNER JOIN JobAdCategories ON JobAds.jad_catid=JobAdCategories.jacat_id) ORDER BY JobAds.jad_date";
	$stmt = $conn->prepare($query);
	$stmt->execute();
	$i = 1;
	do {
		$rowset = $stmt->fetchAll(PDO::FETCH_ASSOC);
		echo '{"offers":' .(json_encode($rowset))."}";

	} while ($stmt->nextRowset());
	
}
?>

