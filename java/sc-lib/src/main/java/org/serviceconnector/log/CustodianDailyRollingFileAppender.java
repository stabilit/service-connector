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
package org.serviceconnector.log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

/**
 * CustodianDailyRollingFileAppender is based on {@link org.apache.log4j.appender.DailyRollingFileAppender} so most of the
 * configuration options can be taken from the documentation on that class. Adapted from the Apache Log4j DailyRollingFileAppender
 * to extend the functionality of the existing class so that the user can
 * limit the number of log backups on disk. Base coding done by Ryan Kimber.
 * 
 * @author Ryan Kimber, Joël Traber
 */
public class CustodianDailyRollingFileAppender extends FileAppender {
	// The code assumes that the following constants are in a increasing sequence.
	/** The Constant TOP_OF_TROUBLE. */
	static final int TOP_OF_TROUBLE = -1;
	/** The Constant TOP_OF_MINUTE. */
	static final int TOP_OF_MINUTE = 0;
	/** The Constant TOP_OF_HOUR. */
	static final int TOP_OF_HOUR = 1;
	/** The Constant HALF_DAY. */
	static final int HALF_DAY = 2;
	/** The Constant TOP_OF_DAY. */
	static final int TOP_OF_DAY = 3;
	/** The Constant TOP_OF_WEEK. */
	static final int TOP_OF_WEEK = 4;
	/** The Constant TOP_OF_MONTH. */
	static final int TOP_OF_MONTH = 5;

	/**
	 * The date pattern. By default, the pattern is set to "'.'yyyy-MM-dd"
	 * meaning daily rollover.
	 */
	private String datePattern = "'.'yyyy-MM-dd";
	/** The max number of days, default = 0 means deactive file deletion. */
	private int maxNumberOfDays = 0;
	/**
	 * The log file will be renamed to the value of the scheduledFilename variable when the next interval is entered. For example,
	 * if the rollover period is one hour, the log file will be renamed to the value of "scheduledFilename" at the beginning of the
	 * next hour. The precise time when a rollover occurs depends on logging activity.
	 */
	private String scheduledFilename;
	/**
	 * The next time we estimate a rollover should occur.
	 */
	private long nextCheck = System.currentTimeMillis() - 1;
	/** now. */
	private Date now = new Date();
	/** The sdf. */
	private SimpleDateFormat sdf;
	/** The rc. */
	private RollingCalendar rc = new RollingCalendar();
	/** The Constant gmtTimeZone. */
	private static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
	/** The timestamp in file name. */
	private boolean timestampInFileName = false;

	/**
	 * The default constructor does nothing.
	 */
	public CustodianDailyRollingFileAppender() {
	}

	/**
	 * Instantiate a CustodianDailyRollingFileAppender and open the file designated by filename. The opened filename will become the
	 * output destination for this appender.
	 * 
	 * @param layout
	 *            the layout
	 * @param filename
	 *            the filename
	 * @param datePattern
	 *            the date pattern
	 * @param maxNumberOfDays
	 *            the max number of days
	 * @param TimestampInFileName
	 *            the timestamp in file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public CustodianDailyRollingFileAppender(Layout layout, String filename, String datePattern, int maxNumberOfDays,
			boolean TimestampInFileName) throws IOException {
		super(layout, filename, true);
		this.datePattern = datePattern;
		this.maxNumberOfDays = maxNumberOfDays;
		this.timestampInFileName = TimestampInFileName;
		activateOptions();
	}

	/**
	 * The DatePattern takes a string in the same format as expected by {@link SimpleDateFormat}. This options determines the
	 * rollover schedule.
	 * 
	 * @param pattern
	 *            the new date pattern
	 */
	public void setDatePattern(String pattern) {
		datePattern = pattern;
	}

	/**
	 * Returns the value of the DatePattern option.
	 * 
	 * @return the date pattern
	 */
	public String getDatePattern() {
		return datePattern;
	}

	@Override
	public void activateOptions() {
		if (datePattern != null && fileName != null) {
			now.setTime(System.currentTimeMillis());
			sdf = new SimpleDateFormat(datePattern);
			int type = computeCheckPeriod();
			printPeriodicity(type);
			rc.setType(type);
			try {
				if (timestampInFileName == true) {
					// adding timestamp to file name
					fileName = fileName.replace(".log", "_" + new Date().getTime() + ".log");
				}
				setFile(fileName, fileAppend, bufferedIO, bufferSize);
			} catch (java.io.IOException e) {
				errorHandler.error("setFile(" + fileName + "," + fileAppend + ") call failed.", e, ErrorCode.FILE_OPEN_FAILURE);
			}
			File file = new File(fileName);
			scheduledFilename = fileName + sdf.format(new Date(file.lastModified()));
		} else {
			LogLog.error("Either File or DatePattern options are not set for appender [" + name + "].");
		}
	}

