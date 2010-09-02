package com.stabilit.scm.common.log.impl;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import com.stabilit.scm.common.log.ILoggingManagerMXBean;

public class LoggingManager implements ILoggingManagerMXBean {

	@Override
	public void setConnectionLoggerLevel(String levelValue) {
		Level level = null;

		if ("DEBUG".equals(levelValue)) {
			level = Level.DEBUG;
		} else if ("INFO".equals(levelValue)) {
			level = Level.INFO;
		} else if ("WARN".equals(levelValue)) {
			level = Level.WARN;
		} else if ("ERROR".equals(levelValue)) {
			level = Level.ERROR;
		} else {
			return;
		}
		LogManager.getLogger("ConnectionLogger").setLevel(level);
	}

	@Override
	public String getConnectionLoggerLevel() {
		return LogManager.getLogger("ConnectionLogger").getLevel().toString();
	}
}
