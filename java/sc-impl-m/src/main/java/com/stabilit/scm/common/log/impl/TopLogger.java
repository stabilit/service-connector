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

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.listener.ILoggerListener;
import com.stabilit.scm.common.listener.LoggerEvent;
import com.stabilit.scm.common.log.ILogger;
import com.stabilit.scm.common.log.ILoggerDecorator;

/**
 * The Class RuntimeLogger. Provides functionality of logging an <code>WarningEvent</code>.
 */
public class TopLogger implements ILoggerListener, ILoggerDecorator {

	/** The concrete logger implementation to use. */
	private ILogger logger;

	/**
	 * Instantiates a new runtime logger. Only visible in package for Factory.
	 * 
	 * @param logger
	 *            the logger
	 */
	TopLogger(ILogger logger) {
		this.logger = logger.newInstance(this);
	}

	@Override
	public void logEvent(LoggerEvent loggerEvent) throws Exception {

		String text = loggerEvent.getText();

		switch (loggerEvent.getLevel()) {
		case INFO:
			this.logger.logInfo(text);
			break;
		case WARN:
			this.logger.logWarn(text);
			break;
		case ERROR:
			this.logger.logError(text);
			break;
		case DEBUG:
			this.logger.logDebug(text);
			break;
		case FATAL:
			this.logger.logError(text);
			break;
		default:
			this.logger.logInfo(text);
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
		return IConstants.LOG_DIR;
	}

	/** {@inheritDoc} */
	@Override
	public String getLogFileName() {
		return IConstants.TOP_LOG_FILE_NAME;
	}
}
