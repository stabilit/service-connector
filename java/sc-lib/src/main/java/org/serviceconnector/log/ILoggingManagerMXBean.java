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
package org.serviceconnector.log;

/**
 * The Interface ILoggingManagerMXBean. Access to Loggers over JMX.
 */
public interface ILoggingManagerMXBean {

	/**
	 * Gets the root LOGGER level.
	 * 
	 * @return the root LOGGER level
	 */
	public abstract String getRootLoggerLevel();

	/**
	 * Sets the root LOGGER level.
	 * 
	 * @param levelValue
	 *            the new root LOGGER level
	 */
	public abstract void setRootLoggerLevel(String levelValue);

	/**
	 * Sets the connection LOGGER level.
	 * 
	 * @param levelValue
	 *            the new connection LOGGER level
	 */
	public abstract void setConnectionLoggerLevel(String levelValue);

	/**
	 * Gets the connection LOGGER level.
	 * 
	 * @return the connection LOGGER level
	 */
	public abstract String getConnectionLoggerLevel();

	/**
	 * Sets the cache LOGGER level.
	 * 
	 * @param levelValue
	 *            the new cache LOGGER level
	 */
	public abstract void setCacheLoggerLevel(String levelValue);

	/**
	 * Gets the cache LOGGER level.
	 * 
	 * @return the cache LOGGER level
	 */
	public abstract String getCacheLoggerLevel();

	/**
	 * Sets the message LOGGER level.
	 * 
	 * @param levelValue
	 *            the new message LOGGER level
	 */
	public abstract void setMessageLoggerLevel(String levelValue);

	/**
	 * Gets the message LOGGER level.
	 * 
	 * @return the message LOGGER level
	 */
	public abstract String getMessageLoggerLevel();

	/**
	 * Sets the performance LOGGER level.
	 * 
	 * @param levelValue
	 *            the new performance LOGGER level
	 */
	public abstract void setPerformanceLoggerLevel(String levelValue);

	/**
	 * Gets the performance LOGGER level.
	 * 
	 * @return the performance LOGGER level
	 */
	public abstract String getPerformanceLoggerLevel();

	/**
	 * Sets the session LOGGER level.
	 * 
	 * @param levelValue
	 *            the new session LOGGER level
	 */
	public abstract void setSessionLoggerLevel(String levelValue);

	/**
	 * Gets the session LOGGER level.
	 * 
	 * @return the session LOGGER level
	 */
	public abstract String getSessionLoggerLevel();

	/**
	 * Sets the subscription LOGGER level.
	 * 
	 * @param levelValue
	 *            the new subscription LOGGER level
	 */
	public abstract void setSubscriptionLoggerLevel(String levelValue);

	/**
	 * Gets the subscription LOGGER level.
	 * 
	 * @return the subscription LOGGER level
	 */
	public abstract String getSubscriptionLoggerLevel();
}
