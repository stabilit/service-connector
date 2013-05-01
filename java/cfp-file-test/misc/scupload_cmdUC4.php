<?php
header("Content-Type: text/plain");
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
# Instructions for use:
# 1) Put this file into the directory to which files will be uploaded
# 2) Configure sc.properties, define file service and remote node
#			see sc.properties for details
# 3) Configure the web server (apache) alias corresponding to the path to 
#    get files from the right directory see also httpd-sc.conf
# 4) check all parameters in the php.ini acording to 
#		http://www.radinks.com/upload/config.php
#	 especially: memory_limit, upload_max_filesize, max_execution_time, max_input_time 
#
# This script will be invoked from SC via the url:
# 	 http://host:port/path/scupload.php?filename=remoteFileName&servicename=file-1
#
# Mail option:
#  Optionally the script may send a notification mail when a file has been 
#  successfully uploaded. 
#  You must configure the smtp server in php.ini or use ini_set in order to 
#  allow mail communication
# ------------------------------------------------------------------------------
*/

// send mail configuration 
$sendMailFlag = 0;		// send mail flag, (0 = will NOT sent mail)

try {
	/* PUT data in stdin Stream */
	$putdata = fopen("php://input","r");
	$fileName = null;	// no default
	$service = null;	// no default
	if (isset($_REQUEST['filename'])) {
		$fileName = $_REQUEST['filename']; 
	}
	if (isset($_REQUEST['servicename'])) {
		$service = $_REQUEST['servicename']; 
	}
} catch(Exception $e) {
	header($_SERVER["SERVER_PROTOCOL"]." 500 Server Error - ".$e->getMessage());
	echo 'exception: '.$e->getMessage().'<br/>';
	exit;
}
// check input params
if ($fileName == null) {
	header($_SERVER["SERVER_PROTOCOL"]." 400 Bad Request - filename is missing");
	echo 'filename is missing<br/>';
	exit;
}
if ($fileName == "") {
	header($_SERVER["SERVER_PROTOCOL"]." 400 Bad Request - filename is empty");
	echo 'filename is empty<br/>';
	exit;
}
if ($service == null) {
	header($_SERVER["SERVER_PROTOCOL"]." 400 Bad Request - servicename is missing");
	echo 'servicename is missing<br/>';
	exit;
}
if ($service == "") {
	header($_SERVER["SERVER_PROTOCOL"]." 400 Bad Request - servicename is empty");
	echo 'servicename is empty<br/>';
	exit;
}

// process the input stream
try {
	/* Open file to write */
	$fp = fopen($fileName,"w");
	
	/* Loop - reading 1 Kb and write it to file */
	while ($data = fread($putdata,1024)) {
		fwrite($fp,$data);
	}
	/* Close the stream */
	fclose($fp);
	fclose($putdata);
} catch(Exception $e) {
	header($_SERVER["SERVER_PROTOCOL"]." 500 Server Error - ".$e->getMessage());
	echo 'exception: '.$e->getMessage().'<br/>';
	exit;
}

system('@CFP_PROC.COM '.$fileName,$retval);
echo 'Command procedure called '.$retval;

// optionally send mail
if ($sendMailFlag == 1) {
	//set error handler	(unfortunatelly there is no other way to catch the errors in the mail function!!)
	set_error_handler("mailErrorHandler");

	$mailTo = array("Joel Traber <joel.traber@stabilit.ch>", "Jan Trnka <jan.trnka@stabilit.ch>");	// mail To: list is required
	$mailCc = array("INFO CC <info@stabilit.ch>");			// mail CC list or null
	$mailBcc = null; 										// mail BCC list or null
	$mailFrom = "Service-Connector <info@stabilit.ch>";	    // mail sender required
	$subject = "Upload file notification for service=".$service;
	$message = "File=".$fileName." has been uploaded via service=".$service." at=".date("Y-m-d H:i:s");

	// create mail headers
	$headers .= 'From:' .$mailFrom ."\r\n";
	$headers .= 'Reply-To:' .$mailFrom ."\r\n"; 
	$headers .= 'X-Mailer: PHP/' .phpversion() ."\r\n"; 
	$headers .= "Content-type: text/plain\r\n";

	// extract mail adresses 
	$mailToString = implode(',', $mailTo);
	if ($mailCc !== null) $headers .= 'Cc: ' .implode(',', $mailCc) ."\r\n";
	if ($mailBcc !== null) $headers .= 'Bcc: ' .implode(',', $mailBcc) ."\r\n";

	// set optional processing params
	$params = '-f '.$mailFrom;

	// send mail
	$ret = mail($mailToString, $subject, $message, $headers, $params);
	if ($ret == true) {
		echo "service: ".$service.", file: ".$fileName." upload mail notification sent to: ".$mailToString."\r\n";
	}
} else {
	echo "service: ".$service.", file: ".$fileName." uploaded, no mail notification sent.\r\n";
}

function mailErrorHandler($errno, $errstr)
	{
	header($_SERVER["SERVER_PROTOCOL"]." 500 Server Error - sending mail failed Error:[$errno] $errstr");
	}
?>
