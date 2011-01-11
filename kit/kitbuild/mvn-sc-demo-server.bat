@echo off
call mvn-source-test-install-cmd.bat ..\..\java\demo-server

rem create demo-server.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\demo-server
copy ..\..\java\demo-server\target\demo-server.jar ..\bin /y
copy ..\..\java\demo-server\scr\main\resources\*.php ..\bin /y