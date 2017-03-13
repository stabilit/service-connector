#!/bin/bash
# start demo client
java -Dlogback.configurationFile=file:../conf/logback-demo-client.xml -jar demo-client-${sc.version}.jar
