@echo off
del ..\..\sc.zip

rem Build Kit
rem kick-off maven to build kit
call mvn-all.bat > build.log

cd C:\stabilit\projects\EUREX\SC\kit
rem Create Installation Kit
echo -
copy ..\..\documents\SC_0_SCMP_E.pdf ..\doc\ /y
"C:\Program Files\WinZip\wzzip" -yp -ex -rP ..\sc.zip @kitbuild\kitList.txt -x@kitbuild\kitExcludeList.txt
