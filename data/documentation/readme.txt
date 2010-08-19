Service Connector T1.0-1 Readme

Issues not implemented in current version
- File services (File upload/download)
- HTTP Proxy Services (redirecting of regular HTTP traffic to another server)
- Concept of serving some sessions over less connections (e.g. 10 sessions over 5 connections)
- operation timeout (observing the time of a send)

Special configuration in current version
- keep alive timeout (kpi) is default set to 0 means inactive
- session timeout (eci) for demo client is set to 60 seconds
- no data interval (noi) for receive publication is set to 300 seconds 
- the SC is configured by sc.properties default: http localhost/8000 tcp localhost/9000¨
- demo client connects to 8000
- demo server connects to 9000
