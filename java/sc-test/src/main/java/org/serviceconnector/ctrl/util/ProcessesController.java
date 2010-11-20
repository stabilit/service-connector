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
		String scPropertiesFull = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + scProperties;
		String log4jFileNameFull = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + log4jSCProperties;
		String pidFileNameFull = getLogDirPath(log4jFileNameFull) + Constants.PID_FILE_NAME;

		String command = "java -Dlog4j.configuration=file:" + log4jFileNameFull + " -jar " + scRunableFull + " -sc.configuration "
				+ scPropertiesFull;
		Process scProcess = Runtime.getRuntime().exec(command);
		try {
			if (FileUtility.exists(pidFileNameFull, 10)) {
				testLogger.info("SC started");
			} else {
				throw new Exception("PID file missing");
			}
		} catch (Exception e) {
			testLogger.info("SC not started! PID file missing");
			throw e;
		}
		return scProcess;
	}

	public void stopSC(Process scProcess, String log4jSCProperties) throws Exception {
		String log4jFileNameFull = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + log4jSCProperties;
		String pidFileNameFull = getLogDirPath(log4jFileNameFull) + Constants.PID_FILE_NAME;
		SCMgmtClient client = new SCMgmtClient(TestConstants.LOCALHOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
		try {
			client.attach(5);
			client.killSC();
			FileUtility.notExists(pidFileNameFull, 10);
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
	 * Creates a new JVM and starts a SCServer process in that JVM
	 * 
	 * @param serverType
	 *            ("session" or "publish")
	 * @param log4jProperties
	 *            (file name)
	 * @param listenerPort
	 * @param SCport
	 * @param maxConnections
	 * @param serviceNames
	 *            (list of strings)
	 * @return Process with JVM in which the server is started
	 * @throws Exception
	 */
	public Process startServer(String serverType, String log4jSrvProperties, int listenerPort, int port, int maxConnections,
			String[] serviceNames) throws Exception {

		String srvRunableFull = userDir + fs + ".." + fs + "target" + fs + TestConstants.serverRunable;
		String log4jFileNameFull = getLogDirPath(log4jSrvProperties) + fs + log4jSrvProperties;
		String IDfileNameFull = getLogDirPath(log4jSrvProperties) + fs + "SRV" + serviceNames[0];

		String services = "";
		for (String service : serviceNames) {
			services += " " + service;
		}
		// start server process
		String command = "java -Dlog4j.configuration=file:" + log4jFileNameFull + " -jar " + srvRunableFull + " " + serverType
				+ " " + listenerPort + " " + port + " " + maxConnections + " " + IDfileNameFull + services;
		Process srvProcess = Runtime.getRuntime().exec(command);
		try {
			if (FileUtility.exists(IDfileNameFull, 10)) {
				testLogger.info("Server started");
			} else {
				throw new Exception("ID file missing");
			}
		} catch (Exception e) {
			testLogger.info("Server not started! ID file missing");
			throw e;
		}
		return srvProcess;
	}

	public void stopServer(Process srvProcess, String log4jSrvProperties, String serverName) throws Exception {

		String IDfileNameFull = getLogDirPath(log4jSrvProperties) + fs + "SRV" + serverName;
		try {
			FileUtility.notExists(IDfileNameFull, 10);
			testLogger.info("Server stopped");
		} catch (Exception e) {
			testLogger.info("Cannot stop server:"+serverName +"! Timeout exceeded.");
		} finally {
			srvProcess.destroy();
			srvProcess.waitFor();
		}
	}

	public Process restartServer(Process srvProcess, String serverType, String log4jSrvProperties, int listenerPort, int port,
			int maxConnections, String[] serviceNames) throws Exception {
		stopServer(srvProcess, log4jSrvProperties, serviceNames[0]);
		srvProcess = null;
		srvProcess = startServer(serverType, log4jSrvProperties, listenerPort, port, maxConnections, serviceNames);
		return srvProcess;
	}
}
