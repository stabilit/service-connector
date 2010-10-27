@echo off
del ..\..\sc.zip

rem Build Kit
rem kick-off maven to build kit
call mvn-all.bat

cd C:\stabilit\projects\EUREX\SC\dev
rem Create Installation Kit
echo -
"C:\Program Files\WinZip\wzzip" -yp -ex -rP ..\sc.zip @scripts\kitList.txt -x@scripts\kitExcludeList.txt
cd C:\stabilit\projects\EUREX\SC\dev\scripts