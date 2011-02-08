@echo off

call mvn-source-test-install-cmd.bat ..\..\java\sc-console
call mvn-assembly-cmd.bat ..\..\java\sc-console
copy ..\..\java\sc-console\target\sc-console.jar ..\bin /y
copy ..\..\java\sc-console\src\main\resources\log4j.properties ..\conf\log4j-console.properties /y
