@echo off

del ..\..\..\kit\sc-bin.zip
del ..\..\..\kit\sc-src.zip
del ..\bin\*.* /F/Q 

call mvn-all.bat

copy ..\..\..\documents\SC_0_SCMP_E.pdf      	..\doc\ /y
copy ..\..\..\documents\SC_4_Operation_E.pdf 	..\doc\ /y
copy ..\..\..\documents\Open_Issues.xls			..\doc\ /y

cd ..\
"C:\Program Files\WinZip\wzzip" -ex -rP ..\..\kit\sc-bin.zip @scripts\kitIncludeBinList.txt -x@scripts\kitExcludeBinList.txt
"C:\Program Files\WinZip\wzzip" -ex -rP ..\..\kit\sc-src.zip @scripts\kitIncludeSrcList.txt -x@scripts\kitExcludeSrcList.txt

rem "*** Kit created ***"