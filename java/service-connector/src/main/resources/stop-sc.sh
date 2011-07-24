#
# sc-console is used to stop SC
$JAVA_HOME/bin/java -Dlog4j.configuration=file:../conf/log4j-console.properties -jar sc-console.jar -h localhost -p 9000 kill