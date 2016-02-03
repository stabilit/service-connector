Run Service Connector as deamon on unix
=======================================

1) install sc files to the directory of your choice usually /home/sc/
	The required files are:
	bin\sc-X.X.X.RELEASE.jar
	bin\sc-lib-X.X.X.RELEASE.jar
	bin\start-sc.sh
	bin\stop-sc.sh
	bin\commons-collections-3.2.1.jar
	bin\commons-configuration-1.6.jar
	bin\commons-lang-2.4.jar
	bin\commons-logging-1.1.1.jar
	bin\ehcache-core-2.4.1.jar
	bin\log4j-1.2.15.jar
	bin\netty-3.2.5.Final.jar
	bin\slf4j-api-1.7.2.jar
	bin\slf4j-log4j12-1.7.2.jar

2) adapt the daemon script serviceconnector.sh as follows: 
	change variale DAEMON=/home/sc/start-sc.sh
	to the value where sc files have been installed

3) move the daemon script in /etc/init.d/serviceconnector.sh

Note:
	The daemon script uses user: <user>. For a general installation a new user must be installed (e.g. sc).

