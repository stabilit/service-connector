del /F/Q ..\..\..\kit\doc\javadoc\*.* 
call mvn-javadoc-cmd.bat ..\..\sc-lib
xcopy ..\..\sc-lib\target\site\apidocs 	..\..\..\kit\doc\javadoc\ /y /e