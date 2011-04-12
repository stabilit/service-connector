call mvn-source-test-install-cmd.bat ..\..\service-connector
call mvn-assembly-cmd.bat ..\..\service-connector
copy ..\..\service-connector\target\sc.jar 									..\..\..\kit\bin /y
copy ..\..\service-connector\src\main\resources\sc.properties 				..\..\..\kit\conf /y
copy ..\..\service-connector\src\main\resources\sc-specific.properties 		..\..\..\kit\conf /y
copy ..\..\service-connector\src\main\resources\log4j.properties 			..\..\..\kit\conf\log4j-sc.properties /y
copy ..\..\service-connector\src\main\resources\readme.txt 					..\..\..\kit /y
copy ..\..\service-connector\src\main\resources\start*.*	 				..\..\..\kit\bin /y
copy ..\..\service-connector\src\main\resources\stop*.* 					..\..\..\kit\bin /y
xcopy ..\..\service-connector\src\main\resources\unix 						..\..\..\kit\bin\unix\ /y /e
xcopy ..\..\service-connector\src\main\resources\win32						..\..\..\kit\bin\win32\ /y /e
xcopy ..\..\service-connector\src\main\resources\win64						..\..\..\kit\bin\win64\ /y /e
copy ..\..\service-connector\src\main\resources\j*.bat						..\..\..\kit\examples /y