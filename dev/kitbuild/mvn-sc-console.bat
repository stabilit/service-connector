@echo off
call mvn-source-test-install-cmd.bat ..\..\java\sc-console

rem create sc-console scconsole.jar and copy to bin dir
call mvn-jar-cmd.bat ..\..\java\sc-console
copy ..\..\java\sc-console\target\scconsole.jar ..\bin /y
