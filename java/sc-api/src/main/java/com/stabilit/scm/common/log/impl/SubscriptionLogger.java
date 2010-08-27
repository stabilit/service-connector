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

import com.stabilit.scm.common.log.ISubscriptionLogger;
import com.stabilit.scm.common.log.Loggers;
import com.stabilit.scm.common.scmp.SCMPMessage;

public class SubscriptionLogger implements ISubscriptionLogger {

	private static final Logger logger = Logger.getLogger(Loggers.SUBSCRIPTION.getValue());
	private static final ISubscriptionLogger SUBSCRIPTION_LOGGER = new SubscriptionLogger();

	private static String NO_DATA_TIMEOUT_EVENT_STR = "no data timeout by class %s - for sessionId %s";
	private static String FIRE_POLL_EVENT_STR = "fire poll by class %s - for sessionId %s, polled message %s, now %s messages in queue";
	private static String FIRE_ADD_EVENT_STR = "fire add by class %s - add message %s, now %s messages in queue";
	private static String FIRE_REMOVE_EVENT_STR = "remove by class %s - remove message, now %s messages in queue";

	/**
	 * Instantiates a new subscription logger. Private for singelton use.
	 */
	private SubscriptionLogger() {
	}

	public static ISubscriptionLogger getInstance() {
		return SubscriptionLogger.SUBSCRIPTION_LOGGER;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logNoDataTimeout(String className, String sessionId) {
		if (logger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(NO_DATA_TIMEOUT_EVENT_STR, className, sessionId);
		logger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logPoll(String className, String sessionId, SCMPMessage queueMessage, int queueSize) {
		if (SubscriptionLogger.logger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(FIRE_POLL_EVENT_STR, className, sessionId, queueMessage, queueSize);
		logger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logAdd(String className, SCMPMessage queueMessage, int queueSize) {
		if (logger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(FIRE_ADD_EVENT_STR, className, queueMessage, queueSize);
		logger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logRemove(String className, int queueSize) {
		if (SubscriptionLogger.logger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(FIRE_REMOVE_EVENT_STR, className, queueSize);
		logger.debug(format.toString());
		format.close();
	}
}