rem sc-console is used to stop sc
java -Dlog4j.configuration=file:%~dp0..\conf\log4j-console.properties -jar %~dp0sc-console.jar -h localhost -p 9000 kill