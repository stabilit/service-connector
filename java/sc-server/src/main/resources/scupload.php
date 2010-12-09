<?php
/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
/*
# 							 						instructions for use  								
#	- Call the script http://host:port/path/scupload.php?name=remoteFileName&service=demo
#		Substitute variable host, port, path and remoteFileName
#		(http://localhost:8080/sc/scupload.php?name=clientLog.txt&service=demo)
# - The script stores a stream in a file. Put it to the folders where you like
#		to store the files.
# - A declared file service on SC need to define the script in SC configuration
#	 	(sc.properties). file-1.uploadScript=scupload.php 
# - A notification mail is sent to all registered recipients in this script
# ------------------------------------------------------------------------------
*/
/* PUT data in stdin Stream */
$putdata = fopen("php://input","r");
$fileName = "myputfile.txt";
$service = "anonymous";
if ($_REQUEST['name']) {
	$fileName = $_REQUEST['name']; 
}
if ($_REQUEST['service']) {
	$service = $_REQUEST['service']; 
}

/* Open file to write */
$fp = fopen($fileName,"w");

/* Loop - reading 1 Kb and write it to file */
while ($data = fread($putdata,1024)) {
	fwrite($fp,$data);
}
/* Close the stream */
fclose($fp);
fclose($putdata);
try {
	ini_set("SMTP","mail.stabilit.ch");
	// send mail notification
	$recpients = array("joel.traber@stabilit.ch", "jan.trnka@stabilit.ch");
	$now = date("Y-m-d H:i:s");
	$subject = "service ".$service.", sc file ".$fileName." upload notification ".$now;
	$body = "service ".$service.", file ".$fileName." has been uploaded, time = ".$now;
	$header = 'From: ds@simtech-ag.ch'."\r\n".
	          'Reply-To: ds@simtech-ag.ch'."\r\n" .
	          'X-Mailer: PHP/' . phpversion();
	$size = count($recpients);
	for ($i = 0; $i < $size; $i++) {
		$recipient = $recpients[$i];
		$ret = mail($recipient, $subject, $body, $header);
		if ($ret == true) {
	        echo "service ".$service.", file ".$fileName." upload mail notification sent to ".$recipient."<br/>";
		} else {
	        echo "service ".$service.", file ".$fileName." upload mail notification send did fail ".$recipient."<br/>";
		}
	}
} catch(Exception $e) {
  echo $e;	
}	
?>
