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
	 * Gets the root logger level.
	 *
	 * @return the root logger level
	 */
	public abstract String getRootLoggerLevel();

	/**
	 * Sets the root logger level.
	 *
	 * @param levelValue the new root logger level
	 */
	public abstract void setRootLoggerLevel(String levelValue);

	/**
	 * Sets the connection logger level.
	 *
	 * @param levelValue the new connection logger level
	 */
	public abstract void setConnectionLoggerLevel(String levelValue);

	/**
	 * Gets the connection logger level.
	 *
	 * @return the connection logger level
	 */
	public abstract String getConnectionLoggerLevel();

	/**
	 * Sets the cache logger level.
	 *
	 * @param levelValue the new cache logger level
	 */
	public abstract void setCacheLoggerLevel(String levelValue);

	/**
	 * Gets the cache logger level.
	 *
	 * @return the cache logger level
	 */
	public abstract String getCacheLoggerLevel();

	/**
	 * Sets the message logger level.
	 *
	 * @param levelValue the new message logger level
	 */
	public abstract void setMessageLoggerLevel(String levelValue);

	/**
	 * Gets the message logger level.
	 *
	 * @return the message logger level
	 */
	public abstract String getMessageLoggerLevel();

	/**
	 * Sets the performance logger level.
	 *
	 * @param levelValue the new performance logger level
	 */
	public abstract void setPerformanceLoggerLevel(String levelValue);

	/**
	 * Gets the performance logger level.
	 *
	 * @return the performance logger level
	 */
	public abstract String getPerformanceLoggerLevel();

	/**
	 * Sets the session logger level.
	 *
	 * @param levelValue the new session logger level
	 */
	public abstract void setSessionLoggerLevel(String levelValue);

	/**
	 * Gets the session logger level.
	 *
	 * @return the session logger level
	 */
	public abstract String getSessionLoggerLevel();

	/**
	 * Sets the subscription logger level.
	 *
	 * @param levelValue the new subscription logger level
	 */
	public abstract void setSubscriptionLoggerLevel(String levelValue);

	/**
	 * Gets the subscription logger level.
	 *
	 * @return the subscription logger level
	 */
	public abstract String getSubscriptionLoggerLevel();
}
