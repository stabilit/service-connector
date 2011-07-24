rem set default directory
cd "%~dp0"
rem start demo client
rem it will stop automatically
"%JAVA_HOME%\bin\"java -Dlog4j.configuration=file:..\conf\log4j-demo-client.properties -jar demo-client.jar