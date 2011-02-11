@echo off

call mvn-source-test-install-cmd.bat ..\..\java\demo-server
call mvn-assembly-cmd.bat ..\..\java\demo-server
copy ..\..\java\demo-server\target\demo-server.jar ..\bin /y
copy ..\..\java\demo-server\src\main\resources\log4j.properties ..\conf\log4j-demo-server.properties /y
copy ..\..\java\demo-server\src\main\resources\*.php ..\examples /y
copy ..\..\java\demo-server\src\main\resources\*.php ..\..\java\sc-test\up-download /y
copy ..\..\java\demo-server\src\main\resources\httpd-sc.conf ..\examples /y
copy ..\..\java\demo-server\target\*sources.jar  ..\sources /y