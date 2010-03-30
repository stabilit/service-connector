package com.stabilit.sc.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeUtility {
	
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public static String getCurrentTimeZoneMillis() {
		long timeInMillis = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		java.util.Date date = cal.getTime();

		synchronized (SDF) {
			return SDF.format(date);
		}
	}
}
