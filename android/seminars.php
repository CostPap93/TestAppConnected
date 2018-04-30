<?php
include("PDOConnection.php");

define("ACTION_SHOW_OFFERS","showoffers");
define("ACTION_ENROLL","enrolled");
define("ACTION_SHOW_SEMINARS","showsem");
define("ACTION_SHOW_LATEST_OFFER","showlatestoffer");
define("RESULT_SUCCESS",0);
define("RESULT_ERROR",1);
define("RESULT_SEMINAR_EXISTS",3);
define("RESULT_SEMINAR_DOESNT_EXIST",4);

$action = $_POST["action"];
$result = RESULT_ERROR;

if(isset($action)){

	if(ACTION_SHOW_SEMINARS == $action){
			showSeminarName($conn);
			$result = RESULT_SUCCESS;
	}else if(ACTION_ENROLL == $action){
		$seminarid = $_POST["seminarid"];
		enrolledUsers($conn,$seminarid);
		$result = RESULT_SUCCESS;
	}else if(ACTION_SHOW_OFFERS == $action){
		$userid = $_POST["userid"];
		showOffers($conn,$userid);
		$result = RESULT_SUCCESS;
	}else{
		$userid = $_POST["userid"];
		showLatestOffersTime($conn,$userid);
		$result = RESULT_SUCCESS;
	}
		
	
		
}

echo(json_encode( array("result" => $result) ) );

			

function seminarExists($conn,$seminarname){
	$query = "SELECT * FROM SEMINARS WHERE NAME = ?";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$seminarname);
	$stmt->execute();
	$rowcount = $stmt->rowcount();
	//for debug
	//var_dump($rowcount);
	echo(json_encode(array("name" => $seminarname) ) );
	return $rowcount;
}

function insertSeminar($conn,$name,$startdate,$image){
	$query = "INSERT INTO SEMINARS(NAME,DESCRIPTION,CATEGORY,IMAGE,STARTDATE) VALUES (?,?,?)";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$seminarname);
	$stmt->bindParam(2,$image);
	$stmt->bindParam(3,$startdate);
	$stmt->execute();
}
function deleteSeminar($conn,$seminarname){
	$query = "DELETE FROM SEMINARS WHERE NAME = ?";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$seminarname);
	$stmt->execute();	
	$rowcount = $stmt->rowcount();
	//for debug
	//var_dump($rowcount);
	return $rowcount;
}

function showSeminarName($conn){
	$query = "SELECT seminarid,seminarname,startdate FROM SEMINAR";
	$stmt = $conn->prepare($query);
	$stmt->execute();
	$i = 1;
	do {
		$rowset = $stmt->fetchAll(PDO::FETCH_ASSOC);
		echo '{"seminars":' .(json_encode($rowset))."}";

	} while ($stmt->nextRowset());

}

function enrolledUsers($conn,$seminarid){
	$query = "SELECT user.username FROM ((userseminar INNER JOIN user ON userseminar.userid=user.userid) INNER JOIN seminar ON userseminar.seminarid= seminar.seminarid) WHERE seminar.seminarid=?;";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$seminarid);
	$stmt->execute();
	$i = 1;
	do {
		$rowset = $stmt->fetchAll(PDO::FETCH_ASSOC);
		echo '{"usernames":' .(json_encode($rowset))."}";

	} while ($stmt->nextRowset());
}

function showOffers($conn,$userid){
	$query = "SELECT offer.offertitle,offer.creationtime FROM ((offer INNER JOIN userinterest ON offer.offercategory=userinterest.interestcategory)INNER JOIN user ON user.userid=userinterest.userid) WHERE user.userid=? ORDER BY offer.creationtime";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$userid);
	$stmt->execute();
	$i = 1;
	do {
		$rowset = $stmt->fetchAll(PDO::FETCH_ASSOC);
		echo '{"offers":' .(json_encode($rowset))."}";

	} while ($stmt->nextRowset());
	
}

function showLatestOffersTime($conn,$userid){
	$query = "SELECT MAX(creationtime) AS maxcreate FROM (SELECT offer.offertitle,offer.creationtime FROM ((offer INNER JOIN userinterest ON offer.offercategory=userinterest.interestcategory)INNER JOIN user ON user.userid=userinterest.userid) WHERE user.userid=?) AS ct";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$userid);
	$stmt->execute();
	$rowset = $stmt->fetch(PDO::FETCH_ASSOC);
	echo (json_encode($rowset));
}
?>