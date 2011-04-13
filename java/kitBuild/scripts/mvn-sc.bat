call mvn-source-test-install-cmd.bat	..\..\service-connector
call mvn-assembly-cmd.bat 				..\..\service-connector
copy ..\..\service-connector\target\sc.jar 									..\kit-tmp\bin /y
copy ..\..\service-connector\src\main\resources\sc.properties 				..\kit-tmp\conf /y
copy ..\..\service-connector\src\main\resources\sc-specific.properties 		..\kit-tmp\conf /y
copy ..\..\service-connector\src\main\resources\log4j.properties 			..\kit-tmp\conf\log4j-sc.properties /y
copy ..\..\service-connector\src\main\resources\start*.*	 				..\kit-tmp\bin /y
copy ..\..\service-connector\src\main\resources\stop*.* 					..\kit-tmp\bin /y
xcopy ..\..\service-connector\src\main\resources\unix 						..\kit-tmp\bin\unix\ /y /e
xcopy ..\..\service-connector\src\main\resources\win32						..\kit-tmp\bin\win32\ /y /e
xcopy ..\..\service-connector\src\main\resources\win64						..\kit-tmp\bin\win64\ /y /e
