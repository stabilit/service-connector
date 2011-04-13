call mvn-source-test-install-cmd.bat	..\..\demo-server
call mvn-assembly-cmd.bat 				..\..\demo-server
copy ..\..\demo-server\target\demo-server.jar 				..\kit-tmp\bin /y
copy ..\..\demo-server\src\main\resources\log4j.properties 	..\kit-tmp\conf\log4j-demo-server.properties /y
copy ..\..\demo-server\src\main\resources\*.php 			..\kit-tmp\examples /y
copy ..\..\demo-server\src\main\resources\httpd-sc.conf 	..\kit-tmp\examples /y
copy ..\..\demo-server\src\main\resources\httpd-sc.conf 	..\kit-tmp\conf /y
copy ..\..\demo-server\target\*sources.jar  				..\kit-tmp\sources /y