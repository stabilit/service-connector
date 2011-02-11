@echo off
del ..\..\sc-bin.zip
del ..\..\sc-src.zip

rem build all
call mvn-all.bat > build.log

rem create kit
echo -
cd C:\stabilit\projects\EUREX\SC\kit
copy ..\documents\SC_0_SCMP_E.pdf doc\ /y
"C:\Program Files\WinZip\wzzip"     -ex -rP ..\sc-bin.zip @kitbuild\kitIncludeBinList.txt -x@kitbuild\kitExcludeBinList.txt
"C:\Program Files\WinZip\wzzip" -yp -ex -rP ..\sc-src.zip @kitbuild\kitIncludeSrcList.txt -x@kitbuild\kitExcludeSrcList.txt