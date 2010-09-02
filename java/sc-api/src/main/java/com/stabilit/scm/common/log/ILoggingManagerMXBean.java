package com.stabilit.scm.common.log;


public interface ILoggingManagerMXBean {

	public abstract void setConnectionLoggerLevel(String level);
	public abstract String getConnectionLoggerLevel();
}
