rem source:jar builds *-sources.jar
del %1%\target /s /q
mvn source:jar -f=%1%\pom.xml
exit