<?php

/*
*		Filename:	img_to_bytes.php
*		Date	:	Oct 2016
*		Desc	:	This script contain a function which convert an image into
*					byte-array-string
*/


	// funtion take an 'image-path' is an input
	// and return byte array of the image
	function convert($filename){
		
		// set image directory
		$filename = "img/" . $filename;
		
		// if image doesn't exits get the default one
		if(!file_exists($filename)){
			
			// default image path
			$filename = "img/default_img.png";
		}
		
		// open file, and  conversion process..
		// do you know what is 'rb'? I don't know let's read about it..
		$file = fopen($filename, "rb");
		$contents = fread($file, filesize($filename));
		fclose($file);
		
		// return base42-string
		return $contents;
	}
?>
