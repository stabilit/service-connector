Run Service Connector as deamon on Red Hat based linux
======================================================

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
	bin\logback-classic-1.2.1.jar
	bin\logback-core-1.2.1.jar
	bin\netty-3.2.5.Final.jar
	bin\slf4j-api-1.7.2.jar

2) adapt the daemon script serviceconnector as follows: 
	change variale DAEMON_PATH="/root/Desktop/sc/bin"
	to the value where sc bin directory resides (i.e. /home/sc/bin)

3) move the daemon script serviceconnector to /etc/init.d/serviceconnector

4) run: chkconfig serviceconnector on
   This command will register serviceconnector service to be started and shutdown automatically 
   
Note:
   You can control the serviceconnector service manually using: service serviceconnector {status|start|stop|restart}

