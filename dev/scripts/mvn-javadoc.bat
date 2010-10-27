@echo off
del ..\documentation\apidocs\*.* /Q/F

rem create javadoc and copies everything to documentation
call mvn-javadoc-cmd.bat ..\..\java\sc-impl
xcopy ..\..\java\sc-impl\target\apidocs ..\documentation\apidocs\ /y /e
