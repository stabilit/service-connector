rem
rem for available commands invoke sc-console without command and see the output:
rem java -Dlog4j.configuration=file:..\conf\log4j-console.properties -jar sc-console.jar
rem
rem show state of service "session-1"
java -Dlog4j.configuration=file:..\conf\log4j-console.properties -jar sc-console.jar -h localhost -p 9000 state?serviceName=session-1