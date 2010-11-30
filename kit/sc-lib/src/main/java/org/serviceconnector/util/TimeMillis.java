package org.serviceconnector.util;

public enum TimeMillis {
	SECOND(1000), MINUTE(1000 * 60), HOUR(1000 * 60 * 60);

	private long millis;

	public long getMillis() {
		return millis;
	}

	private TimeMillis(long millis) {
		this.millis = millis;
	}

}