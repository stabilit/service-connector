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
cd scripts\

rem "Copy the kit into the proper places"
rem ..\bin\rename_and_move_kit.bat	(bat-file created / modified by java)
rem looks like
rem copy ..\..\..\kit\sc-bin.zip ..\..\..\sc-bin_V1.3-1.zip
rem copy ..\..\..\kit\sc-src.zip ..\..\..\sc-src_V1.3-1.zip

rem copy ..\..\..\sc-*.zip S:\projects\WEB_STABILIT\download\sc\ /y
rem copy ..\..\..\sc-*.zip S:\projects\EUREX\SC\versions\ /y

rem "Commit all in SVN" 