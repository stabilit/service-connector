rem set default directory
cd "%~dp0"
rem start demo server
rem it will stop automatically
java -Dlogback.configurationFile=file:..\conf\logback-demo-server.xml -jar demo-server-${sc.version}.jar