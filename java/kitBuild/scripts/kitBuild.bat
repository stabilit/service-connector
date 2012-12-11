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
copy ..\..\sc-lib\target\sc-lib-all.jar								..\kit-tmp\bin /y
copy ..\..\sc-lib\target\sc-lib-all-sources.jar						..\kit-tmp\sources /y
copy ..\..\sc-web\target\sc-web-all.jar 							..\kit-tmp\bin /y
copy ..\..\sc-web\target\sc-web-all-sources.jar						..\kit-tmp\sources /y

copy ..\..\service-connector\target\sc.jar 							..\kit-tmp\bin /y
copy ..\..\service-connector\src\main\rtm\sc.properties 			..\kit-tmp\conf /y
copy ..\..\service-connector\src\main\rtm\sc-specific.properties	..\kit-tmp\conf /y
copy ..\..\service-connector\src\main\rtm\log4j.properties 			..\kit-tmp\conf\log4j-sc.properties /y
copy ..\..\service-connector\src\main\rtm\start*.*	 				..\kit-tmp\bin /y
copy ..\..\service-connector\src\main\rtm\stop*.* 					..\kit-tmp\bin /y

xcopy ..\..\service-connector\src\main\rtm\unix 					..\kit-tmp\bin\unix\ /y /e
xcopy ..\..\service-connector\src\main\rtm\win32					..\kit-tmp\bin\win32\ /y /e
xcopy ..\..\service-connector\src\main\rtm\win64					..\kit-tmp\bin\win64\ /y /e

copy ..\..\sc-console\target\sc-console.jar 						..\kit-tmp\bin /y
copy ..\..\sc-console\src\main\rtm\log4j.properties 				..\kit-tmp\conf\log4j-console.properties /y
copy ..\..\sc-console\src\main\rtm\example_sc-console.bat			..\kit-tmp\examples /y

copy ..\..\demo-client\target\demo-client.jar 						..\kit-tmp\bin /y
copy ..\..\demo-client\src\main\rtm\start*.*	 					..\kit-tmp\bin /y
copy ..\..\demo-client\src\main\rtm\stop*.* 						..\kit-tmp\bin /y
copy ..\..\demo-client\src\main\rtm\log4j.properties 				..\kit-tmp\conf\log4j-demo-client.properties /y
copy ..\..\demo-client\target\*sources.jar  						..\kit-tmp\sources /y

copy ..\..\demo-server\target\demo-server.jar 						..\kit-tmp\bin /y
copy ..\..\demo-server\src\main\rtm\start*.*	 					..\kit-tmp\bin /y
copy ..\..\demo-server\src\main\rtm\stop*.* 						..\kit-tmp\bin /y
copy ..\..\demo-server\src\main\rtm\log4j.properties 				..\kit-tmp\conf\log4j-demo-server.properties /y
copy ..\..\demo-server\src\main\rtm\*.php 							..\kit-tmp\examples /y
copy ..\..\demo-server\src\main\rtm\httpd-sc.conf 					..\kit-tmp\examples /y
copy ..\..\demo-server\src\main\rtm\httpd-sc.conf 					..\kit-tmp\conf /y
copy ..\..\demo-server\target\*sources.jar  						..\kit-tmp\sources /y

copy ..\..\demo-web-server\target\demo-web-server.war 				..\kit-tmp\bin /y
copy ..\..\demo-web-server\target\demo-web-server-sources.jar		..\kit-tmp\sources /y
copy ..\..\demo-web-server\target\demo-web-server\WEB-INF\web.xml	..\kit-tmp\examples /y
copy ..\..\demo-web-server\target\demo-web-server\WEB-INF\web.xml	..\kit-tmp\conf /y

xcopy ..\..\sc-lib\target\site\apidocs 								..\kit-tmp\doc\javadoc\ /y /e

copy ..\..\..\documents\SC_0_SCMP_E-v*.pdf     						..\kit-tmp\doc\ /y
copy ..\..\..\documents\SC_CC_E-V*.pdf      						..\kit-tmp\doc\ /y
copy ..\..\..\documents\SC_4_Operation_E.pdf 						..\kit-tmp\doc\ /y
copy ..\..\..\documents\Open_Issues.xls								..\kit-tmp\doc\ /y
copy ..\examples\*.*												..\kit-tmp\examples\ /y
copy ..\readme.txt													..\kit-tmp\ /y

cd ..\kit-tmp\
"C:\Program Files\WinZip\wzzip" -ex -rP ..\..\..\kit\sc-bin.zip @..\scripts\kitIncludeBinList.txt -x@..\scripts\kitExcludeBinList.txt
"C:\Program Files\WinZip\wzzip" -ex -rP ..\..\..\kit\sc-src.zip @..\scripts\kitIncludeSrcList.txt -x@..\scripts\kitExcludeSrcList.txt
cd ..\scripts\

java -cp ..\bin;..\..\sc-lib\target\sc-lib-all.jar org.serviceconnector.util.KitUtility copyAndRenameKitToVersion ..\..\..\kit\ ..\..\..\
java -cp ..\bin;..\..\sc-lib\target\sc-lib-all.jar org.serviceconnector.util.KitUtility generateHashFile ..\..\..\