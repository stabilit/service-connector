@echo off
call mvn-source-test-install-cmd.bat ..\..\java\demo-server

rem create demo-server.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\demo-server
copy ..\..\java\demo-server\target\demo-server.jar ..\bin /y
copy ..\..\java\demo-serverd\src\main\resources\*.php ..\httpd\up-download /y
copy ..\..\java\demo-server\src\main\resources\hpptd-sc.conf ..\httpd\config /y