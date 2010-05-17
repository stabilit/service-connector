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
package com.stabilit.sc.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.stabilit.sc.util.DateTimeUtility;

/**
 * The Class SimpleLogger. A simple implementation of a logger.
 */
public abstract class SimpleLogger implements ILogger {

	/** The fos. */
	private FileOutputStream fos;
	/** The pw. */
	protected PrintWriter pw;
	/** The dir. */
	private String dir;
	/** The file name. */
	private String fileName;
	/** The log file. */
	private File logFile;
	/** The date. */
	protected Date date;
	/** The date formatter. */
	private SimpleDateFormat dateFormatter;
	/** The time formatter. */
	private SimpleDateFormat timeFormatter;

	/**
	 * Instantiates a new simple logger.
	 * 
	 * @param dir
	 *            the dir
	 * @param fileName
	 *            the file name
	 * @throws Exception
	 *             the exception
	 */
	public SimpleLogger(String dir, String fileName) throws Exception {
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
		fos = new FileOutputStream(fullPath, true);
		pw = new PrintWriter(new OutputStreamWriter(fos));
	}

	/**
	 * Close and open.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void closeAndOpen() throws IOException {
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

	/**
	 * Log error.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logError(String msg) throws IOException {
		this.log(Level.ERROR, msg);
	}

	/**
	 * Log warn.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logWarn(String msg) throws IOException {
		this.log(Level.WARN, msg);
	}

	/**
	 * Log info.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logInfo(String msg) throws IOException {
		this.log(Level.INFO, msg);
	}

	/**
	 * Log debug.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void logDebug(String msg) throws IOException {
		this.log(Level.DEBUG, msg);
	}

	/**
	 * Log trace.
	 * 
	 * @param msg
	 *            the msg
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
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
}