rem  ** ATTENTION **
rem  DO NOT CHANGE THE NAME AND LOCATION OF THIS SCRIPT
rem  IT IS REFERENCED BY THE SCRIPT GENERATING THE WINDOWS SERVICES
rem
rem sc-console is used to stop SC
rem java -Dlog4j.configuration=file:%~dp0..\conf\log4j-console.properties -jar %~dp0sc-console.jar -config %~dp0../conf/sc.properties kill
java -Dlog4j.configuration=file:%~dp0..\conf\log4j-console.properties -jar %~dp0sc-console.jar -h localhost -p 9000 kill