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

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * The Class JMXLoggingManager. Provides access for controlling logging over JMX console.
 *
 * @author JTraber
 */
public class JMXLoggingManager implements ILoggingManagerMXBean {

	/** {@inheritDoc} */
	@Override
	public String getRootLoggerLevel() {
		Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		return rootLogger.getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getConnectionLoggerLevel() {
		return ((Logger) LoggerFactory.getLogger(Loggers.CONNECTION.getValue())).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getCacheLoggerLevel() {
		return ((Logger) LoggerFactory.getLogger(Loggers.CACHE.getValue())).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getMessageLoggerLevel() {
		return ((Logger) LoggerFactory.getLogger(Loggers.MESSAGE.getValue())).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getPerformanceLoggerLevel() {
		return ((Logger) LoggerFactory.getLogger(Loggers.PERFORMANCE.getValue())).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getSessionLoggerLevel() {
		return ((Logger) LoggerFactory.getLogger(Loggers.SESSION.getValue())).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getSubscriptionLoggerLevel() {
		return ((Logger) LoggerFactory.getLogger(Loggers.SUBSCRIPTION.getValue())).getLevel().toString();
	}

	/** {@inheritDoc} */
	@Override
	public void setRootLoggerLevel(String levelValue) {
		Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		Level level = Level.toLevel(levelValue, rootLogger.getLevel());
		rootLogger.setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setConnectionLoggerLevel(String levelValue) {
		Logger connectionLogger = (Logger) LoggerFactory.getLogger(Loggers.CONNECTION.getValue());
		Level level = Level.toLevel(levelValue, connectionLogger.getLevel());
		connectionLogger.setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setCacheLoggerLevel(String levelValue) {
		Logger cacheLogger = (Logger) LoggerFactory.getLogger(Loggers.CACHE.getValue());
		Level level = Level.toLevel(levelValue, cacheLogger.getLevel());
		cacheLogger.setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setMessageLoggerLevel(String levelValue) {
		Logger messageLogger = (Logger) LoggerFactory.getLogger(Loggers.MESSAGE.getValue());
		Level level = Level.toLevel(levelValue, messageLogger.getLevel());
		messageLogger.setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setPerformanceLoggerLevel(String levelValue) {
		Logger performanceLogger = (Logger) LoggerFactory.getLogger(Loggers.PERFORMANCE.getValue());
		Level level = Level.toLevel(levelValue, performanceLogger.getLevel());
		performanceLogger.setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setSessionLoggerLevel(String levelValue) {
		Logger sessionLogger = (Logger) LoggerFactory.getLogger(Loggers.SESSION.getValue());
		Level level = Level.toLevel(levelValue, sessionLogger.getLevel());
		sessionLogger.setLevel(level);
	}

	/** {@inheritDoc} */
	@Override
	public void setSubscriptionLoggerLevel(String levelValue) {
		Logger subscriptionLogger = (Logger) LoggerFactory.getLogger(Loggers.SUBSCRIPTION.getValue());
		Level level = Level.toLevel(levelValue, subscriptionLogger.getLevel());
		subscriptionLogger.setLevel(level);
	}
}
