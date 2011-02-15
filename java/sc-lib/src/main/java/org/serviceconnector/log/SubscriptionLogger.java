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

public class SubscriptionLogger {

	private static final Logger subscriptionLogger = Logger.getLogger(Loggers.SUBSCRIPTION.getValue());

	private static String SUBSCRIBE_STR = "subscription:%s - subscribing to:%s - with mask:%s";
	private static String CHANGE_SUBSCRIBE_STR = "subscription:%s - change subscription to:%s - new mask:%s";
	private static String UNSUBSCRIBE_STR = "subscription:%s - unsubscribing from:%s";
	private static String CREATE_SUBSCRIPTION_STR = "create subscription:%s timeout=%s";
	private static String DELETE_SUBSCRIPTION_STR = "delete subscription:%s";
	private static String ABORT_SUBSCRIPTION_STR = "abort subscription:%s";

	/**
	 * Private constructor for singleton use.
	 */
	private SubscriptionLogger() {
	}

	/**
	 * @param serviceName
	 * @param sessionId
	 * @param mask
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
	 * @param serviceName
	 * @param sessionId
	 * @param mask
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
	 * @param serviceName
	 * @param sessionId
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

	public static void logCreateSubscription(String id, double timeout) {
		if (subscriptionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(CREATE_SUBSCRIPTION_STR, id, timeout);
			subscriptionLogger.trace(format.toString());
			format.close();
		}
	}

	public static void logDeleteSubscription(String id) {
		if (subscriptionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(DELETE_SUBSCRIPTION_STR, id);
			subscriptionLogger.trace(format.toString());
			format.close();
		}
	}

	public static void logAbortSubscription(String id) {
		if (subscriptionLogger.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(ABORT_SUBSCRIPTION_STR, id);
			subscriptionLogger.trace(format.toString());
			format.close();
		}
	}

	public static void warn(String message) {
		if (subscriptionLogger.isEnabledFor(Level.WARN)) {
			subscriptionLogger.warn(message);
		}
	}
	
	public static void debug(String message) {
		if (subscriptionLogger.isEnabledFor(Level.DEBUG)) {
			subscriptionLogger.debug(message);
		}
	}
}