package com.stabilit.sc.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
