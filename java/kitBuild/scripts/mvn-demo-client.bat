call mvn-source-test-install-cmd.bat ..\..\demo-client
call mvn-assembly-cmd.bat ..\..\demo-client
copy ..\..\demo-client\target\demo-client.jar 				..\bin /y
copy ..\..\demo-client\src\main\resources\log4j.properties 	..\conf\log4j-demo-client.properties /y
copy ..\..\demo-client\target\*sources.jar  				..\sources /y