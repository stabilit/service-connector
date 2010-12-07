rem sh state of service "session-1"
java -Dlog4j.configuration=file:..\config\log4j-con.properties -jar ..\bin\scconsole.jar -h localhost -p 9000 state=session-1