@echo off
instsrv ServiceConnector "C:\stabilit\eurexsvn\workspace\SVN_repository\dev\win32\srvany.exe"

sleep 8

regedit /s "C:\stabilit\eurexsvn\workspace\SVN_repository\dev\win32\serviceconnector.reg"

sleep 8

net start ServiceConnector 

rem pause
