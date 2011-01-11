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
# 							 		instructions for use  								
# - Call the script http://host:port/path/scupload.php?name=remoteFileName&service=demo
#		Substitute variable host, port, path and remoteFileName
#		(http://localhost:8080/sc/scupload.php?name=clientLog.txt&service=demo)
# - The script stores a stream in a file. Put it to the folders where you like
#		to store the files.
# - A declared file service on SC need to define the script in SC configuration
#	 	(sc.properties). file-1.uploadScript=scupload.php 
# - A notification mail is sent to all registered recipients in this script
#
# Optional parameters:
#  -mail: specifies if a mail notification is sent (1) or not (0)
#   sample (send mail)   : http://localhost:8080/sc/scupload.php?name=clientLog.txt&service=demo&mail=1
#   sample (no send mail): http://localhost:8080/sc/scupload.php?name=clientLog.txt&service=demo&mail=0
#   the default value is 1 if this flag is not set  
# ------------------------------------------------------------------------------
*/

/*******************************************************************************
 *******************************************************************************
  begin of configuration area  
*/
$printLog = 0;  // print the mail log (1) or not (0)

$mailHost = "mail.stabilit.ch";  // the mail smtp host address (e.g. localhost)
$mailPort = 25;                  // the mail smtp port (e.g. 25)
$greeting = "stabilit";          // the smtp ehlo greeting name

// send mail notification
$recipients = array("joel.traber@stabilit.ch", "jan.trnka@stabilit.ch");  // mail recipients list
$from = "joel.traber@stabilit.ch";                                        // mail sender address
$fromName = "Service-Connector";                                          // mail sender name
$replyTo = $from;                                                         // reply to mail address

/*
  end of configuration area, DO NOT TOUCH AFTER THIS POINT
 ********************************************************************************
*********************************************************************************/

/* PUT data in stdin Stream */
$putdata = fopen("php://input","r");
$fileName = null;  // no default
$service = null;   // no default
$sendMailFlag = 1; // send mail flag, default is 1 (say mail will be sent)
if (isset($_REQUEST['name'])) {
	$fileName = $_REQUEST['name']; 
}
if (isset($_REQUEST['service'])) {
	$service = $_REQUEST['service']; 
}
if (isset($_REQUEST['mail'])) {
	$sendMailFlag = $_REQUEST['mail']; 
}

