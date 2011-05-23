call mvn-source-test-install-cmd.bat 		..\..\sc-lib
call mvn-assembly-cmd.bat 					..\..\sc-lib
copy ..\..\sc-lib\target\sc-lib-all.jar				..\kit-tmp\bin /y
copy ..\..\sc-lib\target\sc-lib-all-sources.jar		..\kit-tmp\sources /y
