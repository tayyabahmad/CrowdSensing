<?php

/*
*		Filename:	get_data.php
*		Date	:	Oct 2016
*		Desc	:	This script provide a list of posts or contributions of the users
*					order by time desc
*					>>
*					>> Request Type	:	GET
*					>> Input var: user_id - to check whether this user have already upvoted/downvoted the contribution.
*					>> Print	: json
*/

	// check if the request type is GET
	if($_SERVER["REQUEST_METHOD"] == "GET"){
		
		$conn = $query = $response = $userId = null;
		$data = Array();
		
		// check for user_id, if it's not found exit the script
		if(!empty($_GET["id"])){
			$userId = $_GET["id"];
		} else{
			echo 'User ID not found..';
			exit();
		}
		
		// the BIG query
		$query = "SELECT p.id, p.post_text, p.location, p.img, p.date, p.modified_date,".
				" (SELECT IFNULL(COUNT(impression),0) FROM post_impression WHERE".
				" post_id = p.id AND impression = 1) AS upvotes, (SELECT IFNULL(COUNT(impression),0)".
				" FROM post_impression WHERE post_id = p.id AND impression = -1) AS downvotes,".
				" IFNULL((SELECT impression FROM post_impression AS pi WHERE" .
				" post_id = p.id  AND pi.user_id = ? LIMIT 1),0) AS voted,".
				" p.user_id,".
				" (select uus.username from user as uus where uus.id = p.upload_user)".
				" FROM post AS p INNER JOIN user AS us ON us.id = p.user_id".
				" ORDER BY p.date DESC";
		
		// set slqi var from file db_cofig.php
		require_once("db_config.php");
		$conn = connect();
		
		// create prepared statment
		$stmt = $conn->prepare($query);
		
		//bind input parameter list and execute the query
		$stmt->bind_param('s', $userId);
		$stmt->execute();
		
		// if error found
		if($stmt->errno){
			$response = "error";
			exit();
		}
		
		/*	OUTPUT-COLUMNS :
		*		post-id, post-text, post-location, post-image, post-date, post-mod-date,
		*		upvotes, downvotes, whether_upvoted-by-this-user,
		*		contributer-id, contributer-name
		*/ 
		$stmt->bind_result($id, $text, $loc, $img, $date, $mod_date, $upvotes, $downvotes, $voted,
						 $user_id, $username);
		
		// attach file image_to_bytes.php :: 
		require_once("img_to_bytes.php");
		
		/*
		*	if contribution contains an image,
		*	attach the image and convert that into base42-string
		*/
		while($stmt->fetch()){
			/*if($img != null){
				$img = convert($img);
				$img = base64_encode($img);
			} */
			
			//set server - image directory
			if($img != null){
				$img = "img/" . $img;
			}
			// make a JSON row of the selected data				
			$row = array('id'=>$id, 'text'=>$text, 'loc'=>$loc, 'img'=> $img, 'date'=>$date, 
					'mod_date'=>$mod_date, 'upvotes'=>$upvotes, 'downvotes'=>$downvotes, 
					'voted'=>$voted, 'user_id'=>$user_id, 'username'=>$username);
			
			// add data-row into an output array			
			$data[] = $row;
		}
		
		// close the connection
		mysqli_close($conn);
		
		// update resposne status
		$response  = "success";
		
		// add response to output array
		$data[0]["response"] = $response;
		
		// print output array in JSON format
		echo json_encode($data, JSON_UNESCAPED_SLASHES);
	}
	
?>