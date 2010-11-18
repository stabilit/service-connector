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
import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

public class PIDFile {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PIDFile.class);
	
	/**
	 * Create file containing the PID of the SC process.
	 * Is used for testing purpose to verify that SC is running properly.
	 */
	public static void create() throws Exception {
		String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		long pid = Long.parseLong(processName.split("@")[0]);
		FileWriter fw = null;
		try {
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
			String path = fileName.substring(0, fileName.lastIndexOf("/"));

			File pidFile = new File(path + Constants.PID_FILE_NAME);
			fw = new FileWriter(pidFile);
			fw.write("pid: " + pid);
			fw.flush();
			fw.close();
			logger.log(Level.OFF, "Create PID-file: " + path + Constants.PID_FILE_NAME);
		}
		finally {
			if (fw != null) {
				fw.close();
			}
		}
	}

	/**
	 * Delete file containing the PID of the SC process.
	 * Is used for testing purpose to verify that SC is running properly.
	 */
	public static void delete() {
		try {
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
			String path = fileName.substring(0, fileName.lastIndexOf("/"));
		
			File pidFile = new File(path + Constants.PID_FILE_NAME);
			if (pidFile.exists()) {
				pidFile.delete();
				logger.log(Level.OFF, "Delete PID-file: " + path + Constants.PID_FILE_NAME);
			}
		}
		catch (Exception ex) {
			// ignore any error
		}
	}

}
