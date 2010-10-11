@echo off
rem create javadoc and copies everything to documentation
call mvn-javadoc-cmd.bat ..\..\java\service-connector
xcopy ..\..\java\service-connector\target\site ..\documentation\ /y /e
