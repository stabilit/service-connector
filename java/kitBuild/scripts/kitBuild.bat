@echo off

del ..\..\..\sc-bin.zip
del ..\..\..\sc-src.zip

call mvn-all.bat

copy ..\..\..\documents\SC_0_SCMP_E.pdf      	..\..\..\kit\doc\ /y
copy ..\..\..\documents\SC_4_Operation_E.pdf 	..\..\..\kit\doc\ /y
copy ..\..\..\documents\Open_Issues.xls			..\..\..\kit\doc\ /y

cd ..\..\..\kit
"C:\Program Files\WinZip\wzzip" -ex -rP ..\sc-bin.zip @..\java\kitbuild\scripts\kitIncludeBinList.txt -x@..\java\kitbuild\scripts\kitExcludeBinList.txt
"C:\Program Files\WinZip\wzzip" -ex -rP ..\sc-src.zip @..\java\kitbuild\scripts\kitIncludeSrcList.txt -x@..\java\kitbuild\scripts\kitExcludeSrcList.txt