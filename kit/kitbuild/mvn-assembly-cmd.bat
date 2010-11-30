rem delete old files
del %1%\target\*.jar /s /q
rem assembly
mvn assembly:assembly -f=%1%\pom.xml -B
exit