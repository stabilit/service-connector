@echo off
call mvn-source-test-install-cmd.bat ..\..\java\sc-client

rem create sc-client client.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-client
copy ..\..\java\sc-client\target\client.jar ..\bin /y
