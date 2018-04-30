<?php
include("PDOConnection.php");
include("seminar.php");

define("ACTION_ADD_USER","add");
define("ACTION_LOGIN","login");
define("ACTION_SHOW","show");
define("RESULT_SUCCESS",0);
define("RESULT_ERROR",1);
define("RESULT_USER_EXISTS",2);

$action = $_POST["action"];
$result = RESULT_ERROR;

if(isset($action)){
	$username = $_POST["username"];
	$pwd = $_POST["password"];
	
	if(ACTION_ADD_USER == $action){
		if(isExistUser($conn,$username)){
			$result = RESULT_USER_EXISTS;
			echo(json_encode( array("result" => $result) ) );
		}
		else{
			insertUser($conn,$username,$pwd);
			$result = RESULT_SUCCESS;
			echo(json_encode( array("result" => $result) ) );
		}
	}
	else{
		if(login($conn,$username,$pwd)){
			$result = RESULT_SUCCESS;
			echo(json_encode( array("result" => $result,"id"=> intval(login($conn,$username,$pwd)) ) ) );
			
			
		}
		else{
			$result = RESULT_ERROR;
			echo(json_encode( array("result" => $result) ) );
		}
	}
}
			

function insertUser($conn,$username,$pwd){
	
	$query = "INSERT INTO USER(USERNAME,PASSWORD) VALUES (?,?)";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$username);
	$stmt->bindParam(2,$pwd);
	$stmt->execute();
	
}

function isExistUser($conn,$username){
	$query = "SELECT * FROM USER WHERE USERNAME = ?";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$username);
	$stmt->execute();
	$rowcount = $stmt->fetchColumn();
	//for debug
	//var_dump($rowcount);
	return $rowcount;
}

function login($conn ,$username,$pwd){
	$query = "SELECT id FROM USER WHERE USERNAME = ? AND PASSWORD = ?";
	$stmt = $conn->prepare($query);
	$stmt->bindParam(1,$username);
	$stmt->bindParam(2,$pwd);
	$stmt->execute();
	$rowcount = $stmt->fetchColumn();
	return $rowcount;
}




