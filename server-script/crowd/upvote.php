<?php

/*
*		Filename:	upvote.php
*		Date	:	Oct 2016
*		Desc	:	This script is used to either upvote or downvote a contribution
*					>>
*					>> Request Type	:	GET
*					>> Input vars:		user-id, post-id, vote(1 for upvote & -1 for downvote) 
*					>> Print	: 		upvotes and downvotes of this post
*/

// check if the request type is GET
if($_SERVER["REQUEST_METHOD"] == "GET"){

	$query = $conn = $response = $user_id = $post_id = $vote  = null;
	$data = array();

	try{
		
		// check for input value of vote, if not found then exit script
		if(!empty($_GET["vote"])){
			$vote = $_GET["vote"];
		} else{
			$vote = "0";
		}
		
		// check for input value of user_id, if not found then exit script
		if(!empty($_GET["user_id"])){
			$user_id = $_GET["user_id"];
		} else{
			echo 'User ID not found..';
			exit();
		}
		
		// check for input value of post_id, if not found then exit script
		if(!empty($_GET["post_id"])){
			
			$post_id = $_GET["post_id"];
		} else{
			echo 'Post ID not found..';
			exit();
		}
		
		// query to check if this post is already voted by the user 
		$query = "SELECT (SELECT COUNT(*) FROM post_impression WHERE user_id = ? AND post_id = ?)";
		
		// set slqi var from file db_cofig.php
		require_once("db_config.php");
		$conn = connect();
		
		// create prepared statment
		$stmt = $conn->prepare($query);
		
		// bind parameters & execute
		$stmt->bind_param("ss", $user_id, $post_id);
		$stmt->execute();
		
		// if err found
		if($stmt->errno){
			$response = "error";
			exit();
		}
		// bind result 
		$stmt->bind_result($var1);
		if($stmt->fetch()){
			
			// check if user have already voted
			if($var1 == "0"){
				
				// vote the post
				$query = "INSERT into post_impression(impression, user_id, post_id) values(?, ?, ?);";
				
				// create new object
				$conn = connect();
				
				// create prepared statement
				$stmt = $conn->prepare($query);
		
				// add parameter list and execute
				$stmt->bind_param("sss", $vote, $user_id, $post_id);
				$stmt->execute();
			}
			else{
				
				// update user vote of this post
				$query = "UPDATE post_impression set impression = ? WHERE user_id = ? AND post_id = ?";
				
				// create new object
				$conn = connect();
				
				// create prepared statement
				$stmt = $conn->prepare($query);
				
				// add parameter list and execute
				$stmt->bind_param("sss", $vote, $user_id, $post_id);
				$stmt->execute();
			}
			
			// query to get upvotes and downvotes of a post
			$query = "SELECT (SELECT IFNULL(COUNT(impression),0) FROM post_impression WHERE".
					 " post_id = p.id AND impression = 1) AS upvotes, (SELECT IFNULL(COUNT(impression),0)".
					 " FROM post_impression WHERE post_id = p.id AND impression = -1) AS downvotes FROM".
					 " post_impression As pi INNER JOIN post AS p on p.id = pi.post_id and ".
					 " pi.post_id = ? LIMIT 1";
					 
			// create new prepared statement		 
			$conn = connect();
			$stmt = $conn->prepare($query);
			
			// add paramter list and execute
			$stmt->bind_param('s',$post_id);
			$stmt->execute();
			
			// bind result
			$stmt->bind_result($upvotes, $downvotes);
			if($stmt->fetch()){
				$data[0]["upvotes"] = $upvotes;
				$data[0]["downvotes"] = $downvotes;
			}
			
			// update response
			$response = "success";
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