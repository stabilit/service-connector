rem In order to start SC with remote JMX add the following parameters in the command line
rem
rem -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
rem
java -Dlog4j.configuration=file:..\config\log4j-sc.properties -jar ..\bin\sc.jar -sc.configuration ..\config\sc.properties