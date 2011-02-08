rem create sc-lib-final.jar and copy to sc-test target dir
call mvn-assembly-cmd.bat ..\..\java\sc-lib
copy ..\..\java\sc-lib\target\sc-lib-all.jar  ..\..\java\sc-test\target /y

rem create sc.jar and copy it to sc-test target dir
call mvn-assembly-cmd.bat ..\..\java\service-connector
copy ..\..\java\service-connector\target\sc.jar ..\..\java\sc-test\target /y
copy ..\..\java\demo-server\src\main\resources\*.php ..\examples\httpd\up-download /y

rem create sc-test
call mvn-assembly-cmd.bat ..\..\java\sc-test
