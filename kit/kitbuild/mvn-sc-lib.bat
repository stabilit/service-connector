@echo off

call mvn-source-test-install-cmd.bat ..\..\java\sc-lib
call mvn-assembly-cmd.bat ..\..\java\sc-lib
copy ..\..\java\sc-lib\target\sc-lib-all.jar  ..\bin /y
copy ..\..\java\sc-lib\target\sc-lib-all-sources.jar  ..\sources /y
