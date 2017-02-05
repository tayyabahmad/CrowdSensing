<?php

/*
*		Filename:	trusted-user.php
*		Date	:	Oct 2016
*		Desc	:	This script provide a list of trusted users
*					>>
*					>> Request Type	:	GET
*					>> Input var: nothing
*					>> Print	: json
*/

	// check if the request type  is GET
	if($_SERVER["REQUEST_METHOD"]=="GET"){
		
		$query = $response = $id = $username = $trust = null;
		$data = array();
		
		// select those having trust >79
		$query = "SELECT u.id, u.username, ROUND((SUM(ROUND(IFNULL((SELECT COUNT(pi.impression) FROM post_impression " .
				 " AS pi WHERE pi.post_id = p.id AND pi.impression = 1)/(SELECT COUNT(pi.impression) ".
			     " FROM post_impression AS pi WHERE pi.post_id = p.id ),0),2)*100)/(SUM(((SELECT COUNT(pi.id) FROM ".
			     " post_impression AS pi WHERE pi.post_id = p.id)))*100)),2)*100 AS trust FROM post AS p ".
			     " INNER JOIN user AS u ON p.user_id = u.id having trust >79";
				 
			$query = "SELECT u.id, u.username ROUND(SUM(IFNULL(((SELECT COUNT(pi.impression) FROM".
			 " post_impression AS pi WHERE pi.post_id = p.id and pi.impression = 1) /".
			 " (SELECT COUNT(pi.impression) FROM post_impression AS pi WHERE".
			 " pi.post_id = p.id )),0)*100)/(SELECT COUNT(pp.id) FROM post pp".
			 " WHERE pp.user_id = u.id), 2) AS trust FROM post AS p INNER JOIN user AS u ON ".
			 " p.user_id = u.id HAVING trust >79";
			 
			 // 
			 $query = "SELECT u.id, u.username,  ROUND(SUM(IFNULL((SELECT COUNT(pi.impression) ".
					  " FROM post_impression AS pi WHERE pi.post_id = p.id AND pi.impression = 1), 0)*100/ ".
					  "(SELECT COUNT(pi.impression) FROM post_impression AS pi WHERE pi.post_id = p.id))/ ".
					  " (SELECT COUNT(pp.id) FROM post AS pp WHERE pp.user_id = u.id) , 2) AS trust FROM post ".
					  " AS p RIGHT JOIN user AS u ON p.user_id = u.id GROUP BY u.username HAVING trust >79";
		// set conn value
		require_once("db_config.php");
		$conn = connect();
		
		// create prepared statement and execute 	
	    $stmt = $conn->prepare($query);
		$stmt->execute();
		
		// if error found
		if($stmt->errno){
			$response = "error";
		}
		
		// bind result
		$stmt->bind_result($id, $username, $trust);
	
		while($stmt->fetch()){
			
			// make JSON row of the selected data and put it into an array
			$row = array('id'=>$id, 'username'=>$username, 'trust'=>$trust);
			$data[] = $row;
		}
 
		 if(count($data)>0){
			 
			 // if trusted amount > 0 update response to success
			 $response = "success";
		 } else {
			 // else update response to error
			 $response = "no-user";
		 }
		
		// put response into JSON array
		$data[0]["response"] = $response;
		
		// print output JSON array
		echo json_encode($data);
		
	}
?>