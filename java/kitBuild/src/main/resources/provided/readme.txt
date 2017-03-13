Service Connector - service oriented messaging middleware
=========================================================

 Kit structure:
 --------------
sc-bin-X.X.X.RELEASE.zip - kit containing only binaries 
 --/bin         (binaries a scripts to manage sc)
       demo-client-X.X.X.RELEASE.jar 		  (runnable client to verify the installation)
       demo-server-X.X.X.RELEASE.jar 		  (runnable server to verify the installation)
       demo-web-server-X.X.X.RELEASE.war 	(demo web application servers implemented as servlets with sc-web-all.jar)
       sc-lib-X.X.X.RELEASE.jar  		      (SC library required for all components client, server, console and SC itself)
       sc-web-X.X.X.RELEASE.jar  		      (SC web library required for servers implemented as servlets)
       sc-X.X.X.RELEASE.jar          		  (runnable SC)
       sc-console-X.X.X.RELEASE.jar  		  (runnable console)
       start-demo-client.bat              (script to start demo client on Windows)
       start-demo-client.sh               (script to start demo client on Unix)
       start-demo-server.bat              (script to start demo server on Windows)
       start-demo-server.sh               (script to start demo server on Unix)
       start-sc.bat                       (script to start SC on Windows)
       start-sc.sh                        (script to start SC on Unix)
       stop-sc.bat                        (script to stop SC on Windows)
       stop-sc.sh                         (script to stop SC on Unix)
       commons-collections-3.2.1.jar      (dependency)
       commons-configuration-1.6.jar      (dependency)
       commons-lang-2.4.jar               (dependency)
       commons-logging-1.1.1.jar          (dependency)
       ehcache-core-2.4.1.jar             (dependency)
       logback-classic-1.2.1.jar          (dependency)
       logback-core-1.2.1.jar             (dependency)
       netty-3.2.5.Final.jar              (dependency)
       slf4j-api-1.7.2.jar                (dependency)
   /bin/debian    (SC as deamon on debian based linux)
       readme.txt          (guide how to setup SC as a deamon)
       serviceconnector.sh (script to run sc as deamon)
   /bin/redhat    (SC as deamon on Red Hat based linux)
       readme.txt          (guide how to setup SC as a deamon)
       serviceconnector    (script to run sc as deamon)
   /bin/win32   (SC as Windows service on Win32 platform)
       readme.txt          (guide how to setup SC as a Windows32 service)
       jbosssvc.exe        (auxiliary utility)
       service.bat         (script to set-up, manage and delete the service)
       start.bat           (script to start the service - example)
       stop.bat            (script to stop the service - example)
   /bin/win64   (SC as Windows service on Win64 platform)
       readme.txt          (guide how to setup SC as a Windows64 service)
       jbosssvc.exe        (auxiliary utility)
       service.bat         (script to set-up, manage and delete the service)
       start.bat           (script to start the service - example)
       stop.bat            (script to stop the service - example)
   /conf        (configuration)
       httpd-conf.sc       (Apache configuration for sc file services - example)
       logback-console.xml (SC console logging setup)
       logback-demo-client.xml (demo client logging setup)
       logback-demo-server.xml (demo server logging setup)
       logback-sc.xml (SC logging setup)
       sc-specific.properties (SC site specific configuration)
       sc.properties       (SC configuration)
       web.xml             (servlet configuration for SC services - example)
   /doc         (documentation)
       SC_4_Operation_E.pdf (SC operation and administration guide)
   /cache       (sc writes temporary cache-files here. See property cache.diskPath)
   /logs        (sc writes all log and dump files here. See logback configuration)
  

 sc-src-X.X.X.RELEASE.zip - kit containing binaries and additional resources required for development
 --/doc         (documentation)
       Open_Issues.xls    	(release notes)
       SC_0_SCMP_E.pdf     	(SCMP documentation)
       SC_4_Operation_E.pdf (SC operation and administration guide)
       SC_CC_E-V1.3.pdf     (Documentation to Service Connector Cache Coherency model)
   /doc/javadoc (java doc) 
   /examples    (additional script examples)
       example_sc-console.bat (script for sc-console - example)
       httpd-conf.sc       (Apache configuration for sc file services - example)
       jconsole.bat        (start script for jconsole - example)
       jvisualvm.bat       (start script for jvisualvm - example)
       sclist.php          (script to be used for SC file services)
       scupload.php        (script to be used for SC file services)
       web.xml             (servlet configuration for SC services - example)
   /sources     (sources of sc-lib and the demo application)
       demo-client-X.X.X.RELEASE-sources.jar     (source code of the demo client)
       demo-server-X.X.X.RELEASE-sources.jar     (source code of the demo server)
       demo-web-server-X.X.X.RELEASE-sources.jar (source code of the demo web application)
       sc-lib-X.X.X.RELEASE-sources.jar          (source code of the SC library)
       sc-web-X.X.X.RELEASE-sources.jar          (source code of the SC web library)
 
 Kit download:
 -------------
  https://www.stabilit.ch/download/public/sc/
  or 
  http://sourceforge.net/projects/serviceconnecto/files/ (selected versions)
 
 
 Source code repository:
 ----------------------- 
  http://www.stabilit.ch/svn/repos/sc (latest branch, all tagged versions, restricted access)
  or
  http://serviceconnecto.svn.sourceforge.net/ (selected tagged versions, public access) 


 Pre-requisites:
 ---------------
  Java 1.6.0_18 or later (1.6.0_26 is recommended)
  Apache 2.0 or later (on node running file services)
  PHP 4.0 or later (on node running file services)
  Tomcat 6.0 or later (for services running as servlet)


 Open Source components used inside SC:
 --------------------------------------
  logback - logging framework
  slf4j - org.slf4j
  Apache commons-configuration
  JBoss netty - org.jboss.netty
  jmx - com.sun.jmx
  EH cache - net.sf.ehcache
  Servlet API - javax.servlet
  

 Installation and verification:
 ------------------------------
 0. Make sure you have installed all required products
 1. Extract the kit into directory of your choice, preserve its structure
 2. Make sure no other application is using ports 7000, 9000, 81, 8080
 3. Invoke bin/start-sc.bat (or .sh on unix/linux platforms)
 4. Invoke bin/start-demo-server.bat (or .sh on unix/linux platforms)
 5. Invoke bin/start-demo-client.bat (or .sh on unix/linux platforms)
 6. Avait the termination of all started components.
 7. Verify the created log files: 
 				logs/demo-client/client.log
 				logs/demo-server/server.log
 				logs/sc/sc.log
 		"ERROR" must not appear here, "WARN" are possible.
 8. Congratulation, you have successfully installed the sc!


 Software upgrade
 ----------------
 If you made changes to the SC configurations, then preserve these files before upgrade. 
 The upgrade is simply done by extracting the kit file into a directory of the 
 previous installation. 
 For compatibility rules see SC_0_SCMP_E.PDF Chapter "scVersion" included in sc-src_V9.9-999.zip


 Software removal
 ----------------
 1. Stop the running SC
 2. Find directories configured in sc.properties or logback configuration file as:
 			- the log directory
 			- the root.pidPath
 			- the root.dumpPath
 			- the cache.diskPath
		Delete these directories
 3. Delete the directory where you have extracted the SC kit file.


 Release notes:
 --------------
	See doc/Open_Issues.xls inside the develoment kit


 Special configurations:
 -----------------------
- logging properties are configured for troubleshooting purposes and may produce large files
- the SC is configured by sc.properties: http=localhost:7000 tcp=localhost:9000
- demo-client is hardcoded connect to port 7000
- demo-server is hardcoded connect to port 9000


--------------------------------------------------------------------------------
 *                                                                             *
 * Copyright � 2010 STABILIT Informatik AG, Switzerland                        *
 *                                                                             *
 * Licensed under the Apache License, Version 2.0 (the "License");             *
 * you may not use this file except in compliance with the License.            *
 * You may obtain a copy of the License at                                     *
 *                                                                             *
 * http://www.apache.org/licenses/LICENSE-2.0                                  *
 *                                                                             *
 * Unless required by applicable law or agreed to in writing, software         *
 * distributed under the License is distributed on an "AS IS" BASIS,           *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    *
 * See the License for the specific language governing permissions and         *
 * limitations under the License.                                              *
 *                                                                             *
 --------------------------------------------------------------------------------