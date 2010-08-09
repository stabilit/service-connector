@echo on
rem StartSC
cd ..\dist
java -Dlog4j.configuration=file:..\config\log4j.properties -jar sc.jar -filename ..\config\sc.properties
cd ..\scripts