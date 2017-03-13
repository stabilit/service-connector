#!/bin/bash
# start demo server
java -Dlogback.configurationFile=file:../conf/logback-demo-server.xml -jar demo-server-${sc.version}.jar
