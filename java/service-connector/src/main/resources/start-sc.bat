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
rem  -Xmx512M	allow max 512MB heap size
rem  -server	enables server JVM
rem	 -Xrs		Reduces use of operating-system signals by the Java virtual machine (JVM).	
rem
rem set default directory
set DIRNAME=%CD%
cd "%~dp0"
rem start sc
java -Xmx512M -Xrs -Dlog4j.configuration=file:../conf/log4j-sc.properties -jar sc.jar -config ../conf/sc.properties
cd %DIRNAME%