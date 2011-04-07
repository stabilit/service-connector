/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.ctrl.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestPublishServiceMessageCallback;
import org.serviceconnector.TestSessionServiceMessageCallback;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCMessageCallback;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.util.FileUtility;

public class ProcessesController {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(ProcessesController.class);
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());

	private String fs;
	private String userDir;

	public ProcessesController() {
		fs = System.getProperty("file.separator");
		userDir = System.getProperty("user.dir");
	}

	/**
	 * get path of the pid file directory configured in the property file
	 */
	private String getPidPath(String propertyFile) throws Exception {
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new EnvironmentConfiguration());
		compositeConfig.addConfiguration(new PropertiesConfiguration(propertyFile));
		compositeConfig.addConfiguration(new SystemConfiguration());
		// Read & parse properties file.
		String pidPath = compositeConfig.getString(Constants.ROOT_PID_PATH);
		File configFile = new File(pidPath);
		if (configFile.exists() == false) {
			configFile.mkdir();
		}
		if (configFile.isDirectory() == false) {
			throw new Exception("wrong property for key=" + Constants.ROOT_PID_PATH);
		}
		return configFile.getAbsolutePath();
	}

	private String getPortFromConfFile(String scPropertiesFullName) throws Exception {
		// Read & parse properties file.
		Properties properties = new Properties();
		properties.load(new FileInputStream(scPropertiesFullName));
		return properties.getProperty(TestConstants.configPortToken);
	}

	public boolean isSCDisabled() {
		String scDisabled = System.getProperty("scDisabled");
		if (scDisabled == null) {
			return false;
		}
		return "true".equals(scDisabled.toLowerCase());
	}

	public String getSCConfigurationFile() {
		String scConfiguration = System.getProperty("scConfiguration");
		if (scConfiguration == null) {
			return TestConstants.SC0Properties;
		}
		return scConfiguration;
	}

	public Map<String, ProcessCtx> startSCEnvironment(List<ServiceConnectorDefinition> scDefs) throws Exception {
		Map<String, ProcessCtx> proccessContexts = new HashMap<String, ProcessCtx>();

		for (ServiceConnectorDefinition serviceConnectorDefinition : scDefs) {
			String scName = serviceConnectorDefinition.getName();
			String log4jSCProperties = serviceConnectorDefinition.getLog4jFileName();
			String scProperties = serviceConnectorDefinition.getProperyFileName();
			proccessContexts.put(scName, this.startSC(scName, log4jSCProperties, scProperties));
		}
		return proccessContexts;
	}

	public void stopSCEnvironment(Map<String, ProcessCtx> scContexts) throws Exception {
		for (ProcessCtx scContext : scContexts.values()) {
			this.stopSC(scContext);
		}
	}

	public ProcessCtx startSC(String scName, String log4jSCProperties, String scProperties) throws Exception {

		ProcessCtx proc = new ProcessCtx();
		proc.setProcessName(scName);

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

		String logDirPath = this.getPidPath(scProperties);
		String pidFileFullName = logDirPath + fs + Constants.PID_FILE_NAME;
		proc.setPidFileName(pidFileFullName);

		// set sc port to SC stop at the end
		proc.setSCPort(Integer.parseInt(this.getPortFromConfFile(scPropertiesFullName)));

		/*
		 * start SC process Arguments:
		 * [0] -Dlog4j.configuration=file
		 * [1] log4jProperties
		 * [2] -jar
		 * [3] SC runnable
		 * [4] -sc.configuration
		 */
		String command = "java -Xmx1024M -Dlog4j.configuration=file:" + log4jFileFullName + " -jar " + scRunableFullName + " "
				+ Constants.CLI_CONFIG_ARG + " " + scPropertiesFullName;

		Process process = Runtime.getRuntime().exec(command);
		proc.setProcess(process);
		int timeout = 15; // seconds
		try {
			FileUtility.waitExistsAndLocked(pidFileFullName, timeout);
			testLogger.info(scName + " started");
		} catch (Exception e) {
			process.destroy();
			process.waitFor();
			testLogger.info(e.getMessage());
			testLogger.error(scName + " not started within " + timeout + " seconds! Timeout exceeded.");
			throw e;
		}
		return proc;
	}

	public void stopSC(ProcessCtx scProcess) throws Exception {
		int timeout = 15; // seconds
		try {
			if (FileUtility.exists(scProcess.getPidFileName())) {
				// file exists
				if (FileUtility.isFileLocked(scProcess.getPidFileName())) {
					// file is locked - SC is running
					SCMgmtClient client = new SCMgmtClient(TestConstants.HOST, scProcess.getSCPort(), ConnectionType.NETTY_TCP);
					client.attach(timeout);
					client.killSC();
				}
				FileUtility.waitNotExistsOrUnlocked(scProcess.getPidFileName(), timeout);
			}
			testLogger.info(scProcess.getProcessName() + " stopped");
		} catch (Exception e) {
			testLogger.info(e.getMessage());
			testLogger.error("Cannot stop " + scProcess.getProcessName() + "! Timeout exceeded.");
		} finally {
			if (scProcess.isRunning()) {
				scProcess.getProcess().destroy();
				scProcess.getProcess().waitFor();
				// make sure the pid file is deleted under any circumstances
				try {
					FileUtility.deleteFile(scProcess.getPidFileName());
				} catch (Exception e) {
				}
			}
		}
	}

	public Map<String, ProcessCtx> startServerEnvironment(List<ServerDefinition> serverDefs) throws Exception {

		Map<String, ProcessCtx> proccessContexts = new HashMap<String, ProcessCtx>();
		for (ServerDefinition srvDef : serverDefs) {
			ProcessCtx srvProcess = this.startServer(srvDef.getServerType(), srvDef.getLog4jproperty(), srvDef.getServerName(),
					srvDef.getServerPort(), srvDef.getScPort(), srvDef.getMaxSessions(), srvDef.getMaxConnections(),
					srvDef.getConnectionType(), srvDef.getServiceNames(), srvDef.getTimezone(), srvDef.getNics());
			proccessContexts.put(srvDef.getServerName(), srvProcess);
		}
		return proccessContexts;
	}

	public void stopServerEnvironment(Map<String, ProcessCtx> srvContexts) throws Exception {
		for (ProcessCtx srvContext : srvContexts.values()) {
			this.stopServer(srvContext);
		}
	}

	public ProcessCtx startServer(String serverType, String log4jSrvProperties, String serverName, int listenerPort, int scPort,
			int maxSessions, int maxConnections, String serviceNames) throws Exception {
		return this.startServer(serverType, log4jSrvProperties, serverName, listenerPort, scPort, maxSessions, maxConnections,
				ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, serviceNames, null, TestConstants.HOST);
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
			int maxSessions, int maxConnections, String serviceNames, String timezone, String nics) throws Exception {
		return this.startServer(serverType, log4jSrvProperties, serverName, listenerPort, scPort, maxSessions, maxConnections,
				ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE, serviceNames, timezone, nics);
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
			int maxSessions, int maxConnections, ConnectionType connectionType, String serviceNames, String timezone, String nics)
			throws Exception {

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

		String pidFileNameFull = userDir + fs + "logs" + fs + "srv" + fs + serverName + ".pid";
		proc.setPidFileName(pidFileNameFull);

		proc.setServiceNames(serviceNames);
		proc.setProcessName(serverName);
		proc.setConnectionType(connectionType);
		proc.setCommunicatorType(serverType);
		proc.setSCPort(scPort);
		/*
		 * start server process Arguments:
		 * [0] -Dlog4j.configuration=file
		 * [1] log4jProperties
		 * [2] -jar [3] server runnable
		 * [4] serverType ("session" or "publish")
		 * [5] serverName
		 * [6] listenerPort
		 * [7] SC port
		 * [8] maxSessions
		 * [9] maxConnections
		 * [10] connectionType ("netty.tcp" or "netty.http")
		 * [11] serviceNames (comma delimited list)
		 * [12] nics (comma separated list)
		 */
		String timezoneParam = null;
		if (timezone != null) {
			// prepare timezoneParam
			timezoneParam = " -Duser.timezone=" + timezone;
		} else {
			// empty timezoneParam
			timezoneParam = "";
		}
		String command = "java -Xmx1024M -Dlog4j.configuration=file:" + log4jFileFullName + timezoneParam + " -jar "
				+ srvRunablFullName + " " + serverType + " " + serverName + " " + listenerPort + " " + scPort + " " + maxSessions
				+ " " + maxConnections + " " + connectionType.getValue() + " " + serviceNames + " " + nics;
		Process srvProcess = Runtime.getRuntime().exec(command);
		proc.setProcess(srvProcess);
		int timeout = 15;
		try {
			FileUtility.waitExistsAndLocked(pidFileNameFull, timeout);
			;
			testLogger.info("Server " + serverName + " started");
		} catch (Exception e) {
			srvProcess.destroy();
			srvProcess.waitFor();
			testLogger.info(e.getMessage());
			testLogger.error("Server " + serverName + "not started within " + timeout + " seconds! Timeout exceeded.");
			throw e;
		}
		return proc;
	}

	public void stopServer(ProcessCtx srvProcess) throws Exception {
		int timeout = 15; // seconds
		try {
			if (FileUtility.exists(srvProcess.getPidFileName())) {
				SCMgmtClient clientMgmt = new SCMgmtClient(TestConstants.HOST, srvProcess.getSCPort(),
						srvProcess.getConnectionType());
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
				FileUtility.waitNotExistsOrUnlocked(srvProcess.getPidFileName(), timeout);
			}
			testLogger.info("Server " + srvProcess.getProcessName() + " stopped");
		} catch (Exception e) {
			testLogger.info(e.getMessage());
			testLogger.error("Cannot stop " + srvProcess.getProcessName() + " server! Timeout exceeded.");
		} finally {
			if (srvProcess.isRunning()) {
				srvProcess.getProcess().destroy();
				srvProcess.getProcess().waitFor();
				// make sure the pid file is deleted under any circumstances
				try {
					FileUtility.deleteFile(srvProcess.getPidFileName());
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
	 * @param keepAliveIntervalSeconds
	 *            the keep alive interval in seconds
	 * @param serviceName
	 *            the service name
	 * @param echoIntervalSeconds
	 *            the echo interval in seconds
	 * @param echoTimeoutSeconds
	 *            the echo timeout in seconds
	 * @param noDataIntervalSeconds
	 *            the no data interval seconds
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
	private ProcessCtx startClient(String clientType, String log4jClnProperties, String clientName, String scHost, int scPort,
			ConnectionType connectionType, int maxConnections, int keepAliveIntervalSeconds, String serviceName,
			int echoIntervalSeconds, int echoTimeoutSeconds, int noDataIntervalSeconds, String methodsToInvoke) throws Exception {

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

		String pidFileNameFull = userDir + fs + "logs" + fs + "cln" + fs + clientName + ".pid";
		proc.setPidFileName(pidFileNameFull);

		proc.setServiceNames(serviceName);
		proc.setProcessName(clientName);
		proc.setConnectionType(connectionType);
		proc.setCommunicatorType(clientType);
		/*
		 * start client process Arguments: 
		 * [0] -Dlog4j.configuration=file 
		 * [1] log4jProperties 
		 * [2] -jar 
		 * [3] client runnable
		 * [4] clientType ("session" or "publish") 
		 * [5] clientName 
		 * [6] scHost 
		 * [7] scPort 
		 * [8] ConnectionType ("netty.tcp" or "netty.http") 
		 * [9] maxConnections 
		 * [10] keepAliveIntervalSeconds 
		 * [11] serviceName 
		 * [12] echoIntervalSeconds
		 * [13] echoTimeoutSeconds
		 * [14] noDataIntervalSeconds
		 * [15] methodsToInvoke (split * by | "initAttach|detach")
		 */
		String command = "java -Dlog4j.configuration=file:" + log4jFileFullName + " -jar " + clnRunablFullName + " " + clientType
				+ " " + clientName + " " + scHost + " " + scPort + " " + connectionType.getValue() + " " + maxConnections + " "
				+ keepAliveIntervalSeconds + " " + serviceName + " " + echoIntervalSeconds + " " + echoTimeoutSeconds + " "
				+ noDataIntervalSeconds + " " + methodsToInvoke;
		Process clnProcess = Runtime.getRuntime().exec(command);
		proc.setProcess(clnProcess);
		int timeout = 15;
		try {
			FileUtility.waitExistsAndLocked(pidFileNameFull, timeout);
			testLogger.info("Client " + clientName + " started doing: " + methodsToInvoke);
		} catch (Exception e) {
			clnProcess.destroy();
			clnProcess.waitFor();
			testLogger.info(e.getMessage());
			testLogger.error(clientName + " not started within " + timeout + " seconds! Timeout exceeded.");
			throw e;
		}
		return proc;
	}

	public ProcessCtx startSessionClient(String log4jClnProperties, String clientName, String scHost, int scPort,
			ConnectionType connectionType, int maxConnections, int keepAliveIntervalSeconds, String serviceName,
			int echoIntervalSeconds, int echoTimeoutSeconds, String methodsToInvoke) throws Exception {
		return this.startClient(TestConstants.COMMUNICATOR_TYPE_SESSION, log4jClnProperties, clientName, scHost, scPort,
				connectionType, maxConnections, keepAliveIntervalSeconds, serviceName, echoIntervalSeconds, echoTimeoutSeconds, 0,
				methodsToInvoke);
	}

	public ProcessCtx startPublishClient(String log4jClnProperties, String clientName, String scHost, int scPort,
			ConnectionType connectionType, int maxConnections, int keepAliveIntervalSeconds, String serviceName,
			int noDataIntervalSeconds, String methodsToInvoke) throws Exception {
		return this
				.startClient(TestConstants.COMMUNICATOR_TYPE_PUBLISH, log4jClnProperties, clientName, scHost, scPort,
						connectionType, maxConnections, keepAliveIntervalSeconds, serviceName, 0, 0, noDataIntervalSeconds,
						methodsToInvoke);
	}

	public void stopClient(ProcessCtx clnProcess) throws Exception {
		if (clnProcess.isRunning()) {
			clnProcess.getProcess().destroy();
			clnProcess.getProcess().waitFor();
			FileUtility.deleteFile(clnProcess.getPidFileName());
		}
		testLogger.error("Client " + clnProcess.getProcessName() + "stopped.");
	}

	public void waitForClientTermination(ProcessCtx[] clientCtxs) {
		for (ProcessCtx processCtx : clientCtxs) {
			this.waitForClientTermination(processCtx);
		}
	}

	public void waitForClientTermination(ProcessCtx clientCtx) {
		try {
			clientCtx.getProcess().waitFor();
			FileUtility.deleteFile(clientCtx.getPidFileName());
			testLogger.error("Client " + clientCtx.getProcessName() + " stopped.");
		} catch (InterruptedException e) {
			testLogger.error("Waiting for Client " + clientCtx.getProcessName() + "termination failed.");
		}
	}
}
