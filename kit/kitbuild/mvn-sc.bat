@echo off

call mvn-source-test-install-cmd.bat ..\..\java\service-connector

rem create service-connector sc.jar and copy to bin dir
call mvn-assembly-cmd.bat ..\..\java\service-connector
copy ..\..\java\service-connector\target\sc.jar ..\bin /y
copy ..\..\java\service-connector\src\main\resources\sc.properties ..\config /y
copy ..\..\java\service-connector\src\main\resources\sc-specific.properties ..\config /y
copy ..\..\java\service-connector\src\main\resources\log4j.properties ..\config\log4j-sc.properties /y
copy ..\..\java\service-connector\src\main\resources\*.php ..\httpd\upload /y
copy ..\..\java\service-connector\src\main\resources\*.php ..\httpd\download /y
copy ..\..\java\service-connector\src\main\resources\hpptd-sc.conf ..\httpd\config /y
