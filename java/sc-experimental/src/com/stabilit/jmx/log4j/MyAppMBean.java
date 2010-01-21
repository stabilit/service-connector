package com.stabilit.jmx.log4j;

public interface MyAppMBean {
	public void setLoggingLevel(String level);

	public String getLoggingLevel();
}
