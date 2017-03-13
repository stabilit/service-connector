rem set default directory
cd "%~dp0"
rem
rem for available commands invoke sc-console without command and see the output:
rem java -Dlogback.configurationFile=file:..\conf\logback-console.xml -jar sc-console.jar
rem
rem show state of service "session-1"
java -Dlogback.configurationFile=file:..\conf\logback-console.xml -jar sc-console-${sc.version}.jar -h localhost -p 9000 state?serviceName=session-1