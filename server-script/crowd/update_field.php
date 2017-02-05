<?php

/*
*		Filename:	update_field.php
*		Date	:	Dec 2016
*		Desc	:	update the given field with the given value
*					>>
*					>> Request Type	:	POST
*					>> Input vars:		user-id, field, value 	
*					>> Print	: 		json
*/

	// check if the request type is POST
	if($_SERVER["REQUEST_METHOD"] == "POST"){
		
		$id = $password = $field = $response = $query = $conn = null;
		$data = Array();
		
		try{
			
			// check for user-id
			if(!empty($_POST["id"])){
				$id = $_POST["id"];
			} else {
				echo 'User-ID not found';
				exit();
			}
			
			// check for field
			if(!empty($_POST["field"])){
				$field = $_POST["field"];
			} else {
				echo 'Field not found';
				exit();
			}
			
			// check for value
			if(!empty($_POST["value"])){
				$value = $_POST["value"];
			} else {
				echo 'Value not found';
				exit();
			}
			
			// query
			switch($field){
				case 'email':
					$query = "UPDATE user SET email_id = ? WHERE id = ?";
					break;
				case 'password':
					$query = "UPDATE user SET password = ? WHERE id = ?";
					break;
				case 'username':
					// function check_username(username, user-id) return {success,taken}
					$query = "SELECT check_username(?, ?);";
					break;
			}
			
			// attach file db-config.php and create slqi object
			require_once("db_config.php");
			$conn = connect();
			
			// create prepared statement
			$stmt = $conn->prepare($query);
			
			// bind parameter list and execute
			$stmt->bind_param('ss', $value, $id);
			$stmt->execute();
			
			//incase of username, check if whether username exists..
			if($field == 'username'){
				$stmt->bind_result($res);
				if($stmt->fetch())
				if($res == 'success')
					$response = 'success';
				else 
					$response = $res;
				else 
					$response = 'error';
			}
			else{
				// if authenticated, update response with success
				$response = "success";
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