package org.serviceconnector.ctrl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.serviceconnector.log.Loggers;
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

	private String getLog4jFullFileName(String log4jSCProperties) {
		String log4jFullFileName = userDir + fs + "src" + fs + "main" + fs + "resources" + fs + log4jSCProperties;
		return log4jFullFileName;
	}

	private String getPidFullFileName(String log4jSCProperties) {
		String log4jFullFileName = getLog4jFullFileName(log4jSCProperties);
		// Read 6 parse properties file.
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(log4jFullFileName));
		} catch (IOException e) {
			logger.error("Error parsing "+log4jSCProperties, e);
		}
		String logDir = properties.getProperty(TestConstants.logDirectoryToken);
		String fullFileName = userDir + fs + logDir + fs + TestConstants.pidLogFile;
		return fullFileName;
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
			logger.error("File "+fileName+" cannot be created", e);
			throw e;
		}
	}

	private void deleteFile(String fileName) throws Exception {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
				for (int i = 0; i < 20; i++) {
					if (file.exists() == false) {
						return;
					}
					Thread.sleep(500);
				}
				throw new TimeoutException("File "+fileName+" was not deleted within allowed wait time.");
			}
		} catch (Exception e) {
			logger.error("File "+fileName+" cannot be deleted", e);
			throw e;
		}
	}

	private synchronized boolean existsFile(String fileName) throws Exception {
		// wait max 10 seconds for file creation
		File file = new File(fileName);
		for (int i = 0; i < 20; i++) {
			if (file.exists()) {
				return true;
			}
			Thread.sleep(500);
		}
		logger.error("File "+fileName+" was not created within allowed wait time.");
		throw new TimeoutException("File "+fileName+" was not created within allowed wait time.");
		
	}

	public Process startSC(String log4jSCProperties, String scProperties) throws Exception {
		String log4jFullFileName = getLog4jFullFileName(log4jSCProperties);
		String fileName = getPidFullFileName(log4jSCProperties);
		deleteFile(fileName);

		String command = "java -Dlog4j.configuration=file:" + log4jFullFileName + " -jar " + userDir + fs + ".." + fs
				+ "service-connector" + fs + "target" + fs + TestConstants.scRunable +" -sc.configuration " + userDir + fs + "src" + fs
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
		deleteFile(getPidFullFileName(log4jProperties));
	}

	/**
	 * Creates a new JVM and starts a SCServer process in that JVM
	 * 
	 * @param serverType ("session" or "publish")
	 * @param log4jSrvProperties (file name)
	 * @param listenerPort
	 * @param port
	 * @param maxConnections
	 * @param serviceNames (list of strings)
	 * @return Process with JVM in which the server is started
	 * @throws Exception
	 */
	public Process startServer(String serverType, String log4jSrvProperties, int listenerPort, int port,
			int maxConnections, String[] serviceNames) throws Exception {
		String log4jFullFileName = getLog4jFullFileName(log4jSrvProperties);
		String fileName = getPidFullFileName(log4jSrvProperties);
		deleteFile(fileName);
		String services = "";
 		for (String service : serviceNames) {
			services += " " + service;
		}

 		// start server process
		String command = "java -Dlog4j.configuration=file:" + log4jFullFileName + " -jar " + userDir + fs + "target" + fs
				+ TestConstants.scRunable + " " + serverType + " " + listenerPort + " " + port + " " + maxConnections + " "
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
