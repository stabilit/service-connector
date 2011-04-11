rem 
rd /S/Q C:\svn-tmp
mkdir C:\svn-tmp
C:\"Program Files\TortoiseSVN\bin\TortoiseProc.exe" /command:export /closeonend:1 /path:"C:\svn-tmp"
rem 
C:\"Program Files\TortoiseSVN\bin\TortoiseProc.exe" /command:import /closeonend:1 /path:"C:\svn-tmp"
rd /S/Q C:\svn-tmp