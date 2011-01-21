Service Connector
=================

List of functions NOT implemented in current version:
- SC cascading, cascaded services
- Tomcat server API

Known Problems:
- see release notes


Special configuration in current version
- Keep alive interval enabled in java API's 60 seconds default
- the SC is configured by sc.properties: http=localhost/7000 tcp=localhost/9000
- demo client is hardcoded to connect to port 7000
- demo server is hardcoded connect to port 9000
- logging properties are configured for troubleshooting purposes => low performance
