@echo on
rem StartServer
cd ..\dist
java -Dlog4j.configuration=file:..\config\log4j.properties -jar server.jar
cd ..\scripts