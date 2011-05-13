call mvn-source-test-install-cmd.bat ..\..\sc-web
call mvn-assembly-cmd.bat ..\..\sc-web
copy ..\..\sc-web\target\sc-web.jar 				..\kit-tmp\bin /y