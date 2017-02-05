<?php

/*
*		Filename:	user_exists.php
*		Date	:	Oct 2016
*		Desc	:	This script is used to check whether the user already taken or not
*					>>
*					>> Request Type	:	GET
*					>> Input vars:		username
*					>> Print	: 		taken, exist
*/

// check if the request type is GET
if($_SERVER["REQUEST_METHOD"] == "GET"){

	$query = $conn = $response = $username  = null;
	$data = array();

	try{
		
		// check for input value of username, if not found then exit script
		if(!empty($_GET["user"])){
			$username = $_GET["user"];
		} else{
			echo 'Username not provided';
			exit();
		}
		
		// query to check if username exists
		$query = "SELECT IFNULL(COUNT(id), 0) FROM user WHERE username = ?";
		
		// set slqi var from file db_cofig.php
		require_once("db_config.php");
		$conn = connect();
		
		// create prepared statment
		$stmt = $conn->prepare($query);
		
		// bind parameters & execute
		$stmt->bind_param("s", $username);
		$stmt->execute();
		
		// if err found
		if($stmt->errno){
			$response = "error";
			exit();
		}
		// bind result 
		$stmt->bind_result($var1);
		if($stmt->fetch()){
			
			// check whether username exists and
			// update response accordingly
			if($var1 == "0"){
				$response = "exist";
			}
			else{
				$response = "taken";
			}
		}
	}
	catch(Exception $exp){
		
		// if there is an exception, update response
		$response = $exp.getMessage();
	}
	finally{
		
		// close connection
		mysqli_close($conn);
		
		// add response to JSON
		$data[0]["response"] = $response;
		
		// print output array in JSON format
		echo json_encode($data);
	}
}
		
	
?>