<?php

/*
*		Filename:	add-user.php
*		Date	:	Oct 2016
*		Desc	:	add a new user
*					>>
*					>> Request Type	:	POST
*					>> Input vars:		username, password, email	
*					>> Print	: 		json
*/

	// check if the request type is POST
	if($_SERVER["REQUEST_METHOD"] == "POST"){
		
		$username = $password = $email = $query = $resonse = $conn = null;
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
			
			// check for email
			if(!empty($_POST["email"])){
				$email = $_POST["email"];
			}
			
			//insert query
			$query = "INSERT INTO user(username, email_id, password) VALUES(?,?,?);";
			
			// create sqli object
			require_once("db_config.php");
			$conn = connect();
			
			// create prepared statement
			$stmt = $conn->prepare($query);
			
			// bind parameters and execute
			$stmt->bind_param("sss",$username, $email, $password);
			$stmt->execute();
			
			if($stmt->affected_rows>0){
				
				// if row added, then update response with success
				$response = "success";
			}
			else {
				// if row not added, then update response with success
				$response = "data_error";
			}
			
		}catch(Exception $e){
			// if execption thrown, update response recordingly.
			$response = "error";
		}
		finally{
			// close connection
			mysqli_close($conn);
		}
		
		// add response to JSON
		$data[0]["response"] = $response;
		
		// output JSON array
		echo json_encode($data);
		
	}

?>