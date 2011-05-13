call mvn-source-test-install-cmd.bat	..\..\demo-web
copy ..\..\demo-web\WebContent\WEB-INF\web.xml 	..\kit-tmp\examples /y
copy ..\..\demo-web\WebContent\WEB-INF\web.xml 	..\kit-tmp\conf /y