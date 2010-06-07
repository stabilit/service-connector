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
package com.stabilit.scm.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The Class DateTimeUtility. Provides basic date time operations.
 */
public final class DateTimeUtility {

	/** The Constant SDF. */
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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
