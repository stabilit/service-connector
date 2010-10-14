package org.serviceconnector.ctrl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

public class ProcessesController {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ProcessesController.class);

	private String fs;
	private String userDir;

	public ProcessesController() {
		fs = System.getProperty("file.separator");
		userDir = System.getProperty("user.dir");
	}

	public String getLog4jPath(String log4jSCProperties) {
		String log4jPath = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + log4jSCProperties;

		return log4jPath;
	}

	public String getPidLogPath(String log4jSCProperties) {
		String log4jPath = getLog4jPath(log4jSCProperties);
		// Read properties file.
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(log4jPath));
		} catch (IOException e) {
		}
		String logDir = properties.getProperty("log.dir");
		String fileName = userDir + fs + logDir + fs + TestConstants.pidLogFile;

		return fileName;
	}

	/**
	 * Create a file with given file name
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void createFile(String fileName) throws IOException {
		try {
			File file = new File(fileName);

			// Create file if it does not exist
			boolean success = file.createNewFile();
			if (success) {
				// File did not exist and was created
			} else {
				// File already exists
			}
		} catch (IOException e) {
			logger.error("createFile", e);
			throw e;
		}
	}

	public void deleteFile(String fileName) throws Exception {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
				for (int i = 0; i < 10; i++) {

					if (file.exists() == false) {
						return;
					}
					Thread.sleep(500);
				}
				logger.error("deleteFile");
				throw new TimeoutException("File was not deleted within allowed wait time.");
			}
		} catch (Exception e) {
			logger.error("deleteFile", e);
			throw e;
		}
	}

	public synchronized boolean existsFile(String fileName) throws Exception {
		for (int i = 0; i < 20; i++) {
			File file = new File(fileName);
			if (file.exists()) {
				return true;
			}
			Thread.sleep(500);
		}
		logger.error("existsFile");
		throw new TimeoutException("File was not created within allowed wait time.");
	}

	public Process startSC(String log4jSCProperties, String scProperties) throws Exception {
		String log4jPath = getLog4jPath(log4jSCProperties);
		String fileName = getPidLogPath(log4jSCProperties);
		deleteFile(fileName);

		String command = "java -Dlog4j.configuration=file:" + log4jPath + " -jar " + userDir + fs + ".." + fs
				+ "service-connector" + fs + "target" + fs + "sc.jar -sc.configuration " + userDir + fs + "src" + fs
				+ "main" + fs + "resources" + fs + scProperties;
		Process scProcess = Runtime.getRuntime().exec(command);

		existsFile(fileName);

		return scProcess;
	}

	public Process restartSC(Process scProcess, String log4jSCProperties, String scProperties) throws Exception {
		scProcess.destroy();
		scProcess.waitFor();
		return startSC(log4jSCProperties, scProperties);
	}

	public void stopProcess(Process p, String log4jProperties) throws Exception {
		p.destroy();
		p.waitFor();
		deleteFile(getPidLogPath(log4jProperties));
	}

	/**
	 * Creates a new JVM and starts a SCServer process in that JVM
	 * 
	 * @param serverType
	 *            "session" or "publish"
	 * @param log4jSrvProperties
	 * @param listenerPort
	 * @param port
	 * @param maxConnections
	 * @param serviceNames
	 * @return Process with JVM in which the server is started
	 * @throws Exception
	 */
	public Process startServer(String serverType, String log4jSrvProperties, int listenerPort, int port,
			int maxConnections, String[] serviceNames) throws Exception {
		String log4jPath = getLog4jPath(log4jSrvProperties);
		String fileName = getPidLogPath(log4jSrvProperties);
		deleteFile(fileName);
		String services = "";
		for (String service : serviceNames) {
			services += " " + service;
		}

		String command = "java -Dlog4j.configuration=file:" + log4jPath + " -jar " + userDir + fs + "target" + fs
				+ "test-server.jar " + serverType + " " + listenerPort + " " + port + " " + maxConnections + " "
				+ fileName + services;
		Process srvProcess = Runtime.getRuntime().exec(command);

		existsFile(fileName);

		return srvProcess;
	}

	public Process restartServer(Process srvProcess, String serverType, String log4jSrvProperties, int listenerPort,
			int port, int maxConnections, String[] serviceNames) throws Exception {
		stopProcess(srvProcess, log4jSrvProperties);
		srvProcess = null;
		srvProcess = startServer(serverType, log4jSrvProperties, listenerPort, port, maxConnections, serviceNames);
		return srvProcess;
	}
}
