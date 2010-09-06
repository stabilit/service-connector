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

	public TestEnvironmentController() {
		fs = System.getProperty("file.separator");
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
		String userDir = System.getProperty("user.dir");
		String log4jPath = userDir + fs + "src" + fs + "main" + fs + "resources" + fs
				+ log4jSCProperties;
		// Read properties file.
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(log4jPath));
		} catch (IOException e) {
		}
		String logDir = properties.getProperty("log.dir");
		String fileName = userDir + fs + logDir + fs + pidLogFile;

		deleteFile(fileName);

		String command = "java -Dlog4j.configuration=file:" + log4jPath + " -jar " + userDir + fs
				+ ".." + fs + "service-connector" + fs + "target" + fs + "sc.jar -filename "
				+ userDir + fs + "src" + fs + "main" + fs + "resources" + fs + scProperties;
		Process p = Runtime.getRuntime().exec(command);

		existsFile(fileName);

		return p;
	}

	public Process restartSC(Process p, String log4jSCProperties, String scProperties)
			throws Exception {
		p.destroy();
		return startSC(log4jSCProperties, scProperties);
	}
	
	public Process startServer(String log4jSCProperties) throws Exception {
		String userDir = System.getProperty("user.dir");
		String log4jPath = userDir + fs + "src" + fs + "main" + fs + "resources" + fs
				+ log4jSCProperties;
		// Read properties file.
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(log4jPath));
		} catch (IOException e) {
		}
		String logDir = properties.getProperty("log.dir");
		String fileName = userDir + fs + logDir + fs + pidLogFile;

		deleteFile(fileName);

		String command = "java -Dlog4j.configuration=file:" + log4jPath + " -jar " + userDir + fs
				+ ".." + fs + "service-connector" + fs + "target" + fs + "sc.jar";
		Process p = Runtime.getRuntime().exec(command);

		existsFile(fileName);

		return p;
	}
}
