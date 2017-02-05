<?php

/*
*		Filename:	post.php
*		Date	:	Oct 2016
*		Desc	:	This script add new post
*					>>
*					>> Request Type	:	POST
*					>> Input vars: 		post-text, post-img, location, user-id, upload-user
*					>> Print	: 		json
*/

	// check if the request type is GET
	if($_SERVER["REQUEST_METHOD"]=="POST"){
	
		$conn = $query = $response = $text = $user_id = $loc = $img = $upload_user = null;
		$max_id = null;
		$data = array();
		
		try{
			
			// check for post-text
			if(!empty($_POST["text"])){
				$text = $_POST["text"];
			}
			
			// check for location
			if(!empty($_POST["loc"])){
				$loc = $_POST["loc"];
			}
			
			// check for user-id
			if(!empty($_POST["user_id"])){
				$user_id = $_POST["user_id"];
			}
			
			// check for upload-user
			if(!empty($_POST["upload_user"])){
				$upload_user = $_POST["upload_user"];
			}
			
			// contribution/post insert query
			$query = "INSERT INTO post(post_text, location, date, modified_date, user_id, upload_user) ".
					 " VALUES(?, ?,now(), now(), ?, ?);";
			
			// set slqi var from file db_cofig.php
			require_once("db_config.php");
			$conn = connect();
					 
			// create prepare statement
			$stmt = $conn->prepare($query);
			
			// bind parameters and execute
			$stmt->bind_param('ssss', $text, $loc, $user_id, $upload_user);
			$stmt->execute();
			
			// get the id of inserted post
			$max_id = $stmt->insert_id;
			
			// if error occure
			if($stmt->error){
				$response = "error";
			} else {
			
			// update response
			$response = "success";
			
				// check for image in post
				if(!empty($_POST["img"])){
					
					// if there is an image, set post-id as image-name and update table
					$query = "UPDATE post SET img = concat(id, '.', 'jpg') WHERE id = ?";
					$stmt = $conn->prepare($query);
					$stmt->bind_param('s', $max_id);
					$stmt->execute();
			
					// decode image from base64 string
					$img = $_POST["img"];
					$img = base64_decode($img);
					$img = imagecreatefromstring($img);
					
					if ($img !== false) {
						
						// save image in img directory
						header('Content-Type: img/jpeg');
						imagejpeg($img, "img/" . $max_id .".jpg");
						imagedestroy($img);
					}
					else {
						
						// if there is an error, update response
						$response =  'img-error';
					}
				}
			}
		
	
		}
		catch(Exception $exp){
			
			// if exception occure, update response
			$response = $exp.getMessage();
		}
		finally {
			
			// close connection
			mysqli_close($conn);
			
			// add response to JSON
			$data[0]["response"] = $response;
			
			// print output array in JSON format
			echo json_encode($data);
		}
	}
?>