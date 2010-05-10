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
package com.stabilit.sc.log.impl;

import com.stabilit.sc.config.IConstants;
import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.log.ILogger;
import com.stabilit.sc.log.SimpleLogger;

/**
 * A factory for creating Logger objects. Provides access to the concrete Logger instances. TODO is not in use at
 * this time. Needs to be done! (JOT)
 * 
 * @author JTraber
 */
public class LoggerFactory extends Factory {

	/** The logger factory. */
	private static LoggerFactory loggerFactory = new LoggerFactory();

	/**
	 * Instantiates a new logger factory.
	 */
	private LoggerFactory() {
		ILogger logger;
		try {
			logger = new SCMPLogger(IConstants.LOG_DIR, IConstants.SCMP_LOG_FILE_NAME);
			this.add(SCMPLogger.class, logger);
			logger = new ConnectionLogger(IConstants.LOG_DIR, IConstants.CONNECTION_LOG_FILE_NAME);
			this.add(ConnectionLogger.class, logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the simple logger.
	 * 
	 * @return the simple logger
	 */
	public ILogger getLogger() {
		return (ILogger) this.getInstance(SimpleLogger.class);
	}

	/**
	 * Gets the logger factory.
	 * 
	 * @return the logger factory
	 */
	public static LoggerFactory getLoggerFactory() {
		if (loggerFactory == null) {
			loggerFactory = new LoggerFactory();
		}
		return loggerFactory;
	}

	/**
	 * Gets the logger by key.
	 * 
	 * @param key
	 *            the key
	 * @return the logger
	 */
	public ILogger getLogger(Object key) {
		return (ILogger) this.factoryMap.get(key);
	}

	/**
	 * Gets the connection logger.
	 * 
	 * @return the connection logger
	 */
	public ILogger getConnectionLogger() {
		return (ILogger) this.factoryMap.get(ConnectionLogger.class);
	}
}
