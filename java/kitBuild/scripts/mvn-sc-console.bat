call mvn-source-test-install-cmd.bat	..\..\sc-console
call mvn-assembly-cmd.bat 				..\..\sc-console
copy ..\..\sc-console\target\sc-console.jar 					..\kit-tmp\bin /y
copy ..\..\sc-console\src\main\resources\log4j.properties 		..\kit-tmp\conf\log4j-console.properties /y
copy ..\..\sc-console\src\main\resources\example_sc-console.bat	..\kit-tmp\examples /y
