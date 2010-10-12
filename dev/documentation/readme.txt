Service Connector T1.0-2

List of functions NOT implemented in current version:
- File services (File upload/download/list)
- HTTP Proxy Services (redirecting of regular HTTP traffic to another server)
- Message caching
- SC cascading, proxy services
- Web-GUI monitor
- SC Service wrapper for MS Windows
- Tomcat server API (servlet)
- compression of messages
- wait for available server (CLN_CREATE_SESSION)
- wait for available server connection (CLN_EXECUTE)
- validation cannot be disabled
- publish service API is not finished (java)
- logging is not complete, catched exceptions may appear in the log


Special configuration in current version
- keep alive interval (kpi) is default set to 0 means inactive
- echo interval (eci) for demo client is set to 60 seconds
- no data interval (noi) for receive publication is set to 300 seconds 
- the SC is configured by sc.properties: http localhost/7000 tcp localhost/9000
- demo client is hardcoded to connects to port 7000
- demo server is hardcoded connects to port 7000