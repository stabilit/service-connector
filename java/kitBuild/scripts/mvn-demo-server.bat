call mvn-source-test-install-cmd.bat ..\..\demo-server
call mvn-assembly-cmd.bat ..\..\demo-server
copy ..\..\demo-server\target\demo-server.jar 				..\..\..\kit\bin /y
copy ..\..\demo-server\src\main\resources\log4j.properties 	..\..\..\kit\conf\log4j-demo-server.properties /y
copy ..\..\demo-server\src\main\resources\*.php 			..\..\..\kit\examples /y
copy ..\..\demo-server\src\main\resources\httpd-sc.conf 	..\..\..\kit\examples /y
copy ..\..\demo-server\target\*sources.jar  				..\..\..\kit\sources /y