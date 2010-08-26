@echo on
rem StartSC
java -Dlog4j.configuration=file:src\test\resources\log4j.properties -jar ..\service-connector\target\sc.jar -filename src\test\resources\scIntegration.properties