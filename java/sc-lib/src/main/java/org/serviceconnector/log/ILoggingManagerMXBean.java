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

	/**
	 * Sets the cache logger level.
	 * 
	 * @param level
	 *            the new cache logger level
	 */
	public abstract void setCacheLoggerLevel(String level);

	/**
	 * Gets the cache logger level.
	 * 
	 * @return the cache logger level
	 */
	public abstract String getCacheLoggerLevel();

	/**
	 * Sets the message logger level.
	 * 
	 * @param level
	 *            the new message logger level
	 */
	public abstract void setMessageLoggerLevel(String level);

	/**
	 * Gets the message logger level.
	 * 
	 * @return the message logger level
	 */
	public abstract String getMessageLoggerLevel();

	/**
	 * Sets the performance logger level.
	 * 
	 * @param level
	 *            the new performance logger level
	 */
	public abstract void setPerformanceLoggerLevel(String level);

	/**
	 * Gets the performance logger level.
	 * 
	 * @return the performance logger level
	 */
	public abstract String getPerformanceLoggerLevel();

	/**
	 * Sets the session logger level.
	 * 
	 * @param level
	 *            the new session logger level
	 */
	public abstract void setSessionLoggerLevel(String level);

	/**
	 * Gets the session logger level.
	 * 
	 * @return the session logger level
	 */
	public abstract String getSessionLoggerLevel();

	/**
	 * Sets the subscription logger level.
	 * 
	 * @param level
	 *            the new subscription logger level
	 */
	public abstract void setSubscriptionLoggerLevel(String level);

	/**
	 * Gets the subscription logger level.
	 * 
	 * @return the subscription logger level
	 */
	public abstract String getSubscriptionLoggerLevel();
}
