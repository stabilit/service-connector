call mvn-source-test-install-cmd.bat		..\..\demo-web-server
call mvn-war-cmd.bat 						..\..\demo-web-server
copy ..\..\demo-web-server\target\demo-web-server.war 										..\kit-tmp\examples /y
copy ..\..\demo-web-server\target\demo-web-server-sources.jar								..\kit-tmp\sources /y
copy ..\..\demo-web-server\target\demo-web-server\WEB-INF\web.xml 							..\kit-tmp\examples /y
copy ..\..\demo-web-server\target\demo-web-server\target\demo-web-server\WEB-INF\web.xml 	..\kit-tmp\conf /y