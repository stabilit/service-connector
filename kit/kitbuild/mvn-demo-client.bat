@echo off

call mvn-source-test-install-cmd.bat ..\..\java\demo-client
call mvn-assembly-cmd.bat ..\..\java\demo-client
copy ..\..\java\demo-client\target\demo-client.jar ..\bin /y
copy ..\..\java\demo-client\src\main\resources\log4j.properties ..\conf\log4j-demo-client.properties /y
copy ..\..\java\demo-client\target\*sources.jar  ..\sources /y