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

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The Class JMXLoggingManager. Provides access for controlling logging over JMX console.
 * 
 * @author JTraber
 */
public class JMXLoggingManager implements ILoggingManagerMXBean {

	/** {@inheritDoc} */
	@Override
	public void setConnectionLoggerLevel(String levelValue) {
		Logger connectionLogger = LogManager.getLogger(Loggers.CONNECTION.getValue());
		Level level = Level.toLevel(levelValue, connectionLogger.getLevel());
		LogManager.getLogger(Loggers.CONNECTION.getValue()).setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public String getConnectionLoggerLevel() {
		return LogManager.getLogger(Loggers.CONNECTION.getValue()).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getCacheLoggerLevel() {
		return LogManager.getLogger(Loggers.CACHE.getValue()).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getMessageLoggerLevel() {
		return LogManager.getLogger(Loggers.MESSAGE.getValue()).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getPerformanceLoggerLevel() {
		return LogManager.getLogger(Loggers.PERFORMANCE.getValue()).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getSessionLoggerLevel() {
		return LogManager.getLogger(Loggers.SESSION.getValue()).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getSubscriptionLoggerLevel() {
		return LogManager.getLogger(Loggers.SUBSCRIPTION.getValue()).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public void setCacheLoggerLevel(String levelValue) {
		Logger cacheLogger = LogManager.getLogger(Loggers.CACHE.getValue());
		Level level = Level.toLevel(levelValue, cacheLogger.getLevel());
		LogManager.getLogger(Loggers.CACHE.getValue()).setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setMessageLoggerLevel(String levelValue) {
		Logger messageLogger = LogManager.getLogger(Loggers.MESSAGE.getValue());
		Level level = Level.toLevel(levelValue, messageLogger.getLevel());
		LogManager.getLogger(Loggers.MESSAGE.getValue()).setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setPerformanceLoggerLevel(String levelValue) {
		Logger performanceLogger = LogManager.getLogger(Loggers.PERFORMANCE.getValue());
		Level level = Level.toLevel(levelValue, performanceLogger.getLevel());
		LogManager.getLogger(Loggers.PERFORMANCE.getValue()).setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setSessionLoggerLevel(String levelValue) {
		Logger sessionLogger = LogManager.getLogger(Loggers.SESSION.getValue());
		Level level = Level.toLevel(levelValue, sessionLogger.getLevel());
		LogManager.getLogger(Loggers.SESSION.getValue()).setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setSubscriptionLoggerLevel(String levelValue) {
		Logger subscriptionLogger = LogManager.getLogger(Loggers.SUBSCRIPTION.getValue());
		Level level = Level.toLevel(levelValue, subscriptionLogger.getLevel());
		LogManager.getLogger(Loggers.SUBSCRIPTION.getValue()).setLevel(level);
	}
}