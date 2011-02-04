rem create sc-lib-final.jar and copy to sc-test target dir
call mvn-assembly-cmd.bat ..\..\java\sc-lib
copy ..\..\java\sc-lib\target\sc-lib-final.jar  ..\..\java\sc-test\target /y

rem create service-connector sc.jar and copy to sc-test target dir
call mvn-assembly-cmd.bat ..\..\java\service-connector
copy ..\..\java\service-connector\target\sc.jar ..\..\java\sc-test\target /y
copy ..\..\java\service-connector\src\main\resources\*.php ..\httpd\upload /y
copy ..\..\java\service-connector\src\main\resources\*.php ..\httpd\download /y

rem create sc-test
call mvn-assembly-cmd.bat ..\..\java\sc-test
