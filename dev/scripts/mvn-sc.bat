@echo off

call mvn-source-test-install-cmd.bat ..\..\java\sc-impl
call mvn-source-test-install-cmd.bat ..\..\java\service-connector

rem create service-connector sc.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\service-connector
copy ..\..\java\service-connector\target\sc.jar ..\bin /y
