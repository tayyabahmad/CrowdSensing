<?php

/*
*		Filename:	confirm_password.php
*		Date	:	Dec 2016
*		Desc	:	Password Confirmation
*					>>
*					>> Request Type	:	POST
*					>> Input vars:		user-id, password 	
*					>> Print	: 		json
*/

	// check if the request type is POST
	if($_SERVER["REQUEST_METHOD"] == "POST"){
		
		$id = $password = $response = $query = $conn = null;
		$data = Array();
		
		try{
			
			// check for user-id
			if(!empty($_POST["id"])){
				$id = $_POST["id"];
			} else {
				echo 'User-ID not found';
				exit();
			}
			
			// check for password
			if(!empty($_POST["password"])){
				$password = $_POST["password"];
			} else {
				echo 'Password not found';
				exit();
			}
			
			// query
			$query = "SELECT id FROM user WHERE id =? AND password =?";
			
			// attach file db-config.php and create slqi object
			require_once("db_config.php");
			$conn = connect();
			
			// create prepared statement
			$stmt = $conn->prepare($query);
			
			// bind parameter list and execute
			$stmt->bind_param('ss', $id, $password);
			$stmt->execute();
			
			// bind result
			$stmt->bind_result($id);
			if($stmt->fetch() && $id != null){
				
				// if authenticated, update response with success
				$response = "success";
			}
			else {
				// if password is incorrect
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