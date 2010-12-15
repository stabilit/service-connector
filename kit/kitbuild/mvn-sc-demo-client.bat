@echo off
call mvn-source-test-install-cmd.bat ..\..\java\demo-client

rem create demo-client.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\demo-client
copy ..\..\java\demo-client\target\demo-client.jar ..\bin /y