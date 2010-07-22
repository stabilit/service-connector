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
import java.util.Formatter;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.ExceptionEvent;
import com.stabilit.scm.common.listener.IExceptionListener;
import com.stabilit.scm.common.log.ILogger;
import com.stabilit.scm.common.log.ILoggerDecorator;

/**
 * The Class ExceptionLogger. Provides functionality of logging an <code>ExceptionEvent</code>.
 */
public class ExceptionLogger implements IExceptionListener, ILoggerDecorator {

	/** The concrete logger implementation to use. */
	private ILogger logger;

	private Formatter format;
	private String EXC_STR = "exception by class %s - %s:%s";

	/**
	 * Instantiates a new exception logger. Only visible in package for Factory.
	 * 
	 * @param logger
	 *            the logger
	 */
	ExceptionLogger(ILogger logger) {
		this.logger = logger.newInstance(this);
		this.format = null;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void exceptionEvent(ExceptionEvent exceptionEvent) {
		try {
			format = new Formatter();
			format.format(EXC_STR, exceptionEvent.getSource().getClass(), exceptionEvent.getThrowable(), exceptionEvent
					.getThrowable().getCause());
			this.logger.log(format.toString());
			format.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public ILoggerDecorator newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String getLogDir() {
		return Constants.LOG_DIR;
	}

	/** {@inheritDoc} */
	@Override
	public String getLogFileName() {
		return Constants.EXCEPTION_LOG_FILE_NAME;
	}
}
