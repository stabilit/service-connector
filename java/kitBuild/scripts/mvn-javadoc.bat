del /F/Q ..\doc\javadoc\*.* 
call mvn-javadoc-cmd.bat ..\..\sc-lib
xcopy ..\..\sc-lib\target\site\apidocs 	..\doc\javadoc\ /y /e