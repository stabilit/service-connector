@echo on
rem StartSC
java -Dlog4j.configuration=file:..\config\log4j.properties -jar ..\dist\sc.jar -filename ..\config\sc.properties