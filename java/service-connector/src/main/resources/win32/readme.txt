Run Service Connector as Windows service on Win32
=================================================

1) install sc files to the directory of your choice usually C:\Program Files\sc\
	The required files are:
	sc.jar
	sc-lib-all.jar
	start-sc.bat
	stop-sc.bat
	win32\jbosssvc.exe
	win32\service.bat
	win32\shutdown.bat
	win32\shutdown.jar
	win32\start.bat
	win32\stop.bat

2) install SC as service by invoking the service.bat as follows
		service install

3) you can now start the service with the dos-command 
		net start ServiceConnector
	or by invoking  
		start.bat

4) In order to stop the service use the dos-command 
		net stop ServiceConnector
	or 
		stop.bat

5) If you want to uninstall SC as windows service by invoking the service.bat as follows
		service uninstall

