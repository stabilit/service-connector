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
package com.stabilit.scm.log.impl;

import com.stabilit.scm.factory.Factory;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.log.ILogger;
import com.stabilit.scm.log.ILoggerDecorator;

/**
 * A factory for creating logger objects. Provides access to the concrete logger instances and logger decorator's.
 * 
 * @author JTraber
 */
public final class LoggerFactory extends Factory {

	/** The Constant LOG4J_KEY. */
	private static final String LOG4J_KEY = "log4j";
	/** The Constant SIMPLE_KEY. */
	private static final String SIMPLE_KEY = "simple";
	/** The Constant DEF_LOGGER, default to use if nothing set. */
	private static final String DEF_LOGGER = LoggerFactory.SIMPLE_KEY;
	/** The logger factory. */
	private static LoggerFactory loggerFactory = new LoggerFactory();
	/** The init, indicates if factory already got initialized. */
	private static boolean init = false;

	/**
	 * Instantiates a new logger factory. Not visible outside.
	 */
	private LoggerFactory() {
	}

	/**
	 * Gets the current logger factory.
	 * 
	 * @param key
	 *            the key identifies concrete logger implementation
	 * @return the current logger factory
	 */
	public static LoggerFactory getCurrentLoggerFactory(String key) {
		if (LoggerFactory.init) {
			return loggerFactory;
		}
		loggerFactory.init(key);
		return loggerFactory;
	}

	/**
	 * Initialize the factory.
	 * 
	 * @param key the key
	 */
	private void init(String key) {
		if (key == null) {
			key = DEF_LOGGER;
		}
		LoggerFactory.init = true;
		ILoggerDecorator loggerDecorator;
		ILogger logger;
		try {
			// simple logger
			logger = new SimpleLogger();
			this.add(SIMPLE_KEY, logger);
			// log4j logger
			logger = new Log4jLogger();
			this.add(LOG4J_KEY, logger);

			// Connection logger
			loggerDecorator = new ConnectionLogger((ILogger) this.getInstance(key));
			this.add(ConnectionLogger.class, loggerDecorator);
			// Exception logger
			loggerDecorator = new ExceptionLogger((ILogger) this.getInstance(key));
			this.add(ExceptionLogger.class, loggerDecorator);
			// Performance logger
			loggerDecorator = new PerformanceLogger((ILogger) this.getInstance(key));
			this.add(PerformanceLogger.class, loggerDecorator);
			// Runtime logger
			loggerDecorator = new RuntimeLogger((ILogger) this.getInstance(key));
			this.add(RuntimeLogger.class, loggerDecorator);
			// Session logger
			loggerDecorator = new SessionLogger((ILogger) this.getInstance(key));
			this.add(SessionLogger.class, loggerDecorator);
			// General logger
			loggerDecorator = new GeneralLogger((ILogger) this.getInstance(key));
			this.add(GeneralLogger.class, loggerDecorator);
			this.add(DEFAULT, loggerDecorator);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return newInstance(DEF_LOGGER);
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance(Object key) {
		IFactoryable logger = super.newInstance(key);
		return logger;
	}
}
