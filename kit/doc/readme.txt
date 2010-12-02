Service Connector T1.0-2

List of functions NOT implemented in current version:
- SC cascading, proxy services
- Tomcat server API (servlet)

Known Problems:
- Error during delivery of large message to client
- maxSessions and maxConnections relationship is not checked in Java API

Special configuration in current version
- the SC is configured by sc.properties: http=localhost/7000 tcp=localhost/9000
- demo client is hardcoded to connects to port 7000
- demo server is hardcoded connects to port 9000