/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package org.serviceconnector.ctrl.util;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.util.FileUtility;
import org.serviceconnetor.TestConstants;

public class ProcessesController {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ProcessesController.class);
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private String fs;
	private String userDir;

	public ProcessesController() {
		fs = System.getProperty("file.separator");
		userDir = System.getProperty("user.dir");
	}

	/**
	 * get path of the log directory configured in the log4j file
	 */
	private String getLogDirPath(String log4jProperties) throws Exception {
		// Read & parse properties file.
		Properties properties = new Properties();
		properties.load(new FileInputStream(log4jProperties));
		return userDir + fs + properties.getProperty(TestConstants.logDirectoryToken);
	}

	public Process startSC(String log4jSCProperties, String scProperties) throws Exception {
		String scRunableFull = userDir + fs + ".." + fs + "service-connector" + fs + "target" + fs + TestConstants.scRunable;
		if (FileUtility.notExists(scRunableFull)) {
			throw new Exception("File:" + scRunableFull + " does not exist!");
		}

		String scPropertiesFull = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + scProperties;
		if (FileUtility.notExists(scPropertiesFull)) {
			throw new Exception("File:" + scPropertiesFull + " does not exist!");
		}

		String log4jFileNameFull = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + log4jSCProperties;
		if (FileUtility.notExists(log4jFileNameFull)) {
			throw new Exception("File:" + log4jFileNameFull + " does not exist!");
		}

		String pidFileNameFull = getLogDirPath(log4jFileNameFull) + Constants.PID_FILE_NAME;

		/*
		 * start SC process Args: 
		 * [0] -Dlog4j.configuration=file
		 * [1] log4jProperties 
		 * [2]-jar 
		 * [3] SC runnable 
		 * [4] -sc.configuration
		 */
		String command = "java -Dlog4j.configuration=file:" + log4jFileNameFull + " -jar " + scRunableFull + " -sc.configuration "
				+ scPropertiesFull;
		Process scProcess = Runtime.getRuntime().exec(command);
		int timeout = 10; // seconds
		try {
			FileUtility.waitExists(pidFileNameFull, timeout);
			testLogger.info("SC started");
		} catch (Exception e) {
			testLogger.info("SC not started within " + timeout + " seconds! Timeout exceeded.");
			throw e;
		}
		return scProcess;
	}

	
	public void stopSC(Process scProcess, String log4jSCProperties) throws Exception {
		String log4jFileNameFull = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + log4jSCProperties;

		String pidFileNameFull = getLogDirPath(log4jFileNameFull) + Constants.PID_FILE_NAME;
		int timeout = 10; // seconds
		try {
			if (FileUtility.exists(pidFileNameFull)) {
				SCMgmtClient client = new SCMgmtClient(TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
				client.attach(timeout);
				client.killSC();
				FileUtility.waitNotExists(pidFileNameFull, timeout);
			}
			testLogger.info("SC stopped");
		} catch (Exception e) {
			testLogger.info("Cannot stop SC! Timeout exceeded.");
		} finally {
			scProcess.destroy();
			scProcess.waitFor();
		}
	}

	public Process restartSC(Process scProcess, String log4jSCProperties, String scProperties) throws Exception {
		stopSC(scProcess, log4jSCProperties);
		return startSC(log4jSCProperties, scProperties);
	}

	/**
	 * Start a server in a new JVM. Parameters controls the server execution.
	 * 
	 * @param serverType
	 *            ("session" or "publish")
	 * @param log4jProperties
	 *            (file name)
	 * @param listenerPort
	 * @param SCport
	 * @param maxSessions
	 * @param maxConnections
	 * @param serviceNames
	 *            (list of strings)
	 * @return Process with JVM in which the server is started
	 * @throws Exception
	 */
	public Process startServer(String serverType, String log4jSrvProperties, String serverName, int listenerPort, int scPort,
			int maxSessions, int maxConnections, String[] serviceNames) throws Exception {

		String srvRunableFull = userDir + fs + ".." + fs + "target" + fs + TestConstants.serverRunable;
		if (FileUtility.notExists(srvRunableFull)) {
			throw new Exception("File:" + srvRunableFull + " does not exist!");
		}

		String log4jFileNameFull = getLogDirPath(log4jSrvProperties) + fs + log4jSrvProperties;
		if (FileUtility.notExists(log4jFileNameFull)) {
			throw new Exception("File:" + log4jFileNameFull + " does not exist!");
		}

		String pidFileNameFull = getLogDirPath(log4jSrvProperties) + fs + serverName + ".pid";

		String services = "";
		for (String service : serviceNames) {
			services += " " + service;
		}
		/*
		 * start server process Args: 
		 * [0] -Dlog4j.configuration=file 
		 * [1] log4jProperties 
		 * [2] -jar 
		 * [3] server runnable 
		 * [4] serverType ("session" or "publish") 
		 * [5] PID file 
		 * [6] listenerPort 
		 * [7] SC port 
		 * [8] maxSessions 
		 * [9] maxConnections 
		 * [10...] serviceNames
		 */
		String command = "java -Dlog4j.configuration=file:" + log4jFileNameFull + " -jar " + srvRunableFull + " " + serverType
				+ pidFileNameFull + " " + " " + listenerPort + " " + scPort + " " + maxSessions + " " + maxConnections + " " 
				+ services;
		Process srvProcess = Runtime.getRuntime().exec(command);
		int timeout = 10;
		try {
			FileUtility.waitExists(pidFileNameFull, timeout);
			testLogger.info("Server started");
		} catch (Exception e) {
			testLogger.info("Server not started within " + timeout + " seconds! Timeout exceeded.");
			throw e;
		}
		return srvProcess;
	}

	public void stopServer(Process srvProcess, String log4jSrvProperties, String serverName) throws Exception {
		String pidFileNameFull = getLogDirPath(log4jSrvProperties) + fs + serverName + ".pid";
		int timeout = 10; // seconds
		try {
			if (FileUtility.exists(pidFileNameFull)) {
				srvProcess.destroy();
				srvProcess.waitFor();
				FileUtility.waitNotExists(pidFileNameFull, timeout);
			}
			testLogger.info("Server stopped");
		} catch (Exception e) {
			testLogger.info("Cannot stop server:" + serverName + "! Timeout exceeded.");
		} finally {
			srvProcess.destroy();	// just to be sure
			srvProcess.waitFor();	// just to be sure
		}
	}

	public Process restartServer(Process srvProcess, String serverType, String serverName, String log4jSrvProperties, int listenerPort, int port,
			int maxSessions, int maxConnections, String[] serviceNames) throws Exception {
		stopServer(srvProcess, log4jSrvProperties, serviceNames[0]);
		srvProcess = null;
		srvProcess = startServer(serverType, log4jSrvProperties, serverName, listenerPort, port, maxSessions, maxConnections, serviceNames);
		return srvProcess;
	}
}
