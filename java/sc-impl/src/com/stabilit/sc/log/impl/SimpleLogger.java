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
package com.stabilit.sc.log.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.stabilit.sc.log.ILogger;
import com.stabilit.sc.log.ILoggerDecorator;
import com.stabilit.sc.log.Level;
import com.stabilit.sc.util.DateTimeUtility;

/**
 * The Class SimpleLogger. A simple implementation of a logger. Writes files using native java library.
 */
public class SimpleLogger implements ILogger {

	/** The fos. */
	FileOutputStream fos;
	/** The pw. */
	protected PrintWriter pw;
	/** The dir. */
	private String dir;
	/** The file name. */
	private String fileName;
	/** The log file. */
	File logFile;
	/** The date. */
	protected Date date;
	/** The date formatter. */
	private SimpleDateFormat dateFormatter;
	/** The time formatter. */
	private SimpleDateFormat timeFormatter;

	/**
	 * Instantiates a new simple logger. Only visible in package for Factory.
	 */
	SimpleLogger() {
	}

	/**
	 * Instantiates a new simple logger. Not visible outside. Instantiation should be done over new instance methods.
	 * 
	 * @param dir
	 *            the directory
	 * @param fileName
	 *            the file name
	 */
	private SimpleLogger(String dir, String fileName) {
		this.date = Calendar.getInstance().getTime();
		this.dir = dir;
		this.fileName = fileName;
		this.timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateFormat = this.dateFormatter.format(date);
		// Create a directory; all non-existent ancestor directories are
		// automatically created
		File dirFile = new File(this.dir);
		if (dirFile.exists() == false) {
			dirFile.mkdirs();
		}
		String fullPath = this.dir + dateFormat + "-" + fileName;
		logFile = new File(fullPath);
		try {
			fos = new FileOutputStream(fullPath, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		pw = new PrintWriter(new OutputStreamWriter(fos));
	}

	/**
	 * Close and open.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void closeAndOpen() throws IOException {
		try {
			pw.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.date = Calendar.getInstance().getTime();
		String dateFormat = this.dateFormatter.format(date);
		String fullPath = this.dir + dateFormat + "-" + fileName;
		fos = new FileOutputStream(fullPath, true);
		pw = new PrintWriter(new OutputStreamWriter(fos));
	}

	/** {@inheritDoc} */
	@Override
	public void log(Object obj) throws IOException {
		throw new IOException("not supported");
	}

	/** {@inheritDoc} */
	@Override
	public void log(byte[] buffer) throws IOException {
		if (DateTimeUtility.isSameDay(date) == false) {
			// day did change
			closeAndOpen();
		}
		try {
			if (logFile.exists() == false) {
				closeAndOpen();
			}
			fos.write(buffer);
			fos.flush();
		} catch (Exception e) {
			closeAndOpen();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void log(byte[] buffer, int offset, int length) throws IOException {
		if (DateTimeUtility.isSameDay(date) == false) {
			// day did change
			closeAndOpen();
		}
		try {
			if (logFile.exists() == false) {
				closeAndOpen();
			}
			fos.write(buffer, offset, length);
			fos.flush();
		} catch (Exception e) {
			closeAndOpen();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void log(String msg) throws IOException {
		if (DateTimeUtility.isSameDay(date) == false) {
			// day did change
			closeAndOpen();
		}
		try {
			if (logFile.exists() == false) {
				closeAndOpen();
			}
			pw.print(msg);
			pw.flush();
		} catch (Exception e) {
			closeAndOpen();
			pw.println(msg);
			pw.flush();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void log(Throwable t) throws IOException {
		if (DateTimeUtility.isSameDay(date) == false) {
			// day did change
			closeAndOpen();
		}
		try {
			if (logFile.exists() == false) {
				closeAndOpen();
			}
			pw.print(this.getLogHead(Level.EXCEPTION));
			pw.print(t.toString());
			pw.flush();
		} catch (Exception e) {
			closeAndOpen();
			pw.print(this.getLogHead(Level.EXCEPTION));
			pw.print(t.toString());
			pw.flush();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void log(Level level, String msg) throws IOException {
		if (DateTimeUtility.isSameDay(date) == false) {
			// day did change
			closeAndOpen();
		}
		try {
			if (logFile.exists() == false) {
				closeAndOpen();
			}
			pw.print(this.getLogHead(level));
			pw.println(msg);
			pw.flush();
		} catch (Exception e) {
			closeAndOpen();
			pw.print(this.getLogHead(level));
			pw.println(msg);
			pw.flush();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void logError(String msg) throws IOException {
		this.log(Level.ERROR, msg);
	}

	/** {@inheritDoc} */
	@Override
	public void logWarn(String msg) throws IOException {
		this.log(Level.WARN, msg);
	}

	/** {@inheritDoc} */
	@Override
	public void logInfo(String msg) throws IOException {
		this.log(Level.INFO, msg);
	}

	/** {@inheritDoc} */
	@Override
	public void logDebug(String msg) throws IOException {
		this.log(Level.DEBUG, msg);
	}

	/** {@inheritDoc} */
	@Override
	public void logTrace(String msg) throws IOException {
		this.log(Level.TRACE, msg);
	}

	/**
	 * Gets the log head.
	 * 
	 * @param level
	 *            the level
	 * @return the log head
	 */
	public String getLogHead(Level level) {
		// 2009-12-14 20:17:13 - 127.0.0.1 - INFO -->
		Date now = Calendar.getInstance().getTime();
		StringBuilder sb = new StringBuilder();
		sb.append(this.timeFormatter.format(now));
		sb.append(" - 0.0.0.0 ");
		sb.append(level.getLevel());
		sb.append(" --> ");
		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public ILogger newInstance() {
		// careful in use - is always the same instance
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ILogger newInstance(ILoggerDecorator loggerDecorator) {
		// we need a new instance in this case
		return new SimpleLogger(loggerDecorator.getLogDir(), loggerDecorator.getLogFileName());
	}
}