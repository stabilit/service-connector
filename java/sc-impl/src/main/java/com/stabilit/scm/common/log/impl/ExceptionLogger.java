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

import java.util.Formatter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.stabilit.scm.common.log.IExceptionLogger;

public class ExceptionLogger implements IExceptionLogger {

	private static final IExceptionLogger EXCEPTION_LOGGER = new ExceptionLogger();
	
	private static String EXC_STR = "exception by %s - %s:%s";

	/**
	 * Instantiates a new exception logger. Private for singelton use.
	 */
	private ExceptionLogger() {
	}

	public static IExceptionLogger getInstance() {
		return ExceptionLogger.EXCEPTION_LOGGER;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logDebugException(Logger logger, String className, Throwable throwable) {
		if (logger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(EXC_STR, className, throwable, throwable.getCause());
		logger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logErrorException(Logger logger, String className, Throwable throwable) {
		if (logger.isEnabledFor(Level.ERROR) == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(EXC_STR, className, throwable, throwable.getCause());
		logger.error(format.toString());
		format.close();
	}
}
