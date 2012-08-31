@echo on

del ..\..\..\kit\sc-bin.zip /F/Q
del ..\..\..\kit\sc-src.zip /F/Q

del ..\kit-tmp\*.* /F/Q
rd ..\kit-tmp\bin /S/Q
rd ..\kit-tmp\cache /S/Q
rd ..\kit-tmp\conf /S/Q
rd ..\kit-tmp\doc /S/Q
rd ..\kit-tmp\examples /S/Q
rd ..\kit-tmp\logs /S/Q
rd ..\kit-tmp\sources /S/Q

md ..\kit-tmp\bin
md ..\kit-tmp\cache
md ..\kit-tmp\conf
md ..\kit-tmp\doc
md ..\kit-tmp\examples
md ..\kit-tmp\logs
md ..\kit-tmp\sources

call mvn-all.bat

copy ..\..\..\documents\SC_0_SCMP_E-v*.pdf     	..\kit-tmp\doc\ /y
copy ..\..\..\documents\SC_CC_E-V*.pdf      	..\kit-tmp\doc\ /y
copy ..\..\..\documents\SC_4_Operation_E.pdf 	..\kit-tmp\doc\ /y
copy ..\..\..\documents\Open_Issues.xls			..\kit-tmp\doc\ /y
copy ..\examples\*.*							..\kit-tmp\examples\ /y
copy ..\readme.txt								..\kit-tmp\ /y

cd ..\kit-tmp\
"C:\Program Files\WinZip\wzzip" -ex -rP ..\..\..\kit\sc-bin.zip @..\scripts\kitIncludeBinList.txt -x@..\scripts\kitExcludeBinList.txt
"C:\Program Files\WinZip\wzzip" -ex -rP ..\..\..\kit\sc-src.zip @..\scripts\kitIncludeSrcList.txt -x@..\scripts\kitExcludeSrcList.txt
cd ..\scripts\

java -cp ..\bin;..\..\sc-lib\target\sc-lib-all.jar org.serviceconnector.util.KitUtility copyAndRenameKitToVersion ..\..\..\kit\ ..\..\..\
java -cp ..\bin;..\..\sc-lib\target\sc-lib-all.jar org.serviceconnector.util.KitUtility generateHashFile ..\..\..\