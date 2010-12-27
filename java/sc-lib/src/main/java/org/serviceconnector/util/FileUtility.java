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
package org.serviceconnector.util;

import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class FileUtility.
 */
public class FileUtility {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(FileUtility.class);


	/**
	 *
	 * @param filename
	 * @return true if the given file exists
	 */
	public static boolean exists(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 *
	 * @param filename
	 * @return true if the given file does not exist
	 */
	public static boolean notExists(String filename){
		return !exists(filename);
	}
	

	/**
	 * 
	 * @param filename to look for
	 * @param nrSeconds to wait (check is done in 1 second interval)
	 * @throws Exception if the file does not exist after the given time
	 */
	public static void waitExists(String filename, int nrSeconds) throws Exception {
		for (int i = 0; i < (nrSeconds*10); i++) {
			if (exists(filename)) {
				return;
			}
			Thread.sleep(100);
		}
		throw new TimeoutException("File:" + filename + " does not exist after " + nrSeconds + " seconds timeout.");
	}


	/**
	 * 
	 * @param filename to look for
	 * @param nrSeconds to wait (check is done in 1 second interval)
	 * @throws Exception if the file still exists after the given time
	 */
	public static void waitNotExists(String filename, int nrSeconds) throws Exception {
		for (int i = 0; i < (nrSeconds*10); i++) {
			if (notExists(filename)) {
				return;
			}
			Thread.sleep(100);
		}
		throw new TimeoutException("File:" + filename + " does still exist after " + nrSeconds + " seconds timeout.");
	}
	
	/**
	 * Create file containing the PID. Is used for testing purpose to verify that process is running properly.
	 * @return 
	 */
	public static void createPIDfile(String fileNameFull) throws Exception {
		FileWriter fw = null;
		try {
			String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			long pid = Long.parseLong(processName.split("@")[0]);

			// create the pid file
			File pidFile = new File(fileNameFull);
			fw = new FileWriter(pidFile);
			fw.write("pid: " + pid);
			fw.flush();
			logger.log(Level.OFF, "Create PID-file: " + fileNameFull + " PID:" + pid);
		} finally {
			if (fw != null) {
				fw.close();
			}
		}
	}

	/**
	 * Delete file containing the PID. Is used for testing purpose to verify that process is running properly.
	 */
	public static void deletePIDfile(String fileNameFull) {
		try {
			File pidFile = new File(fileNameFull);
			if (pidFile.exists()) {
				pidFile.delete();
				logger.info("Delete PID-file: " + fileNameFull);
			}
		} catch (Exception e) {
			// ignore any error
			e.printStackTrace();
		}
	}
	
	/**
	 * @return path of the current log4j configuration file
	 */
	public static String getPath() throws SCMPValidatorException{
		
		

		Category rootLogger = logger.getParent();
		Enumeration<?> appenders = rootLogger.getAllAppenders();
		FileAppender fileAppender = null;
		while (appenders.hasMoreElements()) {
			Appender appender = (Appender) appenders.nextElement();
			if (appender instanceof FileAppender) {
				fileAppender = (FileAppender) appender;
				break;
			}
		}
		String fileName = fileAppender.getFile();
		String fs = System.getProperty("file.separator");
		int index = fileName.lastIndexOf(fs);
		if (index == -1) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "invalid log directory " + fileName);
		}
		String path = null;
		if (fileName.lastIndexOf(":") == -1) {	
			path = System.getProperty("user.dir") + fs + fileName.substring(0, index); 	// relative path
		} else {
			path = fileName.substring(0, index);					//absolute path
		}
		return path;
	}

	
}
