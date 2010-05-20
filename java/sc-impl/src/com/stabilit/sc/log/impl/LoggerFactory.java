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

import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.log.ILogger;
import com.stabilit.sc.log.ILoggerDecorator;

/**
 * A factory for creating Logger objects. Provides access to the concrete Logger instances.
 * 
 * @author JTraber
 */
public final class LoggerFactory extends Factory {

	/** The logger factory. */
	private static LoggerFactory loggerFactory = new LoggerFactory();
	private static boolean init = false;

	private static final String LOG4J_KEY = "log4j";
	private static final String SIMPLE_KEY = "simple";
	private static final String DEF_LOGGER = LoggerFactory.SIMPLE_KEY;

	/**
	 * Instantiates a new logger factory.
	 */
	private LoggerFactory() {
	}

	/**
	 * Gets the current logger factory.
	 * 
	 * @return the current logger factory
	 */
	public static LoggerFactory getCurrentLoggerFactory(String key) {
		if (LoggerFactory.init) {
			return loggerFactory;
		}
		loggerFactory.init(key);
		return loggerFactory;
	}

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
