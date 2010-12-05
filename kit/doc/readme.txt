Service Connector T1.0-7

List of functions NOT implemented in current version:
- SC cascading, proxy services
- Tomcat server API (servlet)

Known Problems:
- Operation timeout does not work well for large messages
- caching poorly tested
- file services poorly tested
- some issues in publishing

Special configuration in current version
- the SC is configured by sc.properties: http=localhost/7000 tcp=localhost/9000
- demo client is hardcoded to connect to port 7000
- demo server is hardcoded connect to port 9000