	/**
	 * Prints the periodicity.
	 * 
	 * @param type
	 *            the type
	 */
	void printPeriodicity(int type) {
		switch (type) {
		case TOP_OF_MINUTE:
			LogLog.debug("Appender [" + name + "] to be rolled every minute.");
			break;
		case TOP_OF_HOUR:
			LogLog.debug("Appender [" + name + "] to be rolled on top of every hour.");
			break;
		case HALF_DAY:
			LogLog.debug("Appender [" + name + "] to be rolled at midday and midnight.");
			break;
		case TOP_OF_DAY:
			LogLog.debug("Appender [" + name + "] to be rolled at midnight.");
			break;
		case TOP_OF_WEEK:
			LogLog.debug("Appender [" + name + "] to be rolled at start of week.");
			break;
		case TOP_OF_MONTH:
			LogLog.debug("Appender [" + name + "] to be rolled at start of every month.");
			break;
		default:
			LogLog.warn("Unknown periodicity for appender [" + name + "].");
		}
	}

	/**
	 * Compute check period.
	 * This method computes the roll over period by looping over the periods, starting with the shortest, and stopping when the r0
	 * is different from from r1, where r0 is the epoch formatted according the datePattern (supplied by the user) and r1 is the
	 * epoch+nextMillis(i) formatted according to datePattern. All date formatting is done in GMT and not local format because the
	 * test logic is based on comparisons relative to 1970-01-01 00:00:00 GMT (the epoch).
	 * 
	 * @return the int
	 */
	private int computeCheckPeriod() {
		RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.ENGLISH);
		// set sate to 1970-01-01 00:00:00 GMT
		Date epoch = new Date(0);
		if (datePattern != null) {
			for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
				simpleDateFormat.setTimeZone(gmtTimeZone); // do all date
				// formatting in GMT
				String r0 = simpleDateFormat.format(epoch);
				rollingCalendar.setType(i);
				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
				String r1 = simpleDateFormat.format(next);
				if (r0 != null && r1 != null && !r0.equals(r1)) {
					return i;
				}
			}
		}
		return TOP_OF_TROUBLE; // Deliberately head for trouble...
	}

	/**
	 * Rollover the current file to a new file.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	void rollOver() throws IOException {

		/* Compute filename, but only if datePattern is specified */
		if (datePattern == null) {
			errorHandler.error("Missing DatePattern option in rollOver().");
			return;
		}

		String datedFilename = fileName + sdf.format(now);
		// It is too early to roll over because we are still within the bounds of the current interval. Rollover will occur once the
		// next interval is reached.
		if (scheduledFilename.equals(datedFilename)) {
			return;
		}

		// close current file, and rename it to datedFilename
		this.closeFile();

		File target = new File(scheduledFilename);
		if (target.exists()) {
			target.delete();
		}

		File file = new File(fileName);
		boolean result = file.renameTo(target);
		if (result) {
			LogLog.debug(fileName + " -> " + scheduledFilename);
		} else {
			LogLog.error("Failed to rename [" + fileName + "] to [" + scheduledFilename + "].");
		}

		try {
			// This will also close the file. This is OK since multiple close operations are safe.
			this.setFile(fileName, false, this.bufferedIO, this.bufferSize);
		} catch (IOException e) {
			errorHandler.error("setFile(" + fileName + ", false) call failed.");
		}
		scheduledFilename = datedFilename;
	}

	/**
	 * This method differentiates DailyRollingFileAppender from its super class.
	 * Before actually logging, this method will check whether it is time to do a rollover. If it is, it will schedule the next
	 * rollover time and then rollover.
	 * 
	 * @param event
	 *            the event
	 */
	protected void subAppend(LoggingEvent event) {
		long n = System.currentTimeMillis();
		if (n >= nextCheck) {
			now.setTime(n);
			nextCheck = rc.getNextCheckMillis(now);
			try {
				cleanupAndRollOver();
			} catch (IOException ioe) {
				LogLog.error("cleanupAndRollover() failed.", ioe);
			}
		}
		super.subAppend(event);
	}

	/**
	 * Gets the max number of days.
	 * 
	 * @return the max number of days
	 */
	public int getMaxNumberOfDays() {
		return maxNumberOfDays;
	}

	/**
	 * Sets the max number of days.
	 * 
	 * @param maxNumberOfDays
	 *            the new max number of days
	 */
	public void setMaxNumberOfDays(int maxNumberOfDays) {
		this.maxNumberOfDays = maxNumberOfDays;
	}

	/**
	 * Checks if is timestamp in file name.
	 * 
	 * @return true, if is timestamp in file name
	 */
	public boolean isTimestampInFileName() {
		return timestampInFileName;
	}

	/**
	 * Sets the timestamp in file name.
	 * 
	 * @param timestampInFileName
	 *            the new timestamp in file name
	 */
	public void setTimestampInFileName(boolean timestampInFileName) {
		this.timestampInFileName = timestampInFileName;
	}

	/**
	 * Cleanup and roll over.
	 * This method checks to see if we're exceeding the number of log backups that we are supposed to keep, and if so, deletes the
	 * offending files. It then delegates to the rollover method to rollover to a new file if required.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void cleanupAndRollOver() throws IOException {
		// Check to see if there are already 5 files
		File file = new File(fileName);
		Calendar cal = Calendar.getInstance();
		if (this.maxNumberOfDays == 0) {
			// ignore file deletion when maxDays == 0
			this.rollOver();
			return;
		}
		cal.add(Calendar.DATE, -this.maxNumberOfDays);
		Date cutoffDate = cal.getTime();
		if (file.getParentFile().exists()) {
			File[] files = file.getParentFile().listFiles(new StartsWithFileFilter(file.getName(), false));
			int nameLength = file.getName().length();
			for (int i = 0; i < files.length; i++) {
				String datePart = null;
				try {
					datePart = files[i].getName().substring(nameLength);
					Date date = sdf.parse(datePart);
					if (date.before(cutoffDate)) {
						files[i].delete();
					}
				} catch (Exception pe) {
					// This isn't a file we should touch (it isn't named correctly)
				}
			}
		}
		this.rollOver();
	}

	/**
	 * The Class StartsWithFileFilter.
	 */
	private class StartsWithFileFilter implements FileFilter {

		/** The starts with. */
		private String startsWith;
		/** The include directories. */
		private boolean inclDirs = false;

		/**
		 * Instantiates a new starts with file filter.
		 * 
		 * @param startsWith
		 *            the starts with
		 * @param includeDirectories
		 *            the include directories
		 */
		public StartsWithFileFilter(String startsWith, boolean includeDirectories) {
			this.startsWith = startsWith.toUpperCase();
			inclDirs = includeDirectories;
		}

		@Override
		public boolean accept(File pathname) {
			if (!inclDirs && pathname.isDirectory()) {
				return false;
			} else
				return pathname.getName().toUpperCase().startsWith(startsWith);
		}
	}

	/**
	 * RollingCalendar is a helper class to DailyRollingFileAppender. Given a
	 * periodicity type and the current time, it computes the start of the next
	 * interval.
	 */
	private class RollingCalendar extends GregorianCalendar {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -3560331770601814177L;
		/** The type. */
		private int type = CustodianDailyRollingFileAppender.TOP_OF_TROUBLE;

		/**
		 * Instantiates a new rolling calendar.
		 */
		public RollingCalendar() {
			super();
		}

		/**
		 * Instantiates a new rolling calendar.
		 * 
		 * @param tz
		 *            the tz
		 * @param locale
		 *            the locale
		 */
		public RollingCalendar(TimeZone tz, Locale locale) {
			super(tz, locale);
		}

		/**
		 * Sets the type.
		 * 
		 * @param type
		 *            the new type
		 */
		public void setType(int type) {
			this.type = type;
		}

		/**
		 * Gets the next check millis.
		 * 
		 * @param now
		 *            the now
		 * @return the next check millis
		 */
		public long getNextCheckMillis(Date now) {
			return getNextCheckDate(now).getTime();
		}

		/**
		 * Gets the next check date.
		 * 
		 * @param now
		 *            the now
		 * @return the next check date
		 */
		public Date getNextCheckDate(Date now) {
			this.setTime(now);

			switch (type) {
			case CustodianDailyRollingFileAppender.TOP_OF_MINUTE:
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.MINUTE, 1);
				break;
			case CustodianDailyRollingFileAppender.TOP_OF_HOUR:
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.HOUR_OF_DAY, 1);
				break;
			case CustodianDailyRollingFileAppender.HALF_DAY:
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				int hour = get(Calendar.HOUR_OF_DAY);
				if (hour < 12) {
					this.set(Calendar.HOUR_OF_DAY, 12);
				} else {
					this.set(Calendar.HOUR_OF_DAY, 0);
					this.add(Calendar.DAY_OF_MONTH, 1);
				}
				break;
			case CustodianDailyRollingFileAppender.TOP_OF_DAY:
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.DATE, 1);
				break;
			case CustodianDailyRollingFileAppender.TOP_OF_WEEK:
				this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.WEEK_OF_YEAR, 1);
				break;
			case CustodianDailyRollingFileAppender.TOP_OF_MONTH:
				this.set(Calendar.DATE, 1);
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.MONTH, 1);
				break;
			default:
				throw new IllegalStateException("Unknown periodicity type.");
			}
			return getTime();
		}
	}
}
