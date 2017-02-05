
<!--  
		filename:	user_trust
		date	:	Oct 2016
		desc	: 	HTML table which show user trust level
!-->

<html>
	<head>
		<title> User Trust Manager </title>
	</head>
	<style>
	body{
	color:#333;	
	}
	div{
		width:75%;
	}
	table{
		width:100%;margin-top:30px;background-color:#eee;
		border-color:#ccc;
	}
	.tr-head{
		color:#555;background-color:#ccc;cell-padding:10px;
	}
	</style>

	<body >
		<br />
		<center>
			<div>
				<h3 align = "center"> User Trust Manager
				</h3>
				<table>
					<tr class="tr-head">
						<th> Username</th>
						<th> Email-id</th>
						<th> Trust Level (in % )</th>
					</tr>
					<?php echo data() ?>
				</table>
			</div>
		</center>
	</body>
</html>

<?php

// get data in HTML format
function data(){
	
	// create sqli instance
	require_once("db_config.php");
	$conn = connect();
	
	// query for trust level
	$query = "SELECT u.username, u.email_id, ifnull(ROUND((SUM(ROUND(IFNULL((SELECT COUNT(pi.impression) FROM post_impression".
			 " AS pi WHERE pi.post_id = p.id AND pi.impression = 1)/(SELECT COUNT(pi.impression) ".
			 " FROM post_impression AS pi WHERE pi.post_id = p.id ),0),2)*100)/(SUM(((SELECT COUNT(pi.id) FROM ".
			 " post_impression AS pi WHERE pi.post_id = p.id)))*100)),2),0)*100 AS trust FROM user AS u ".
			 " LEFT JOIN post AS p ON p.user_id = u.id  group by u.username";
	
	 $query = "SELECT u.username, u.email_id,  IFNULL(ROUND(SUM(IFNULL((SELECT COUNT(pi.impression) ".
					  " FROM post_impression AS pi WHERE pi.post_id = p.id AND pi.impression = 1), 0)*100/ ".
					  "(SELECT COUNT(pi.impression) FROM post_impression AS pi WHERE pi.post_id = p.id))/ ".
					  " (SELECT COUNT(pp.id) FROM post AS pp WHERE pp.user_id = u.id) , 2),0) AS trust FROM post ".
					  " AS p RIGHT JOIN user AS u ON p.user_id = u.id GROUP BY u.username";
	
	// create prepared statement
	$stmt = $conn->prepare($query);
	$stmt->execute();	
	
	// bind result
	$stmt->bind_result($username, $email, $trust);
	$formattedData = "";
	
	while($stmt->fetch()){
		
		// light-green background-color for high trusted contributer
		// yellowish background-color for medium trusted contributer
		// redish background-color for medium trusted contributer
		$back_color = null;
		if($trust>69)
			$back_color="#78AB46";
		else if($trust > 49)
			$back_color="#FFD700";
		else 
			$back_color = "#EE2C2C";
	
		// parse data into HTML formate
		$temp = "<tr> ".
				"<td ><a href='user_trust_ind.php?us=".$username."'>" . $username . "</a></td>".
				"<td style='color:".$back_color."'>" . $email . "</td>".
				"<td style='color:".$back_color."'>" . $trust. "</td> </tr>";
				$formattedData = $formattedData . $temp;
	}
	return $formattedData;
}
?>