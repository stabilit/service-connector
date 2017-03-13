rem set default directory
cd "%~dp0"
rem start demo client
rem it will stop automatically
java -Dlogback.configurationFile=file:..\conf\logback-demo-client.xml -jar demo-client-${sc.version}.jar