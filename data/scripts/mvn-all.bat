@echo off
rem Build Kit
rem kick-off maven to build all

rem first create sources and install all projects to local repository
call mvn-source-test-install-cmd.bat ..\..\java\sc-api
call mvn-source-test-install-cmd.bat ..\..\java\sc-impl
call mvn-source-test-install-cmd.bat ..\..\java\service-connector
call mvn-source-test-install-cmd.bat ..\..\java\sc-unit

rem create service-connector sc.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\service-connector
copy ..\..\java\service-connector\target\sc.jar ..\bin /y

rem create sc-cosole scconsole.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-console
copy ..\..\java\sc-console\target\scconsole.jar ..\bin /y

rem create sc-simulation server.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-simulation
copy ..\..\java\sc-simulation\target\server.jar ..\bin /y

rem create sc-unit client.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-unit
copy ..\..\java\sc-unit\target\client.jar ..\bin /y

rem create javadoc and copies everything to documentation
call mvn-javadoc-cmd.bat ..\..\java\service-connector
xcopy ..\..\java\service-connector\target\site ..\documentation\ /y /e

