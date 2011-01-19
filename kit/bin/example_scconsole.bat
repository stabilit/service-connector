rem
rem show state of service "session-1"
rem
java -Dlog4j.configuration=file:..\config\log4j-con.properties -jar ..\bin\scconsole.jar -h localhost -p 9000 state=session-1