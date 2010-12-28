Service Connector
=================

List of functions NOT implemented in current version:
- SC cascading, proxy services
- Tomcat server API

Known Problems:
- Operation timeout does not work well for large messages
- caching poorly tested
- file services poorly tested


Special configuration in current version
- the SC is configured by sc.properties: http=localhost/7000 tcp=localhost/9000
- demo client is hardcoded to connect to port 7000
- demo server is hardcoded connect to port 9000
- logging properties are configured for troubleshooting purposes => low performance