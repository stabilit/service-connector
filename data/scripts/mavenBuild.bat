@echo on
rem Build Kit
rem kick-off maven to build all
call mvn-sc.bat
rem copy sc.jar to dist dir
copy /y target\sc.jar ..\..\data\dist
cd /../../data\scripts