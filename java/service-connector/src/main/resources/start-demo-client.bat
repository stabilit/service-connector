rem start demo client
rem it will stop automatically
java -Dlog4j.configuration=file:%~dp0..\conf\log4j-demo-client.properties -jar %~dp0demo-client.jar