/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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

public abstract class SimpleLogger implements ILogger {
	private FileOutputStream fos;
	protected PrintWriter pw;
	private String dir;
	private String fileName;
	private File logFile;
	protected Date date;
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat timeFormatter;

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

	protected void closeAndOpen() throws IOException {
		try {
			pw.close();
			fos.close();
		} catch (Exception e) {
		}
		this.date = Calendar.getInstance().getTime();
		String dateFormat = this.dateFormatter.format(date);
		String fullPath = this.dir + dateFormat + "-" + fileName;
		fos = new FileOutputStream(fullPath, true);
		pw = new PrintWriter(new OutputStreamWriter(fos));
	}

	@Override
	public void log(Object obj) throws IOException {
		throw new IOException("not supported");
	}

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

	public void logError(String msg) throws IOException {
		this.log(Level.ERROR, msg);
	}

	public void logWarn(String msg) throws IOException {
		this.log(Level.WARN, msg);
	}

	public void logInfo(String msg) throws IOException {
		this.log(Level.INFO, msg);
	}

	public void logDebug(String msg) throws IOException {
		this.log(Level.DEBUG, msg);
	}

	public void logTrace(String msg) throws IOException {
		this.log(Level.TRACE, msg);
	}

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