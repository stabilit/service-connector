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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * The Class DateTimeUtility. Provides basic date time operations.
 */
public final class DateTimeUtility {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(DateTimeUtility.class);

	/** The Constant SDF. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	/**
	 * Instantiates a new date time utility.
	 */
	private DateTimeUtility() {
	}

	/**
	 * Gets the current time zone millis.
	 * 
	 * @return the current time zone millis
	 */
	public static String getCurrentTimeZoneMillis() {
		long timeInMillis = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		java.util.Date date = cal.getTime();

		synchronized (SDF) { // SDF is not thread safe
			return SDF.format(date);
		}
	}

	/**
	 * Gets the current time.
	 *
	 * @return the current time
	 */
	public static Date getCurrentTime() {
		Calendar cal = Calendar.getInstance();
		java.util.Date date = cal.getTime();
		return date;
	}

	/**
	 * Gets the current time (ms).
	 *
	 * @return the current time (ms)
	 */
	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * Gets the time as string.
	 * 
	 * @param date
	 *            the date
	 * @return the time as string
	 */
	public static String getTimeAsString(Date date) {
		synchronized (SDF) { // SDF is not thread safe
			return SDF.format(date);
		}
	}

	/**
	 * Gets the increment time in millis.
	 * 
	 * @param date
	 *            the date
	 * @param inc
	 *            the inc
	 * @return the increment time in millis
	 */
	public static Date getIncrementTimeInMillis(Date date, long inc) {
		long time = date.getTime();
		time += inc;
		return new Date(time);
	}

	/**
	 * Parses the date string.
	 * 
	 * @param dateString
	 *            the date string
	 * @return the date
	 * @throws ParseException
	 *             the parse exception
	 */
	public static Date parseDateString(String dateString) throws ParseException {
		synchronized (SDF) {
			return SDF.parse(dateString);
		}
	}

	/**
	 * Checks if is same day.
	 * 
	 * @param date
	 *            the date
	 * @return true, if is same day
	 */
	@SuppressWarnings("deprecation")
	public static boolean isSameDay(Date date) {
		Date now = Calendar.getInstance().getTime();
		int nowDay = now.getDate();
		int nowMonth = now.getMonth();
		int nowYear = now.getYear();
		int cDay = date.getDate();
		int cMonth = date.getMonth();
		int cYear = date.getYear();
		if (cYear != nowYear) {
			return false;
		}
		if (cMonth != nowMonth) {
			return false;
		}
		if (cDay != nowDay) {
			return false;
		}
		return true;
	}
}
