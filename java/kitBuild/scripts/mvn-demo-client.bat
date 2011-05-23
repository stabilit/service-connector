call mvn-source-test-install-cmd.bat		..\..\demo-client
call mvn-assembly-cmd.bat					..\..\demo-client
copy ..\..\demo-client\target\demo-client.jar 				..\kit-tmp\bin /y
copy ..\..\demo-client\src\main\resources\log4j.properties 	..\kit-tmp\conf\log4j-demo-client.properties /y
copy ..\..\demo-client\target\*sources.jar  				..\kit-tmp\sources /y