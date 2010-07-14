@echo off
cd C:\stabilit\projects\EUREX\SC\dev
del sc.zip

rem Build Kit
rem kick-off maven to build all

rem Create Installation Kit
echo -
"C:\Program Files\WinZip\wzzip" -yp -ex -rP sc.zip @data\scripts\kitList.txt -x@data\scripts\kitExcludeList.txt