@echo off

rem create sc-lib-final.jar and copy to sc-test target dir
call mvn-assembly-cmd.bat ..\..\sc-lib
copy ..\..\sc-lib\target\sc-lib-all.jar  ..\..\sc-test\target /y

rem create sc.jar and copy it to sc-test target dir
call mvn-assembly-cmd.bat ..\..\service-connector
copy ..\..\service-connector\target\sc.jar ..\..\sc-test\target /y
copy ..\..\demo-server\src\main\resources\*.php ..\..\sc-test\up-download /y

rem create sc-test
call mvn-assembly-cmd.bat ..\..\sc-test

rem create sc-web.jar
call mvn-assembly-cmd.bat ..\..\sc-web
