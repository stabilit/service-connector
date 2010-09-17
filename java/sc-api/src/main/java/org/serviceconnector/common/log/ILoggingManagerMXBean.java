package org.serviceconnector.common.log;

public interface ILoggingManagerMXBean {

	/**
	 * Sets the connection logger level.
	 * 
	 * @param level
	 *            the new connection logger level
	 */
	public abstract void setConnectionLoggerLevel(String level);

	/**
	 * Gets the connection logger level.
	 * 
	 * @return the connection logger level
	 */
	public abstract String getConnectionLoggerLevel();
}
