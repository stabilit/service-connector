Service Connector T1.0-1

Issues not implemented in current version
- File services (File upload/download)
- HTTP Proxy Services (redirecting of regular HTTP traffic to another server)
- Message caching
- SC cascading
- Server with less connections than sessions (attr. maxConnections)
- operation timeout
- compression of messages

Special configuration in current version
- keep alive timeout (kpi) is default set to 0 means inactive
- session interval (eci) for demo client is set to 60 seconds
- no data interval (noi) for receive publication is set to 300 seconds 
- the SC is configured by sc.properties default: http localhost/8000 tcp localhost/9000¨
- demo client connects to 8000
- demo server connects to 9000
- sc version is set to 1.0-000
- scmp version is set to 1.0