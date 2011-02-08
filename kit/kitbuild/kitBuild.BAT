@echo off
del ..\..\sc.zip
del ..\..\sc-src.zip

rem build all
call mvn-all.bat > build.log

rem create kit
echo -
cd C:\stabilit\projects\EUREX\SC\kit
copy ..\documents\SC_0_SCMP_E.pdf doc\ /y
"C:\Program Files\WinZip\wzzip"     -ex -rP ..\sc.zip     @kitbuild\kitIncludeList.txt    -x@kitbuild\kitExcludeList.txt
"C:\Program Files\WinZip\wzzip" -yp -ex -rP ..\sc-src.zip @kitbuild\kitIncludeSrcList.txt -x@kitbuild\kitExcludeSrcList.txt