call mvn-source-test-install-cmd.bat	..\..\demo-web-server
copy ..\..\demo-web-server\WebContent\WEB-INF\web.xml 	..\kit-tmp\examples /y
copy ..\..\demo-web-server\WebContent\WEB-INF\web.xml 	..\kit-tmp\conf /y