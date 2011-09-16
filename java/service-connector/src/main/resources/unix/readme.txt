Run Service Connector as deamon on unix
=======================================

1) install sc files to the directory of your choice usually /home/sc/
	The required files are:
	bin\sc.jar
	bin\sc-lib-all.jar
	bin\start-sc.sh
	bin\stop-sc.sh

2) adapt the daemon script serviceconnector.sh as follows: 
	change variale DAEMON=/home/sc/start-sc.sh
	to the value where sc files have been installed

3) move the daemon script in /etc/init.d/serviceconnector.sh

Note:
	The daemon script uses user: <user>. For a general installation a new user must be installed (e.g. sc).

