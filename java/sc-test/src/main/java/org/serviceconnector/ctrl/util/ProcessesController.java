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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestPublishServiceMessageCallback;
import org.serviceconnector.TestSessionServiceMessageCallback;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.FileUtility;

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
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new PropertiesConfiguration(log4jProperties));
		compositeConfig.addConfiguration(new EnvironmentConfiguration());
		// Read & parse properties file.
		return userDir + fs + compositeConfig.getString(TestConstants.logDirectoryToken);
	}

	private String getPortFromConfFile(String scPropertiesFullName) throws Exception {
		// Read & parse properties file.
		Properties properties = new Properties();
		properties.load(new FileInputStream(scPropertiesFullName));
		return properties.getProperty(TestConstants.configPortToken);
	}

	public ProcessCtx startSC(String log4jSCProperties, String scProperties) throws Exception {

		ProcessCtx proc = new ProcessCtx();

		String scRunableFullName = userDir + fs + "target" + fs + TestConstants.scRunable;
		if (FileUtility.notExists(scRunableFullName)) {
			testLogger.error("File:" + scRunableFullName + " does not exist!");
			throw new Exception("File:" + scRunableFullName + " does not exist!");
		}
		proc.setRunableName(scRunableFullName);

		String scPropertiesFullName = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + scProperties;
		if (FileUtility.notExists(scPropertiesFullName)) {
			testLogger.error("File:" + scPropertiesFullName + " does not exist!");
			throw new Exception("File:" + scPropertiesFullName + " does not exist!");
		}
		proc.setPropertyFileName(scPropertiesFullName);

		String log4jFileFullName = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + log4jSCProperties;
		if (FileUtility.notExists(log4jFileFullName)) {
			testLogger.error("File:" + log4jFileFullName + " does not exist!");
			throw new Exception("File:" + log4jFileFullName + " does not exist!");
		}
		proc.setLog4jFileName(log4jFileFullName);

		String pidFileFullName = userDir + fs + "log" + fs + "sc" + fs + Constants.PID_FILE_NAME;
		proc.setPidFileName(pidFileFullName);

		// set sc port to SC stop at the end
		proc.setSCPort(Integer.parseInt(this.getPortFromConfFile(scPropertiesFullName)));

		/*
		 * start SC process Arguments: [0] -Dlog4j.configuration=file [1] log4jProperties [2]-jar [3] SC runnable [4]
		 * -sc.configuration
		 */
		String command = "java -Dlog4j.configuration=file:" + log4jFileFullName + " -jar " + scRunableFullName
				+ " -sc.configuration " + scPropertiesFullName;

		Process process = Runtime.getRuntime().exec(command);
		proc.setProcess(process);
		int timeout = 10; // seconds
		try {
			FileUtility.waitExists(pidFileFullName, timeout);
			testLogger.info("SC started");
		} catch (Exception e) {
			testLogger.info(e.getMessage());
			testLogger.error("SC not started within " + timeout + " seconds! Timeout exceeded.");
			throw e;
		}
		return proc;
	}

	public void stopSC(ProcessCtx scProcess) throws Exception {
		int timeout = 10; // seconds
		try {
			if (FileUtility.exists(scProcess.getPidFileName())) {
				SCMgmtClient client = new SCMgmtClient(TestConstants.HOST, scProcess.getSCPort(), ConnectionType.NETTY_TCP);
				client.attach(timeout);
				client.killSC();
				FileUtility.waitNotExists(scProcess.getPidFileName(), timeout);
			}
			testLogger.info("SC stopped");
		} catch (Exception e) {
			testLogger.info(e.getMessage());
			testLogger.error("Cannot stop SC! Timeout exceeded.");
		} finally {
			if (scProcess.isRunning()) {
				scProcess.getProcess().destroy();
				scProcess.getProcess().waitFor();
				// make sure the pid file is deleted under any circumstances
				try {
					FileUtility.deletePIDfile(scProcess.getPidFileName());
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Start a server in a new JVM. Parameters controls the server execution.
	 * 
	 * @param serverType
	 *            ("session" or "publish")
	 * @param log4jProperties
	 *            (file name)
	 * @param serverName
	 * @param listenerPort
	 * @param SCport
	 * @param maxSessions
	 * @param maxConnections
	 * @param serviceNames
	 *            (comma delimited list)
	 * @return Process with JVM in which the server is started
	 * @throws Exception
	 */
	public ProcessCtx startServer(String serverType, String log4jSrvProperties, String serverName, int listenerPort, int scPort,
			int maxSessions, int maxConnections, String serviceNames) throws Exception {
		return this.startServer(serverType, log4jSrvProperties, serverName, listenerPort, scPort, maxSessions, maxConnections,
				ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, serviceNames);
	}

	/**
	 * Start a server in a new JVM. Parameters controls the server execution.
	 * 
	 * @param serverType
	 *            ("session" or "publish")
	 * @param log4jProperties
	 *            (file name)
	 * @param serverName
	 * @param listenerPort
	 * @param SCport
	 * @param maxSessions
	 * @param maxConnections
	 * @param connectionType
	 * @param serviceNames
	 *            (comma delimited list)
	 * @return Process with JVM in which the server is started
	 * @throws Exception
	 */
	public ProcessCtx startServer(String serverType, String log4jSrvProperties, String serverName, int listenerPort, int scPort,
			int maxSessions, int maxConnections, ConnectionType connectionType, String serviceNames) throws Exception {

		ProcessCtx proc = new ProcessCtx();

		String srvRunablFullName = userDir + fs + "target" + fs + TestConstants.serverRunable;
		if (FileUtility.notExists(srvRunablFullName)) {
			testLogger.error("File:" + srvRunablFullName + " does not exist!");
			throw new Exception("File:" + srvRunablFullName + " does not exist!");
		}
		proc.setRunableName(srvRunablFullName);

		String log4jFileFullName = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + log4jSrvProperties;
		if (FileUtility.notExists(log4jFileFullName)) {
			testLogger.error("File:" + log4jFileFullName + " does not exist!");
			throw new Exception("File:" + log4jFileFullName + " does not exist!");
		}
		proc.setLog4jFileName(log4jFileFullName);

		String pidFileNameFull = userDir + fs + "log" + fs + "srv" + fs + serverName + ".pid";
		proc.setPidFileName(pidFileNameFull);

		proc.setServiceNames(serviceNames);
		proc.setProcessName(serverName);
		proc.setConnectionType(connectionType);
		proc.setCommunicatorType(serverType);
		/*
		 * start server process Arguments: [0] -Dlog4j.configuration=file [1] log4jProperties [2] -jar [3] server runnable
		 * [4] serverType ("session" or "publish") [5] serverName [6] listenerPort [7] SC port [8] maxSessions [9]
		 * maxConnections [10] connectionType ("netty.tcp" or "netty.http") [11] serviceNames (comma delimited list)
		 */
		String command = "java -Dlog4j.configuration=file:" + log4jFileFullName + " -jar " + srvRunablFullName + " " + serverType
				+ " " + serverName + " " + listenerPort + " " + scPort + " " + maxSessions + " " + maxConnections + " " + " "
				+ connectionType.getValue() + " " + serviceNames;
		Process srvProcess = Runtime.getRuntime().exec(command);
		proc.setProcess(srvProcess);
		int timeout = 10;
		try {
			FileUtility.waitExists(pidFileNameFull, timeout);
			testLogger.info("Server started");
		} catch (Exception e) {
			testLogger.info(e.getMessage());
			testLogger.error("Server not started within " + timeout + " seconds! Timeout exceeded.");
			throw e;
		}
		return proc;
	}

	public void stopServer(ProcessCtx srvProcess) throws Exception {
		int timeout = 10; // seconds
		try {
			if (FileUtility.exists(srvProcess.getPidFileName())) {
				SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_TCP, ConnectionType.NETTY_TCP);
				clientMgmt.attach(timeout);
				String serviceName = srvProcess.getServiceNames().split(",")[0];
				clientMgmt.enableService(serviceName); // service might be disabled during tests
				if (srvProcess.getCommunicatorType() == TestConstants.COMMUNICATOR_TYPE_SESSION) {
					// Create session with KILL command
					SCSessionService scSessionService = clientMgmt.newSessionService(serviceName);
					SCMessage scMessage = new SCMessage();
					scMessage.setSessionInfo(TestConstants.killServerCmd);
					try {
						scSessionService.createSession(scMessage, new TestSessionServiceMessageCallback(scSessionService));
					} catch (SCServiceException ex) {
					}
				} else {
					// Subscribe with KILL command
					SCPublishService scPublishService = clientMgmt.newPublishService(serviceName);
					SCSubscribeMessage scMessage = new SCSubscribeMessage();
					SCMessageCallback cbk = new TestPublishServiceMessageCallback(scPublishService);
					scMessage.setSessionInfo(TestConstants.killServerCmd);
					scMessage.setMask("ABCD"); // dummy (mask may not be empty)
					try {
						scPublishService.subscribe(scMessage, cbk);
					} catch (SCServiceException ex) {
					}
				}
				clientMgmt.detach();
				FileUtility.waitNotExists(srvProcess.getPidFileName(), timeout);
			}
			testLogger.info("Server stopped");
		} catch (Exception e) {
			testLogger.info(e.getMessage());
			testLogger.error("Cannot stop server! Timeout exceeded.");
		} finally {
			if (srvProcess.isRunning()) {
				srvProcess.getProcess().destroy();
				srvProcess.getProcess().waitFor();
				// make sure the pid file is deleted under any circumstances
				try {
					FileUtility.deletePIDfile(srvProcess.getPidFileName());
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Start client.
	 * 
	 * @param clientType
	 *            the client type
	 * @param log4jClnProperties
	 *            the log4j cln properties
	 * @param clientName
	 *            the client name
	 * @param scHost
	 *            the sc host
	 * @param scPort
	 *            the sc port
	 * @param connectionType
	 *            the connection type
	 * @param maxConnections
	 *            the max connections
	 * @param keepAliveIntervalInSeconds
	 *            the keep alive interval in seconds
	 * @param serviceName
	 *            the service name
	 * @param echoIntervalInSeconds
	 *            the echo interval in seconds
	 * @param echoTimeoutInSeconds
	 *            the echo timeout in seconds
	 * @param methodsToInvoke
	 *            the methods to invoke
	 * @return the process ctx
	 * @throws Exception
	 *             the exception <br>
	 * <br>
	 *             Example to call: <br>
	 *             ProcessCtx clnCtx = ctrl.startClient(TestConstants.COMMUNICATOR_TYPE_SESSION, TestConstants.log4jSCProperties,
	 *             TestConstants.sesServiceName1, TestConstants.HOST, TestConstants.PORT_HTTP, ConnectionType.NETTY_HTTP, 10, 0,
	 *             TestConstants.sesServiceName1, 300, 5, "\"initAttach|detach\"");
	 */
	public ProcessCtx startClient(String clientType, String log4jClnProperties, String clientName, String scHost, int scPort,
			ConnectionType connectionType, int maxConnections, int keepAliveIntervalInSeconds, String serviceName,
			int echoIntervalInSeconds, int echoTimeoutInSeconds, String methodsToInvoke) throws Exception {

		ProcessCtx proc = new ProcessCtx();

		String clnRunablFullName = userDir + fs + "target" + fs + TestConstants.clientRunable;
		if (FileUtility.notExists(clnRunablFullName)) {
			testLogger.error("File:" + clnRunablFullName + " does not exist!");
			throw new Exception("File:" + clnRunablFullName + " does not exist!");
		}
		proc.setRunableName(clnRunablFullName);

		String log4jFileFullName = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + log4jClnProperties;
		if (FileUtility.notExists(log4jFileFullName)) {
			testLogger.error("File:" + log4jFileFullName + " does not exist!");
			throw new Exception("File:" + log4jFileFullName + " does not exist!");
		}
		proc.setLog4jFileName(log4jFileFullName);

		String pidFileNameFull = userDir + fs + "log" + fs + "srv" + fs + clientName + ".pid";
		proc.setPidFileName(pidFileNameFull);

		proc.setServiceNames(serviceName);
		proc.setProcessName(clientName);
		proc.setConnectionType(connectionType);
		proc.setCommunicatorType(clientType);
		/*
		 * start client process Arguments: [0] -Dlog4j.configuration=file [1] log4jProperties [2] -jar [3] client runnable
		 * [4] clientType ("session" or "publish") [5] clientName [6] scHost [7] scPort [8] ConnectionType ("netty.tcp" or "netty.http") [9] maxConnections [10]
		 * keepAliveIntervalSeconds [11] serviceName [12] echoIntervalInSeconds[13] echoTimeoutInSeconds[14] methodsToInvoke (split by | "init|attach|detach")[15]
		 */
		String command = "java -Dlog4j.configuration=file:" + log4jFileFullName + " -jar " + clnRunablFullName + " " + clientType
				+ " " + clientName + " " + scHost + " " + scPort + " " + connectionType.getValue() + " " + maxConnections + " "
				+ keepAliveIntervalInSeconds + " " + serviceName + " " + echoIntervalInSeconds + " " + echoTimeoutInSeconds + " "
				+ methodsToInvoke;
		Process clnProcess = Runtime.getRuntime().exec(command);
		proc.setProcess(clnProcess);
		testLogger.info("Client started");
		return proc;
	}

}
