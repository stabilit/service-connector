@echo off
del ..\doc\apidocs\*.* /Q/F

rem create javadoc and copies everything to documentation
call mvn-javadoc-cmd.bat ..\..\java\sc-lib
xcopy ..\..\java\sc-lib\target\site\apidocs ..\doc\apidocs\ /y /e
