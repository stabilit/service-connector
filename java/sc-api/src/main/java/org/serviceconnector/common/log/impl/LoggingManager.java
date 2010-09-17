package org.serviceconnector.common.log.impl;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.serviceconnector.common.log.ILoggingManagerMXBean;


/**
 * The Class LoggingManager. Provides access for controlling logging over JMX console.
 * 
 * @author JTraber
 */
public class LoggingManager implements ILoggingManagerMXBean {

	/** {@inheritDoc} */
	@Override
	public void setConnectionLoggerLevel(String levelValue) {
		Level level = LoggingManager.convertLevelValue(levelValue);
		LogManager.getLogger("ConnectionLogger").setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public String getConnectionLoggerLevel() {
		return LogManager.getLogger("ConnectionLogger").getLevel().toString();
	}

	/**
	 * Convert level value.
	 * 
	 * @param levelValue
	 *            the level value
	 * @return the level
	 */
	public static Level convertLevelValue(String levelValue) {
		Level level = null;

		if ("DEBUG".equals(levelValue)) {
			level = Level.DEBUG;
		} else if ("INFO".equals(levelValue)) {
			level = Level.INFO;
		} else if ("WARN".equals(levelValue)) {
			level = Level.WARN;
		} else if ("ERROR".equals(levelValue)) {
			level = Level.ERROR;
		} else if ("FATAL".equals(levelValue)) {
			level = Level.FATAL;
		} else if ("TRACE".equals(levelValue)) {
			level = Level.TRACE;
		} else if ("OFF".equals(levelValue)) {
			level = Level.OFF;
		} else {
			return Level.INFO;
		}
		return level;
	}
}