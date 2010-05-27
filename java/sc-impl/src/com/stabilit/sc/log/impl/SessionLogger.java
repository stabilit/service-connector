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

import java.util.Formatter;

import com.stabilit.sc.config.IConstants;
import com.stabilit.sc.listener.ISessionListener;
import com.stabilit.sc.listener.SessionEvent;
import com.stabilit.sc.log.ILogger;
import com.stabilit.sc.log.ILoggerDecorator;

public class SessionLogger implements ISessionListener, ILoggerDecorator {

	/** The concrete logger implementation to use. */
	private ILogger logger;

	private Formatter format;
	private String CREATE_SESSION_STR = "create session:%s";
	private String DELETE_SESSION_STR = "delete session:%s";

	SessionLogger(ILogger logger) {
		this.format = null;
		this.logger = logger.newInstance(this);
	}

	/** {@inheritDoc} */
	@Override
	public void createSessionEvent(SessionEvent sessionEvent) throws Exception {
		format = new Formatter();
		format.format(CREATE_SESSION_STR, sessionEvent.getSessionId());
		this.logger.log(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public void deleteSessionEvent(SessionEvent sessionEvent) throws Exception {
		format = new Formatter();
		format.format(DELETE_SESSION_STR, sessionEvent.getSessionId());
		this.logger.log(format.toString());
		format.close();
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
		return IConstants.SESSION_LOG_FILE_NAME;
	}
}
