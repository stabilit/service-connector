rem install
del %1%\target /s /q
mvn source:jar jar:test-jar install -f=%1%\pom.xml
exit