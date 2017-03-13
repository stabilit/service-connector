#!/bin/bash
#  ** ATTENTION **
#  THIS SCRIPT MAY BE REFERENCED BY THE DEAMON SCRIPT
# sc-console is used to send kill command to SC
# either configuration file 
# 		 -config ../conf/sc.properties or 
# or host and port
# 		-h localhost -p 9000
# must eb provided to reach the SC
java -Dlogback.configurationFile=file:../conf/logback-console.xml -jar sc-console-${sc.version}.jar -config ../conf/sc.properties kill
