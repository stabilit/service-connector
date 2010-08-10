@echo on
set startPath=%cd%

rem Build Kit
rem kick-off maven to build all

rem first install all projects
call mvn-install-cmd.bat /../../java/sc-api
cd %startDir%
call mvn-install-cmd.bat /../../java/sc-impl
cd %startDir%
call mvn-install-cmd.bat /../../java/service-connector
cd %startDir%
call mvn-install-cmd.bat /../../java/sc-simulation
cd %startDir%

rem assembly service-connector sc.jar
call mvn-assembly-cmd.bat /../../java/service-connector
rem copy sc.jar to dist dir
copy /y target\sc.jar ..\..\data\dist
cd %startPath%

rem assembly sc-simulation server.jar
call mvn-assembly-cmd.bat /../../java/sc-simulation
rem copy server.jar to dist dir
copy /y target\server.jar ..\..\data\dist
cd %startPath%

