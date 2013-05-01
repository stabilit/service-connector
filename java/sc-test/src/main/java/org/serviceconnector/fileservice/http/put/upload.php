<?php
/* PUT Daten kommen in den stdin Stream */
$putdata = fopen("php://input","r");
$flieName = "myputfile.txt";
if ($_REQUEST['name']) {
	$fileName = $_REQUEST['name']; 
}

/* Eine Datei zum Schreiben öffnen */
$fp = fopen($fileName,"w");

/* Jeweils 1kB Daten lesen und
   in die Datei schreiben */
while ($data = fread($putdata,1024)) {
	fwrite($fp,$data);
}
/* Die Streams schließen */
fclose($fp);
fclose($putdata);
?>
