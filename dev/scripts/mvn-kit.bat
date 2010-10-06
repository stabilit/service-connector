@echo off
rem Build Kit
rem kick-off maven to build all

rem first create sources and install all projects to local repository
call mvn-source-test-install-cmd.bat ..\..\java\sc-impl
call mvn-source-test-install-cmd.bat ..\..\java\service-connector
call mvn-source-test-install-cmd.bat ..\..\java\sc-console
call mvn-source-test-install-cmd.bat ..\..\java\sc-server
call mvn-source-test-install-cmd.bat ..\..\java\sc-client

rem create service-connector sc.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\service-connector
copy ..\..\java\service-connector\target\sc.jar ..\bin /y

rem create sc-cosole scconsole.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-console
copy ..\..\java\sc-console\target\scconsole.jar ..\bin /y

rem create server.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-server
copy ..\..\java\sc-server\target\server.jar ..\bin /y

rem create client.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\sc-client
copy ..\..\java\sc-client\target\client.jar ..\bin /y

rem create javadoc and copies everything to documentation
call mvn-javadoc-cmd.bat ..\..\java\service-connector
xcopy ..\..\java\service-connector\target\site ..\documentation\ /y /e

