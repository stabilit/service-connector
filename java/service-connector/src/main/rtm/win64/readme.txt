Run Service Connector as Windows service on Win32
=================================================

1) install sc files to the directory of your choice usually C:\Program Files\sc\
	The required files are:
	bin\sc.jar
	bin\sc-console.jar
	bin\sc-lib-all.jar
	bin\win32\jbosssvc.exe
	bin\win32\service.bat
	bin\win32\start.bat
	bin\win32\stop.bat

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

