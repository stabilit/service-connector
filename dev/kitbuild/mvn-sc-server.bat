@echo off
call mvn-source-test-install-cmd.bat ..\..\java\sc-server

rem create sc-server.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-server
copy ..\..\java\sc-server\target\server.jar ..\bin /y