rem  ** ATTENTION **
rem  DO NOT CHANGE THE NAME AND LOCATION OF THIS SCRIPT
rem  IT IS REFERENCED BY THE SCRIPT GENERATING THE WINDOWS SERVICES
rem
rem set default directory
set DIRNAME=%CD%
cd "%~dp0"
rem sc-console is used to stop SC
java -Dlog4j.configuration=file:..\conf\log4j-console.properties -jar sc-console.jar -config ../conf/sc.properties kill
cd %DIRNAME%