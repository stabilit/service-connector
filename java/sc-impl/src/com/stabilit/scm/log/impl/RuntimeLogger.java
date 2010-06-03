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

import com.stabilit.sc.config.IConstants;
import com.stabilit.sc.listener.IRuntimeListener;
import com.stabilit.sc.listener.RuntimeEvent;
import com.stabilit.sc.log.ILogger;
import com.stabilit.sc.log.ILoggerDecorator;

/**
 * The Class RuntimeLogger. Provides functionality of logging an <code>WarningEvent</code>.
 */
public class RuntimeLogger implements IRuntimeListener, ILoggerDecorator {

	/** The concrete logger implementation to use. */
	private ILogger logger;

	/**
	 * Instantiates a new runtime logger. Only visible in package for Factory.
	 * 
	 * @param logger
	 *            the logger
	 */
	RuntimeLogger(ILogger logger) {
		this.logger = logger.newInstance(this);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void runtimeEvent(RuntimeEvent runtimeEvent) {
		try {
			this.logger.log(runtimeEvent.getText());
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
		return IConstants.LOG_DIR;
	}

	/** {@inheritDoc} */
	@Override
	public String getLogFileName() {
		return IConstants.RUNTIME_LOG_FILE_NAME;
	}
}
