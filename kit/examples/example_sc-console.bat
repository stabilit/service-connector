rem
rem for available commands invoke sc-console without parameters and see output like:
rem java -Dlog4j.configuration=file:..\conf\log4j-console.properties -jar sc-console.jar
rem
rem show state of service "session-1"
java -Dlog4j.configuration=file:..\conf\log4j-console.properties -jar sc-console.jar -h localhost -p 9000 state=session-1