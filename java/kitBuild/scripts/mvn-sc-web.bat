call mvn-source-test-install-cmd.bat ..\..\sc-web
call mvn-assembly-cmd.bat ..\..\sc-web
copy ..\..\sc-web\target\sc-web-all.jar 				 ..\kit-tmp\bin /y
copy ..\..\sc-web\target\sc-web-all-sources.jar  ..\kit-tmp\sources /y