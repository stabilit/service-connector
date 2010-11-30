@echo off
rem build all
rem
call mvn-sc.bat
call mvn-sc-lib.bat
call mvn-sc-console.bat
call mvn-sc-server.bat
call mvn-sc-client.bat
call mvn-sc-test.bat
call mvn-javadoc.bat
