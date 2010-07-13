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
package com.stabilit.scm.common.log.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.log.ILogger;
import com.stabilit.scm.common.log.ILoggerDecorator;
import com.stabilit.scm.common.log.Level;

/**
 * The Class Log4jLogger. Provides Access to log4j framework. Logging works over log4j. Configurations are in log4j
 * property files.
 */
public class Log4jLogger implements ILogger {

	/** The logger. */
	private Logger log;

	/**
	 * Instantiates a new log4j logger. Only visible in package for Factory.
	 */
	Log4jLogger() {
	}

	/**
	 * Instantiates a new log4j logger. Not visible outside. Instantiation should be done over new instance methods.
	 * 
	 * @param log
	 *            the log
	 */
	private Log4jLogger(Logger log) {
		this.log = log;
	}

	/** {@inheritDoc} */
	@Override
	public void log(Object obj) throws IOException {
		throw new IOException("not supported");
	}

	/** {@inheritDoc} */
	@Override
	public void log(String msg) throws IOException {
		this.log.info(msg);
	}

	/** {@inheritDoc} */
	@Override
	public void log(Throwable t) throws IOException {
		this.log.info(t);
	}

	/** {@inheritDoc} */
	@Override
	public void log(Level level, String msg) throws IOException {
		this.log.info(msg);
	}

	/** {@inheritDoc} */
	@Override
	public void logError(String msg) throws IOException {
		this.log.error(msg);
	}

	/** {@inheritDoc} */
	@Override
	public void logWarn(String msg) throws IOException {
		this.log.warn(msg);
	}

	/** {@inheritDoc} */
	@Override
	public void logInfo(String msg) throws IOException {
		this.log.info(msg);
	}

	/** {@inheritDoc} */
	@Override
	public void logDebug(String msg) throws IOException {
		this.log.debug(msg);
	}

	/** {@inheritDoc} */
	@Override
	public ILogger newInstance() {
		// careful in use - is always the same instance
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ILogger newInstance(ILoggerDecorator loggerDecorator) {
		// class necessary to get correct logger from log4j framework
		Logger log = Logger.getLogger(loggerDecorator.getClass());
		// we need a new instance in this case
		Log4jLogger log4jLogger = new Log4jLogger(log);
		return log4jLogger;
	}
}