rem  ** ATTENTION **
rem  DO NOT CHANGE THE NAME AND LOCATION OF THIS SCRIPT
rem  IT IS REFERENCED BY THE SCRIPT GENERATING THE WINDOWS SERVICES
rem
rem set default directory
set DIRNAME=%CD%
cd "%~dp0"
rem sc-console is used to send kill command to SC
rem either configuration file 
rem 		 -config ../conf/sc.properties or 
rem or host and port
rem 		-h localhost -p 9000
rem must eb provided to reach the SC
java -Dlog4j.configuration=file:..\conf\log4j-console.properties -jar sc-console.jar -config ../conf/sc.properties kill
cd %DIRNAME%