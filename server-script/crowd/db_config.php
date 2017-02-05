<?php

/*
*		Filename:	db_config.php
*		Date	:	Oct 2016
*		Desc	:	This script provide an sqli object via function connect()
*/

		
	// connect() which return sqli object
	function connect(){
			
		// credentials
		$servername = "localhost";
		$database = "crowd_imp";	
		$username = "root";
		$password = "";
			
		// create an instance
		$conn = new mysqli($servername,  $username, $password, $database);
			
		// if there is an error exit the program
		// else return the object
		if($conn->connect_error){
			die("Database connection error." . connect_error);
			}
		else 
			return $conn;
	}

?>