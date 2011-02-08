@echo off

call mvn-source-test-install-cmd.bat ..\..\java\service-connector
call mvn-assembly-cmd.bat ..\..\java\service-connector
copy ..\..\java\service-connector\target\sc.jar ..\bin /y
copy ..\..\java\service-connector\src\main\resources\sc.properties ..\conf /y
copy ..\..\java\service-connector\src\main\resources\sc-specific.properties ..\conf /y
copy ..\..\java\service-connector\src\main\resources\log4j.properties ..\conf\log4j-sc.properties /y
