rem start demo server
rem it will stop automatically
java -Dlog4j.configuration=file:%~dp0..\conf\log4j-demo-server.properties -jar %~dp0demo-server.jar