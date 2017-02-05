
<!--  
		filename:	user_trust-ind
		date	:	Oct 2016
		desc	: 	HTML table which show user trust level of each contribution of a user
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
					<th> Post ID</th>
					<th> Posted Text</th>
					<th> Trust Level (in % )</th>
				</tr>
				<?php echo data() ?>
			</table>
		</div>
		</center>
	</body>
</html>


<?php

// return HTML formated data
function data(){

	$username = null;
	
	// search for username
	if(!empty($_GET["us"])){
		$username = $_GET["us"];
	}
	
	// query
	$query = "select p.id, p.post_text, round(ifnull(((select count(pi.impression) from post_impression ".
			 " as pi where pi.post_id = p.id and pi.impression = 1)/(select count(pi.impression) ".
			 " from post_impression as pi where pi.post_id = p.id )),0),2)*100 as trust from post p".
			 " inner join user u on p.user_id = u.id where u.username = ?";
			 
	// set sqli instance
	require_once("db_config.php");
	$conn = connect();
	
	// prepared statement
	$stmt = $conn->prepare($query);
	
	// bind parameter list and execute
	$stmt->bind_param('s', $username);
	$stmt->execute();
	
	// bind result
	$stmt->bind_result($id, $text, $trust);
	$formattedData = "";
	
	while($stmt->fetch()){
		
		// light-green background-color for high trusted contribution
		// yellowish background-color for medium trusted contribution
		// redish background-color for medium trusted contribution
		$back_color = null;
		if($trust>69)
			$back_color="#78AB46";
		else if($trust > 49)
			$back_color="#FFD700";
		else 
			$back_color = "#EE2C2C";
	
	
		// parse data into HTML formate
		$temp = "<tr> ".
				"<td style='color:".$back_color."'>" . $id . "</td>".
				"<td style='color:".$back_color."'>" . $text . "</td>".
				"<td style='color:".$back_color."'>" . $trust. "</td> </tr>";
				$formattedData = $formattedData . $temp;
	}
	return $formattedData;
}
?>