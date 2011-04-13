del ..\kit-tmp\doc\javadoc\*.* /F/Q 
call mvn-javadoc-cmd.bat ..\..\sc-lib
xcopy ..\..\sc-lib\target\site\apidocs 	..\kit-tmp\doc\javadoc\ /y /e