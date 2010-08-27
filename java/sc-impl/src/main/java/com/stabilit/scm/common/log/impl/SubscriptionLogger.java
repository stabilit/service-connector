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

	private static final Logger subscriptionLogger = Logger.getLogger(Loggers.SUBSCRIPTION.getValue());
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
	public void logNoDataTimeout(String source, String sessionId) {
		if (SubscriptionLogger.subscriptionLogger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(NO_DATA_TIMEOUT_EVENT_STR, source, sessionId);
		SubscriptionLogger.subscriptionLogger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public void logPoll(String source, String sessionId, SCMPMessage queueMessage, int queueSize) {
		if (SubscriptionLogger.subscriptionLogger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(FIRE_POLL_EVENT_STR, source, sessionId, queueMessage, queueSize);
		SubscriptionLogger.subscriptionLogger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public void logAdd(String source, SCMPMessage queueMessage, int queueSize) {
		if (SubscriptionLogger.subscriptionLogger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(FIRE_ADD_EVENT_STR, source, queueMessage, queueSize);
		SubscriptionLogger.subscriptionLogger.debug(format.toString());
		format.close();
	}

	/** {@inheritDoc} */
	@Override
	public void logRemove(String source, int queueSize) {
		if (SubscriptionLogger.subscriptionLogger.isDebugEnabled() == false) {
			return;
		}
		Formatter format = new Formatter();
		format.format(FIRE_REMOVE_EVENT_STR, source, queueSize);
		SubscriptionLogger.subscriptionLogger.debug(format.toString());
		format.close();
	}
}