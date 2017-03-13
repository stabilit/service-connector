Run Service Connector as Windows service on Win32
=================================================

1) install sc files to the directory of your choice usually C:\Program Files\sc\
	The required files are:
	bin\sc-X.X.X.RELEASE.jar
	bin\sc-console-X.X.X.RELEASE.jar
	bin\sc-lib-X.X.X.RELEASE.jar
	bin\win32\jbosssvc.exe
	bin\win32\service.bat
	bin\win32\start.bat
	bin\win32\stop.bat
	bin\commons-collections-3.2.1.jar
	bin\commons-configuration-1.6.jar
	bin\commons-lang-2.4.jar
	bin\commons-logging-1.1.1.jar
	bin\ehcache-core-2.4.1.jar
	bin\logback-classic-1.2.1.jar
	bin\logback-core-1.2.1.jar
	bin\netty-3.2.5.Final.jar
	bin\slf4j-api-1.7.2.jar

2) install SC as service by invoking the service.bat as follows
		service install

3) you can now start the service with the dos-command 
		net start "Service Connector"
	or by invoking  
		start.bat

4) In order to stop the service use the dos-command 
		net stop "Service Connector"
	or 
		stop.bat

5) If you want to uninstall SC as windows service by invoking the service.bat as follows
		service uninstall

