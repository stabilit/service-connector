rem  ** ATTENTION **
rem  DO NOT CHANGE THE NAME AND LOCATION OF THIS SCRIPT
rem  IT IS REFERENCED BY THE SCRIPT GENERATING THE WINDOWS SERVICES
rem
rem In order to start SC with remote JMX add the following parameters in the command line
rem		 -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
rem
rem you can pass parameters on the command line like
rem     -DlogDirectory=c:/temp 
rem or OS Environment variables like
rem			-DlogDirectory=%OS_VARIABLE%
rem and use them in the sc.properties files as ${sys:logDirectory} and in log4j.properties as ${logDirectory}
rem
rem Adapt this script to optimize JVM parameters for SC
rem  -Xmx1024M  allow 1GB memory
rem  -server    enables server JVM
rem
rem# start sc
java -Xmx1024M  -Dlog4j.configuration=file:%~dp0../conf/log4j-sc.properties -jar %~dp0sc.jar -config %~dp0../conf/sc.properties