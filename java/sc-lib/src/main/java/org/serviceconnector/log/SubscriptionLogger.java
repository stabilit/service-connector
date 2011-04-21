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
package org.serviceconnector.log;

import java.util.Formatter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The Class SubscriptionLogger.
 */
public final class SubscriptionLogger {

	/** The Constant subscriptionLogger. */
	private static final Logger SUBSCRIPTION_LOGGER = Logger.getLogger(Loggers.SUBSCRIPTION.getValue());

	/** The subscribe str. */
	private static String subscribeStr = "subscription:%s - subscribing to:%s - with mask:%s";
	/** The change subscribe str. */
	private static String changeSubscribeStr = "subscription:%s - change subscription to:%s - new mask:%s";
	/** The unsubscribe str. */
	private static String unsubscribeStr = "subscription:%s - unsubscribing from:%s";
	/** The create subscription str. */
	private static String createSubscriptionStr = "create subscription:%s timeout=%s";
	/** The delete subscription str. */
	private static String deleteSubscriptionStr = "delete subscription:%s";
	/** The abort subscription str. */
	private static String abortSubscriptionStr = "abort subscription:%s";

	/**
	 * Private constructor for singleton use.
	 */
	private SubscriptionLogger() {
	}
	
	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public static boolean isEnabled() {
		return SUBSCRIPTION_LOGGER.isTraceEnabled();
	}

	/**
	 * Log subscribe.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 * @param mask
	 *            the mask
	 */
	public static synchronized void logSubscribe(String serviceName, String sessionId, String mask) {
		if (SUBSCRIPTION_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(subscribeStr, sessionId, serviceName, mask);
			SUBSCRIPTION_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log change subscribe.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 * @param mask
	 *            the mask
	 */
	public static synchronized void logChangeSubscribe(String serviceName, String sessionId, String mask) {
		if (SUBSCRIPTION_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(changeSubscribeStr, sessionId, serviceName, mask);
			SUBSCRIPTION_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log unsubscribe.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 */
	public static synchronized void logUnsubscribe(String serviceName, String sessionId) {
		if (SUBSCRIPTION_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(unsubscribeStr, sessionId, serviceName);
			SUBSCRIPTION_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log create subscription.
	 * 
	 * @param id
	 *            the id
	 * @param timeout
	 *            the timeout
	 */
	public static void logCreateSubscription(String id, double timeout) {
		if (SUBSCRIPTION_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(createSubscriptionStr, id, timeout);
			SUBSCRIPTION_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log delete subscription.
	 * 
	 * @param id
	 *            the id
	 */
	public static void logDeleteSubscription(String id) {
		if (SUBSCRIPTION_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(deleteSubscriptionStr, id);
			SUBSCRIPTION_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log abort subscription.
	 * 
	 * @param id
	 *            the id
	 */
	public static void logAbortSubscription(String id) {
		if (SUBSCRIPTION_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(abortSubscriptionStr, id);
			SUBSCRIPTION_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Trace.
	 * 
	 * @param message
	 *            the message
	 */
	public static void trace(String message) {
		if (SUBSCRIPTION_LOGGER.isEnabledFor(Level.TRACE)) {
			SUBSCRIPTION_LOGGER.trace(message);
		}
	}

	/**
	 * Error.
	 * 
	 * @param message
	 *            the message
	 */
	public static void error(String message) {
		if (SUBSCRIPTION_LOGGER.isEnabledFor(Level.ERROR)) {
			SUBSCRIPTION_LOGGER.error(message);
		}
	}
}