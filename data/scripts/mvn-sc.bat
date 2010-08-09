rem delete old files
cd /../../java/service-connector
del /s /q target
mvn assembly:assembly -B
exit