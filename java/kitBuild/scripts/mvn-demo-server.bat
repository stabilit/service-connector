call mvn-source-test-install-cmd.bat ..\..\demo-server
call mvn-assembly-cmd.bat ..\..\demo-server
copy ..\..\demo-server\target\demo-server.jar 				..\bin /y
copy ..\..\demo-server\src\main\resources\log4j.properties 	..\conf\log4j-demo-server.properties /y
copy ..\..\demo-server\src\main\resources\*.php 			..\examples /y
copy ..\..\demo-server\src\main\resources\httpd-sc.conf 	..\examples /y
copy ..\..\demo-server\target\*sources.jar  				..\sources /y