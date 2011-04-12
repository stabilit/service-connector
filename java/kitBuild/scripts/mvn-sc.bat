call mvn-source-test-install-cmd.bat ..\..\service-connector
call mvn-assembly-cmd.bat ..\..\service-connector
copy ..\..\service-connector\target\sc.jar 									..\bin /y
copy ..\..\service-connector\src\main\resources\sc.properties 				..\conf /y
copy ..\..\service-connector\src\main\resources\sc-specific.properties 		..\conf /y
copy ..\..\service-connector\src\main\resources\log4j.properties 			..\conf\log4j-sc.properties /y
copy ..\..\service-connector\src\main\resources\readme.txt 					..\ /y
copy ..\..\service-connector\src\main\resources\start*.*	 				..\bin /y
copy ..\..\service-connector\src\main\resources\stop*.* 					..\bin /y
xcopy ..\..\service-connector\src\main\resources\unix 						..\bin\unix\ /y /e
xcopy ..\..\service-connector\src\main\resources\win32						..\bin\win32\ /y /e
xcopy ..\..\service-connector\src\main\resources\win64						..\bin\win64\ /y /e
copy ..\..\service-connector\src\main\resources\j*.bat						..\examples /y