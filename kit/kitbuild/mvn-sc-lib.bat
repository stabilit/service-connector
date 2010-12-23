@echo off

call mvn-source-test-install-cmd.bat ..\..\java\sc-lib

rem create sc-lib-final.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-lib
copy ..\..\java\sc-lib\target\sc-lib-final.jar  ..\bin /y
copy ..\..\java\sc-lib\target\sc-lib-final-sources.jar  ..\src /y

