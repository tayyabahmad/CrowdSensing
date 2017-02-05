<?php

/*
*		Filename:	login.php
*		Date	:	Oct 2016
*		Desc	:	authenticate a user
*					>>
*					>> Request Type	:	POST
*					>> Input vars:		username, password 	
*					>> Print	: 		json
*/

	// check if the request type is POST
	if($_SERVER["REQUEST_METHOD"] == "POST"){
		
		$id = $username = $password = $response = $query = $conn = null;
		$data = Array();
		
		try{
			
			// check for username
			if(!empty($_POST["username"])){
				$username = $_POST["username"];
			}
			
			// check for password
			if(!empty($_POST["password"])){
				$password = $_POST["password"];
			}
			
			// query
			$query = "SELECT id FROM user WHERE username =? AND password =?";
			
			// attach file db-config.php and create slqi object
			require_once("db_config.php");
			$conn = connect();
			
			// create prepared statement
			$stmt = $conn->prepare($query);
			
			// bind parameter list and execute
			$stmt->bind_param('ss', $username, $password);
			$stmt->execute();
			
			// bind result
			$stmt->bind_result($id);
			if($stmt->fetch() && $id != null){
				
				// if authenticated, update response with success
				$response = "success";
			}
			else {
				// if not authenticated, update response with incorrect password or username
				$response = "incorrect";
			}
		}catch(Exception $e){
			
			// if exception occur, show it, and update response
				$response = "error";
		}
		finally{
			// close connection
			mysqli_close($conn);
		}
		
		// attach response 
		$data[0]["response"] = $response;
		$data[0]["id"] = $id;
		
		// output JSON array
		echo json_encode($data);
	}
?>