if ($fileName == null || $service == null || $fileName == "" || $service == "") {
	header($_SERVER["SERVER_PROTOCOL"]." 400 Bad Request");
	echo "400 bad request\r\n";
	if ($fileName == null || $fileName == "") {
	   echo "parameter name is missing\r\n";
	}
	if ($service == null || $service == "") {
	   echo "parameter service is missing\r\n";
	}
	exit;
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
if ($sendMailFlag == 1) {
	try {
        $now = date("Y-m-d H:i:s");
		$subject = "service ".$service.", sc file ".$fileName." upload notification ".$now;
        $body = "service ".$service.", file ".$fileName." has been uploaded, time = ".$now;
		$size = count($recipients);
		for ($i = 0; $i < $size; $i++) {
			$recipient = $recipients[$i];
			$ret = sendMail($mailHost, $mailPort, $greeting, $from, $fromName, $replyTo, $recipient, $subject, $body, $now, $printLog);
			if ($ret == true) {
		        echo "service ".$service.", file ".$fileName." upload mail notification sent to ".$recipient."\r\n";
			} else {
		        echo "service ".$service.", file ".$fileName." upload mail notification send did fail ".$recipient."\r\n";
			}
		}
	} catch(Exception $e) {	  
	  echo $e;	
	}
} else {
    echo "service ".$service.", file ".$fileName." uploaded, no mail notification sent.\r\n";
}

/**
 * sendMail function
 * 
 * @param $host
 * @param $port
 * @param $recipient
 * @param $subject
 * @param $body
 * @param $header
 */
function sendMail($host, $port, $greeting, $from, $fromName, $replyTo, $recipient, $subject, $body, $date, $printLog) {
   try {
      $header = array(
        'Date' => date('r'),
        'From' => $fromName,
        'Sender' => $from,
        'Reply-To' => $replyTo,
        'Subject' => $subject,
        'To' => $recipient,
        'X-Mailer' => "X-Mailer: PHP/".phpversion(),	           
        'Content-Type' => 'text/plain; charset=utf-8');
     /* send mail */
     $smtp = new MailConnect($host, $port);
     $smtp->connect();
     $smtp->sendHelo($greeting);
     $smtp->sendFrom($from);
     $smtp->sendRcpt($recipient);
     $smtp->sendData($body, $header);
     $smtp->sendQuit();
     if ($printLog == 1) {
    	echo "---- begin mail log ------\r\n";
    	echo $smtp->getLog();
    	echo "---- end of mail log ------\r\n";
    	return 0;
     }
   } catch(Exception $e) {   	
   	    $smtp->log("Exception ".$e);
    	echo "---- begin mail log ------\r\n";
    	echo $smtp->getLog();
    	echo "---- end of mail log ------\r\n";
    	return 1;   	
   }	
}

/**
 * This is class connecting and sending mail 
 *
 */
class MailConnect
{
    private $host;
    private $port;
    private $sock;
    private $response = '';
    private $log = '';

    public function MailConnect($host='localhost', $port=25) {
        $this->host = $host;
        $this->port = $port;
    }
    
    public function connect()
    {
        $this->sock = @fsockopen($this->host, $this->port);
        if (!$this->sock) {
            throw new Exception("Connection failed, sock is null.");
        }
        if (!$this->check('220')) {
            throw new Exception("Connection failed, check 220 failure.");
        }
        // switch to non-blocking mode - just return data no response
        set_socket_blocking($this->sock, true);
        // set timeout of the server connection
        stream_set_timeout($this->sock, 0, 600000);
        return true;
    }
    
    public function sendHelo($greeting)
    {
    	$this->sendCmd("EHLO ".$greeting);
        if( !$this->check('250')) {
            throw new Exception("Failed to send EHLO.");
        }
        return true;
    }

    public function sendFrom($from)
    {
    	$this->sendCmd("MAIL FROM:<".$from.'>');
        if( !$this->check('250')) {
            throw new Exception("Failed to send address of sender.");
        }
        return true;
    }

    public function sendRcpt($to)
    {
        $this->sendCmd("RCPT TO:<".$to.">");
        if( !$this->check('250')) {
            throw new Exception("Failed to send recipient.");
        }
        return true;
    }
  
    public function sendData($message, $header)
    {
        $this->sendCmd('DATA');
        $i = 0;
        foreach( $header as $key => $value)
        {
            if( $i < count($header)-1 ) {
                $this->sendCmd($key.": ".$value);
            } else {
                $this->sendCmd($key.": ".$value."\r\n");
            }
            $i++;
        }    	
        if( !$this->check('354')) {
            throw new Exception("Data transfer did fail.");
        }
        // send the message
        $this->sendCmd($message."\r\n");
        $this->log($message);
        // send the end
        $this->sendCmd('.');
        $this->check('250');
    }
  
    public function sendQuit()
    {
        $this->sendCmd("QUIT");
        $this->check('221');
        fclose($this->sock);
        return true;
    }
    
    public function sendCmd($cmd)
    {
        $retCode = fputs($this->sock, $cmd."\r\n");
        $this->log("> ".$cmd);
    }

    public function getReply()
    {
        $go = true;
        $message = "";
        do
        {
            $tmp = fgets($this->sock, 1024);
            if($tmp === false) {
                $go = false;
            } else  {
                $message .= $tmp;
                if( preg_match('/^([0-9]{3})(-(.*[\r\n]{1,2})+\\1)? [^\r\n]+[\r\n]{1,2}$/', $message)) {
                	$go = false;
                }
            }
        } while($go);
        $this->log("< ".$message);
        return $message;
    }
    public function isValid()
    {
        $this->response = $this->getReply();
        return (empty($this->response) || preg_match('/^[5]/', $this->response)) ? false : true;
    }

    public function check($code)
    {
        if( $this->isValid() )
        {
            $pat = '/^'. $code .'/';
            if( preg_match($pat, $this->response)) {
                return true;
            }
        }
        return false;
    }
    
    public function log($str)
    {
        $this->log .= $str."\r\n";
    }
    
    public function getLog() {
    	return $this->log;
    }
}
?>
