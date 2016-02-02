#!/bin/bash
#  ** ATTENTION **
#  THIS SCRIPT MAY BE REFERENCED BY THE DEAMON SCRIPT
# sc-console is used to send kill command to SC
# either configuration file 
# 		 -config ../conf/sc.properties or 
# or host and port
# 		-h localhost -p 9000
# must eb provided to reach the SC
java -Dlog4j.configuration=file:../conf/log4j-console.properties -jar sc-console-${sc.version}.jar -config ../conf/sc.properties kill
