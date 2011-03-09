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
	private static final Logger subscriptionLogger = Logger.getLogger(Loggers.SUBSCRIPTION.getValue());

	/** The SUBSCRIB e_ str. */
	private static String SUBSCRIBE_STR = "subscription:%s - subscribing to:%s - with mask:%s";
	
	/** The CHANG e_ subscrib e_ str. */
	private static String CHANGE_SUBSCRIBE_STR = "subscription:%s - change subscription to:%s - new mask:%s";
	
	/** The UNSUBSCRIB e_ str. */
	private static String UNSUBSCRIBE_STR = "subscription:%s - unsubscribing from:%s";
	
	/** The CREAT e_ subscriptio n_ str. */
	private static String CREATE_SUBSCRIPTION_STR = "create subscription:%s timeout=%s";
	
	/** The DELET e_ subscriptio n_ str. */
	private static String DELETE_SUBSCRIPTION_STR = "delete subscription:%s";
	
	/** The ABOR t_ subscriptio n_ str. */
	private static String ABORT_SUBSCRIPTION_STR = "abort subscription:%s";

	/**
	 * Private constructor for singleton use.
	 */
	private SubscriptionLogger() {
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
		if (subscriptionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(SUBSCRIBE_STR, sessionId, serviceName, mask);
			subscriptionLogger.trace(format.toString());
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
		if (subscriptionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(CHANGE_SUBSCRIBE_STR, sessionId, serviceName, mask);
			subscriptionLogger.trace(format.toString());
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
		if (subscriptionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(UNSUBSCRIBE_STR, sessionId, serviceName);
			subscriptionLogger.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public static boolean isEnabled() {
		return subscriptionLogger.isTraceEnabled();
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
		if (subscriptionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(CREATE_SUBSCRIPTION_STR, id, timeout);
			subscriptionLogger.trace(format.toString());
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
		if (subscriptionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(DELETE_SUBSCRIPTION_STR, id);
			subscriptionLogger.trace(format.toString());
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
		if (subscriptionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(ABORT_SUBSCRIPTION_STR, id);
			subscriptionLogger.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Warn.
	 * 
	 * @param message
	 *            the message
	 */
	public static void warn(String message) {
		if (subscriptionLogger.isEnabledFor(Level.WARN)) {
			subscriptionLogger.warn(message);
		}
	}
	
	/**
	 * Debug.
	 * 
	 * @param message
	 *            the message
	 */
	public static void debug(String message) {
		if (subscriptionLogger.isEnabledFor(Level.DEBUG)) {
			subscriptionLogger.debug(message);
		}
	}
}