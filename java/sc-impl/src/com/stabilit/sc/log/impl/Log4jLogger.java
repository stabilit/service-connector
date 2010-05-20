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

import java.io.IOException;

import org.apache.log4j.Logger;

import com.stabilit.sc.log.ILogger;
import com.stabilit.sc.log.ILoggerDecorator;
import com.stabilit.sc.log.Level;

public class Log4jLogger implements ILogger {

	private Logger log;

	Log4jLogger() {
	}

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
	public void log(byte[] buffer) throws IOException {
		this.log.info(buffer);
	}

	/** {@inheritDoc} */
	@Override
	public void log(byte[] buffer, int offset, int length) throws IOException {
		this.log.info(buffer);
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

	public void logError(String msg) throws IOException {
		this.log.error(msg);
	}

	public void logWarn(String msg) throws IOException {
		this.log.warn(msg);
	}

	public void logInfo(String msg) throws IOException {
		this.log.info(msg);
	}

	public void logDebug(String msg) throws IOException {
		this.log.debug(msg);
	}

	public void logTrace(String msg) throws IOException {
		this.log.trace(msg);
	}

	@Override
	public ILogger newInstance() {
		return this;
	}

	@Override
	public ILogger newInstance(ILoggerDecorator loggerDecorator) {
		Logger log = Logger.getLogger(loggerDecorator.getClass());
		Log4jLogger log4jLogger = new Log4jLogger(log);
		return log4jLogger;
	}
}