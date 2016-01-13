#!/bin/bash
# start demo client
java -Dlog4j.configuration=file:../conf/log4j-demo-client.properties -jar demo-client-${sc.version}.jar
