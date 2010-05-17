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

/**
 * A factory for creating Logger objects. Provides access to the concrete Logger instances.
 * 
 * @author JTraber
 */
public final class LoggerFactory extends Factory {

	/** The logger factory. */
	private static LoggerFactory loggerFactory = new LoggerFactory();

	/**
	 * Gets the current logger factory.
	 * 
	 * @return the current logger factory
	 */
	public static LoggerFactory getCurrentLoggerFactory() {
		return loggerFactory;
	}

	/**
	 * Instantiates a new logger factory.
	 */
	private LoggerFactory() {
		ILogger logger;
		try {
			// Connection logger
			logger = new ConnectionLogger(IConstants.LOG_DIR, IConstants.CONNECTION_LOG_FILE_NAME);
			this.add(ConnectionLogger.class, logger);
			// Exception logger
			logger = new ExceptionLogger(IConstants.LOG_DIR, IConstants.EXCEPTION_LOG_FILE_NAME);
			this.add(ExceptionLogger.class, logger);
			// Performance logger
			logger = new PerformanceLogger(IConstants.LOG_DIR, IConstants.PERFORMANCE_LOG_FILE_NAME);
			this.add(PerformanceLogger.class, logger);
			// Runtime logger
			logger = new RuntimeLogger(IConstants.LOG_DIR, IConstants.RUNTIME_LOG_FILE_NAME);
			this.add(RuntimeLogger.class, logger);
			// General logger
			logger = new GeneralLogger(IConstants.LOG_DIR, IConstants.GENERAL_LOG_FILE_NAME);
			this.add(GeneralLogger.class, logger);
			this.add(DEFAULT, logger);
			// SCMP logger
			logger = new SCMPLogger(IConstants.LOG_DIR, IConstants.SCMP_LOG_FILE_NAME);
			this.add(SCMPLogger.class, logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public ILogger newInstance() {
		return newInstance(DEFAULT);
	}

	/** {@inheritDoc} */
	@Override
	public ILogger newInstance(Object key) {
		ILogger logger = (ILogger) super.newInstance(key);
		return logger;
	}
}
