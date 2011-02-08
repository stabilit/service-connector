@echo off

del ..\doc\apidocs\*.* /Q/F
call mvn-javadoc-cmd.bat ..\..\java\sc-lib
xcopy ..\..\java\sc-lib\target\site\apidocs ..\doc\javadoc\ /y /e
