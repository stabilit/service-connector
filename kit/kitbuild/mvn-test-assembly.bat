rem create sc-lib-final.jar and copy to sc-test target dir
call mvn-assembly-cmd.bat ..\..\java\sc-lib

rem create service-connector sc.jar and copy to sc-test target dir
call mvn-assembly-cmd.bat ..\..\java\service-connector

rem create sc-test
call mvn-assembly-cmd.bat ..\..\java\sc-test

copy ..\..\java\service-connector\target\sc.jar ..\..\java\sc-test\target /y
copy ..\..\java\sc-lib\target\sc-lib-final.jar  ..\..\java\sc-test\target /y