package com.stabilit.sc.ctrl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

public class TestEnvironmentController {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(TestEnvironmentController.class);

	private static String pidLogFile = "pid.log";
	private static String fs;
	private static String userDir;

	public TestEnvironmentController() {
		fs = System.getProperty("file.separator");
		userDir = System.getProperty("user.dir");
	}

	public String getLog4jPath(String log4jSCProperties) {
		String log4jPath = userDir + fs + "src" + fs + "main" + fs + "resources" + fs
				+ log4jSCProperties;

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
		String fileName = userDir + fs + logDir + fs + pidLogFile;

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
			}
		} catch (Exception e) {
			logger.error("deleteFile", e);
			throw e;
		}
	}

	public boolean existsFile(String fileName) throws Exception {
		for (int i = 0; i < 10; i++) {
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

		String command = "java -Dlog4j.configuration=file:" + log4jPath + " -jar " + userDir + fs
				+ ".." + fs + "sc-impl" + fs + "target" + fs + "sc.jar -filename " + userDir + fs
				+ "src" + fs + "main" + fs + "resources" + fs + scProperties;
		Process p = Runtime.getRuntime().exec(command);

		existsFile(fileName);

		return p;
	}

	public Process restartSC(Process p, String log4jSCProperties, String scProperties)
			throws Exception {
		p.destroy();
		return startSC(log4jSCProperties, scProperties);
	}

	public void stopProcess(Process p, String log4jProperties) throws Exception {
		p.destroy();
		deleteFile(getPidLogPath(log4jProperties));
	}

	public Process startServer(String log4jSCProperties, int listenerPort, int port,
			int maxConnections, String[] serviceNames) throws Exception {
		String log4jPath = getLog4jPath(log4jSCProperties);
		String fileName = getPidLogPath(log4jSCProperties);
		deleteFile(fileName);
		String services = "";
		for (String service : serviceNames) {
			services += " " + service;
		}

		String command = "java -Dlog4j.configuration=file:" + log4jPath + " -jar " + userDir + fs
				+ "target" + fs + "test-server.jar " + listenerPort + " " + port + " "
				+ maxConnections + " " + fileName + services;
		Process p = Runtime.getRuntime().exec(command);

		existsFile(fileName);

		return p;
	}
}
