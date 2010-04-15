package com.stabilit.sc.common.log;

import com.stabilit.sc.common.factory.Factory;

public class LoggerFactory extends Factory {
	private static LoggerFactory loggerFactory = new LoggerFactory();

	private LoggerFactory() {
		ILogger logger;
		try {
			logger = new SCMPLogger("", "scmp.log");
			this.add(SCMPLogger.class, logger);
			logger = new ConnectionLogger("", "con.log");
			this.add(ConnectionLogger.class, logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ILogger getLogger() {
		return (ILogger) this.getInstance(SimpleLogger.class);
	}

	public static LoggerFactory getLoggerFactory() {
		if (loggerFactory == null) {
			loggerFactory = new LoggerFactory();
		}
		return loggerFactory;
	}

	public ILogger getLogger(Object key) {
		return (ILogger) this.factoryMap.get(key);
	}

	public ILogger getConnectionLogger() {
		return (ILogger) this.factoryMap.get(ConnectionLogger.class);
	}

}
