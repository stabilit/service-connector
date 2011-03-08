Service Connector - open source messaging middleware
====================================================

 Kit structure:
 --------------
 sc-bin_V9.9-999.zip - kit containing only binaries 
 --/bin					(binaries a scripts to manage sc)
   /bin/unix		(script to run sc as deamon on unix/linux. see the readme.txt)
   /bin/win32		(script and runables to run sc as Windows service on win32 platform)
   /bin/win64  	(script and runables to run sc as Windows service on win64 platform)
   /conf				(sc configuration files)
   /cache				(sc writes temporary cache-files here. See also sc property cache.diskPath)
   /logs				(sc writes all log and dump files here. See log4j properties)
   
 sc-src_V9.9-999.zip - kit containing binaries and additional resources required for development
 --/doc					(sc documentation and java docs)
   /examples		(additional scripts examples and file service configurations)
   /sources			(sources of sc and the demo application)
 
 For complete source see: http://www.stabilit.ch/svn/repos/sc


 Pre-requisites:
 ---------------
  Java 1.6.18 or later
  Apache 2.0 or later (on node running file services)
  PHP 4.0 or later (on node running file services)


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
 		"ERROR" or "FATAL" must not appear here, "WARN" are possible.
 8. Congratulation, you have successfully installed the sc!

 Software upgrade
 ----------------
 If you made changes to the SC configurations, then preserve these files before upgrade. 
 The upgrade is simply done by extracting the kit file into a directory of the 
 previous installation. 
 For compatibility issues see SC_0_SCMP_E.PDF Chapter 9.30 (scVersion)

 Software removal
 ----------------
 1. Stop the running SC
 2. If you made changes to the SC configuration check where you have defined:
 			- the log directory
 			- the root.pidPath
 			- the root.dumpPath
 			- the cache.diskPath
		Delete these directories
 3. Delete the directory where you have extracted the SC kit file.

 Release notes:
 --------------
	See doc/Open_Issues.xls
	(later www.stabilit.ch/bugzilla will contain release notes)


 Special configurations:
 -----------------------
- logging properties are configured for troubleshooting purposes => low performance
- the SC is configured by sc.properties: http=localhost:7000 tcp=localhost:9000
- demo-client is hardcoded connect to port 7000
- demo-server is hardcoded connect to port 9000


--------------------------------------------------------------------------------
 *                                                                             *
 * Copyright © 2010 STABILIT Informatik AG, Switzerland                        *
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