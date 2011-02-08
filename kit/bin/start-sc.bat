rem In order to start SC with remote JMX add the following parameters in the command line
rem		 -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
rem
rem you can pass parameters on the command line like
rem     -DlogDirectory=c:/temp 
rem or OS Environment variables like
rem			-DlogDirectory=%OS_VARIABLE%
rem and use them in the sc.properties files as ${sys:logDirectory} and in log4j.properties as ${logDirectory}
rem
rem# start sc
java -Dlog4j.configuration=file:../conf/log4j-sc.properties -jar sc.jar -config ../conf/sc.properties