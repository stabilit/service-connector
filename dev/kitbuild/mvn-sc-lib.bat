@echo off


call mvn-source-test-install-cmd.bat ..\..\java\sc-lib

rem create sc-lib-Final.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-lib
copy ..\..\java\sc-lib\target\sc-lib-Final.jar  ..\bin /y
