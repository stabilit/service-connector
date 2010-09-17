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
package org.serviceconnector.common.log.impl;

import java.util.Formatter;

import org.apache.log4j.Logger;
import org.serviceconnector.common.log.ISubscriptionLogger;
import org.serviceconnector.common.log.Loggers;


public class SubscriptionLogger implements ISubscriptionLogger {

	private static final Logger logger = Logger.getLogger(Loggers.SUBSCRIPTION.getValue());
	private static final ISubscriptionLogger SUBSCRIPTION_LOGGER = new SubscriptionLogger();

	private static String SUBSCRIBE_STR = "session:%s - subscribing to:%s - with mask:%s";
	private static String CHANGE_SUBSCRIBE_STR = "session:%s - subscribed to:%s - new mask:%s";
	private static String UNSUBSCRIBE_STR = "session:%s - unsubscribing from:%s";

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
	public synchronized void logSubscribe(String serviceName, String sessionId, String mask) {
		if (logger.isInfoEnabled()) {
			Formatter format = new Formatter();
			format.format(SUBSCRIBE_STR, sessionId, serviceName, mask);
			logger.debug(format.toString());
			format.close();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logChangeSubscribe(String serviceName, String sessionId, String mask) {
		if (logger.isInfoEnabled()) {
			Formatter format = new Formatter();
			format.format(CHANGE_SUBSCRIBE_STR, sessionId, serviceName, mask);
			logger.debug(format.toString());
			format.close();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void logUnsubscribe(String serviceName, String sessionId) {
		if (logger.isInfoEnabled()) {
			Formatter format = new Formatter();
			format.format(UNSUBSCRIBE_STR, sessionId, serviceName);
			logger.debug(format.toString());
			format.close();
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
}