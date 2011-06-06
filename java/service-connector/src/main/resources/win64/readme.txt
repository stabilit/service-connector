Run Service Connector as Windows service on Win64
=================================================

1) install sc files to the directory of your choice usually C:\Program Files\sc\
	The required files are:
	bin\sc.jar
	bin\sc-lib-all.jar
	bin\start-SC.bat
	bin\stop-SC.bat
	bin\win64\jbosssvc.exe
	bin\win64\service.bat
	bin\win64\shutdown.bat
	bin\win64\shutdown.jar
	bin\win64\start.bat
	bin\win64\stop.bat

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
