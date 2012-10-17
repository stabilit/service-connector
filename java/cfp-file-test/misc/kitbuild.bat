@echo on

del ..\kit\logs\SC0\*.* /F/Q
del ..\kit\files\*.* /F/Q
del ..\kit\* /F/Q

rd ..\kit\logs\SC0 /S/Q
rd ..\kit\logs /S/Q
rd ..\kit\files /S/Q
call mvn-assembly-cmd.bat 				..\..\fileUploader

copy ..\target\fileUploader.jar			..\kit /y
copy ..\..\sc-lib\target\sc-lib-all.jar	..\kit /y
copy sclist.php							..\kit /y
copy scupload.php						..\kit /y
copy httpd-sc.conf						..\kit /y
xcopy files								..\kit\files\ /e /i /h
copy readme.txt							..\kit /y
copy sc.properties						..\kit /y