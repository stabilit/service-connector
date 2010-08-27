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

import org.apache.log4j.Logger;

import com.stabilit.scm.common.log.IMessageLogger;
import com.stabilit.scm.common.log.Loggers;
import com.stabilit.scm.common.scmp.SCMPMessage;

public class MessageLogger implements IMessageLogger {

	private static final Logger logger = Logger.getLogger(Loggers.MESSAGE.getValue());
	private static final IMessageLogger MESSAGE_LOGGER = new MessageLogger();
	
	private String MSG_LONG_STR = "msg:%s";
	private String MSG_SHORT_STR = "msg:%s";

	/**
	 * Instantiates a new connection logger. Private for singelton use.
	 */
	private MessageLogger() {
	}

	public static IMessageLogger getInstance() {
		return MessageLogger.MESSAGE_LOGGER;
	}
	
	/** {@inheritDoc} */
	@Override
	public synchronized void logMessage(String className, SCMPMessage message) {
		if (logger.isInfoEnabled()) {
			try {
				Formatter format = new Formatter();
				format.format(MSG_SHORT_STR, message);
				logger.info(format.toString());
				format.close();
			} catch (Exception e) {
				// TODO JOT exception logging
			}
		}
		if (logger.isDebugEnabled()) {
			try {
				Formatter format = new Formatter();
				format.format(MSG_LONG_STR, message);
				logger.debug(format.toString());
				format.close();
			} catch (Exception e) {
				// TODO JOT exception logging
			}
		}

	}
}
