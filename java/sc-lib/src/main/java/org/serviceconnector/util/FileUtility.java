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
package org.serviceconnector.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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
public final class FileUtility {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(FileUtility.class);

	/**
	 * Instantiates a new file utility.
	 */
	private FileUtility() {
	}

	/**
	 * Exists.
	 * 
	 * @param filename
	 *            the filename
	 * @return true if the given file exists
	 */
	public static boolean exists(String filename) {
		File file = new File(filename);
		return file.exists();
	}

	/**
	 * Checks if is file locked.
	 * 
	 * @param filename
	 *            the filename
	 * @return true, if is file locked
	 */
	public static boolean isFileLocked(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			// Get a file channel for the file
			FileChannel channel;
			try {
				channel = new RandomAccessFile(file, "rw").getChannel();

				// Use the file channel to create a lock on the file.
				// This method blocks until it can retrieve the lock.
				FileLock lock = channel.tryLock();
				if (lock == null) {
					// could not get lock - means some other instance is locking the file
					return true;
				}
				lock.release();
			} catch (Exception e) {
				LOGGER.debug(e);
			}
			return false;
		} else {
			return false;
		}
	}

	/**
	 * Locate.
	 * 
	 * @param resourceName
	 *            the resource name
	 * @return the uRL
	 */
	public static URL locate(String resourceName) {
		URL url = FileUtility.locateFromAbsolutePath(resourceName);
		if (url == null) {
			url = FileUtility.locateFromCurrentClasspath(resourceName);
		}
		return url;
	}

	/**
	 * Locate from absolute path.
	 * 
	 * @param resourceName
	 *            the resource name
	 * @return the uRL
	 */
	public static URL locateFromAbsolutePath(String resourceName) {
		URL url = null;
		// attempt to load from an absolute path
		if (url == null) {
			File file = new File(resourceName);
			if (file.isAbsolute() && file.exists()) { // already absolute?
				try {
					url = toURL(file);
				} catch (MalformedURLException e) {
					url = null;
				}
			}
		}
		return url;
	}

	/**
	 * Locate from current classpath.
	 * 
	 * @param resourceName
	 *            the resource name
	 * @return the uRL
	 */
	public static URL locateFromCurrentClasspath(String resourceName) {
		URL url = null;
		// attempt to load from the context classpath
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null) {
			url = loader.getResource(resourceName);
		}
		return url;
	}

	/**
	 * To url.
	 * 
	 * @param file
	 *            the file
	 * @return the uRL
	 * @throws MalformedURLException
	 *             the malformed url exception
	 */
	static URL toURL(File file) throws MalformedURLException {
		try {
			Method toURI = file.getClass().getMethod("toURI", (Class[]) null);
			Object uri = toURI.invoke(file, (Object[]) null);
			Method toURL = uri.getClass().getMethod("toURL", (Class[]) null);
			URL url = (URL) toURL.invoke(uri, (Object[]) null);

			return url;
		} catch (Exception e) {
			throw new MalformedURLException(e.getMessage());
		}
	}

	/**
	 * Not exists.
	 * 
	 * @param filename
	 *            the filename
	 * @return true if the given file does not exist
	 */
	public static boolean notExists(String filename) {
		return !exists(filename);
	}

	/**
	 * Not exists or unlocked.
	 * 
	 * @param filename
	 *            the filename
	 * @return true if the given file does not exist
	 */
	public static boolean notExistsOrUnlocked(String filename) {
		if (exists(filename) == false || isFileLocked(filename) == false) {
			return true;
		}
		return false;
	}

	/**
	 * @param filename
	 *            to look for
	 * @param nrSeconds
	 *            to wait (check is done in 1 second interval)
	 * @throws Exception
	 *             if the file does not exist after the given time
	 */
	public static void waitExists(String filename, int nrSeconds) throws Exception {
		if (exists(filename)) {
			return;
		}
		for (int i = 0; i < (nrSeconds * 10); i++) {
			if (exists(filename)) {
				return;
			}
			Thread.sleep(100);
		}
		throw new TimeoutException("File:" + filename + " does not exist or unlocked after " + nrSeconds + " seconds timeout.");
	}

	/**
	 * @param filename
	 *            to look for
	 * @param nrSeconds
	 *            to wait (check is done in 1 second interval)
	 * @throws Exception
	 *             if the file does not exist after the given time
	 */
	public static void waitExistsAndLocked(String filename, int nrSeconds) throws Exception {
		if (exists(filename) && isFileLocked(filename)) {
			return;
		}
		for (int i = 0; i < (nrSeconds * 10); i++) {
			if (exists(filename) && isFileLocked(filename)) {
				return;
			}
			Thread.sleep(100);
		}
		throw new TimeoutException("File:" + filename + " does not exist or unlocked after " + nrSeconds + " seconds timeout.");
	}

	/**
	 * @param filename
	 *            to look for
	 * @param nrSeconds
	 *            to wait (check is done in 1 second interval)
	 * @throws Exception
	 *             if the file still exists after the given time
	 */
	public static void waitNotExistsOrUnlocked(String filename, int nrSeconds) throws Exception {
		if (notExistsOrUnlocked(filename)) {
			return;
		}
		for (int i = 0; i < (nrSeconds * 10); i++) {
			if (notExistsOrUnlocked(filename)) {
				return;
			}
			Thread.sleep(100);
		}
		throw new TimeoutException("File=" + filename + " does still exist after " + nrSeconds + " seconds timeout.");
	}

	/**
	 * Create file containing the PID. Is used for testing purpose to verify that process is running properly.
	 * 
	 * @param fileNameFull
	 *            the file name full
	 * @return the lock
	 * @throws Exception
	 *             the exception
	 */
	public static FileLock createPIDfileAndLock(String fileNameFull) throws Exception {
		FileWriter fw = null;
		try {
			String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			long pid = Long.parseLong(processName.split("@")[0]);

			File pidFile = new File(fileNameFull);
			File parent = pidFile.getParentFile();
			// create directory if non existent
			if (parent.exists() == true || parent.mkdirs()) {
				if (pidFile.exists() == false) {
					pidFile.createNewFile();
				}
				fw = new FileWriter(pidFile);
				fw.write("pid: " + pid);
				fw.flush();
				fw.close();
			}
			FileChannel channel = new RandomAccessFile(pidFile, "rw").getChannel();
			FileLock lock = channel.lock();
			LOGGER.log(Level.OFF, "Create PID-file=" + fileNameFull + " PID=" + pid);
			return lock;
		} finally {
			if (fw != null) {
				fw.close();
			}
		}
	}

	/**
	 * Delete file. Catch all possible errors
	 * 
	 * @param fileNameFull
	 *            the file name full
	 */
	public static void deleteFile(String fileNameFull) {
		try {
			File pidFile = new File(fileNameFull);
			if (pidFile.exists()) {
				pidFile.delete();
			}
		} catch (Exception e) {
			LOGGER.debug(e);
			// ignore any error
		}
	}

	/**
	 * Gets the log path.
	 * 
	 * @return directory configured for appender in the current log4j configuration file
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public static String getLogPath() throws SCMPValidatorException {
		Category rootLogger = LOGGER.getParent();
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
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "invalid log directory=" + fileName);
		}
		String path = null;
		if (fileName.lastIndexOf(":") == -1) {
			path = System.getProperty("user.dir") + fs + fileName.substring(0, index); // relative path
		} else {
			path = fileName.substring(0, index); // absolute path
		}
		return path;
	}

	/**
	 * Read file to writer.
	 * 
	 * @param filePath
	 *            the file path
	 * @param writer
	 *            the writer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void readFileToWriter(String filePath, Writer writer) throws IOException {
		FileReader fr = new FileReader(filePath);
		char[] buffer = new char[1 << 16];
		int bytesRead = -1;
		while ((bytesRead = fr.read(buffer)) != -1) {
			writer.write(buffer, 0, bytesRead);
		}
		fr.close();
		return;
	}

}
