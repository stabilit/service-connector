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

import java.util.Date;
import java.util.Formatter;

import org.serviceconnector.service.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SubscriptionLogger.
 */
public final class SubscriptionLogger {

	/** The Constant subscriptionLogger. */
	private static final Logger SUBSCRIPTION_LOGGER = LoggerFactory.getLogger(Loggers.SUBSCRIPTION.getValue());

	/** The subscribe str. */
	private static String subscribeStr = "subscription=%s - subscribing to=%s - with mask=%s";
	/** The change subscribe str. */
	private static String changeSubscribeStr = "subscription=%s - change subscription to=%s - new mask=%s";
	/** The unsubscribe str. */
	private static String unsubscribeStr = "subscription=%s - unsubscribing from=%s";
	/** The create subscription str. */
	private static String createSubscriptionStr = "create subscription=%s timeout=%sms noi=%sms";
	/** The delete subscription str. */
	private static String deleteSubscriptionStr = "delete subscription=%s";
	/** The abort subscription str. */
	private static String abortSubscriptionStr = "abort subscription sid=%s noi=%sms creationTime=%3$tH:%3$tM:%3$tS.%3$tL reason=%4$s";
	/** The timeout subscription string. */
	private static String timeoutSubscriptionStr = "timeout subscription sid=%s noi=%sms creationTime=%3$tH:%3$tM:%3$tS.%3$tL";
	/** The schedule timeout string. */
	private static String scheduleTimeoutStr = "schedule subscription sid=%s, timeout=%sms, delay=%sms";
	/** The cancel timeout string. */
	private static String cancelTimeoutStr = "cancel subscription session sid=%s";

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
	public static boolean isTraceEnabled() {
		return SUBSCRIPTION_LOGGER.isTraceEnabled();
	}

	/**
	 * Checks if is debug enabled.
	 *
	 * @return true, if is debug enabled
	 */
	public static boolean isDebugEnabled() {
		return SUBSCRIPTION_LOGGER.isDebugEnabled();
	}

	/**
	 * Log subscribe.
	 *
	 * @param serviceName the service name
	 * @param subscriptionId the subscription Id
	 * @param mask the mask
	 */
	public static synchronized void logSubscribe(String serviceName, String subscriptionId, String mask) {
		if (SUBSCRIPTION_LOGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(subscribeStr, subscriptionId, serviceName, mask);
			SUBSCRIPTION_LOGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * Log change subscribe.
	 *
	 * @param serviceName the service name
	 * @param subscriptionId the subscription Id
	 * @param mask the mask
	 */
	public static synchronized void logChangeSubscribe(String serviceName, String subscriptionId, String mask) {
		if (SUBSCRIPTION_LOGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(changeSubscribeStr, subscriptionId, serviceName, mask);
			SUBSCRIPTION_LOGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * Log unsubscribe.
	 *
	 * @param serviceName the service name
	 * @param subscriptionId the subscription Id
	 */
	public static synchronized void logUnsubscribe(String serviceName, String subscriptionId) {
		if (SUBSCRIPTION_LOGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(unsubscribeStr, subscriptionId, serviceName);
			SUBSCRIPTION_LOGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * Log create subscription.
	 *
	 * @param id the id
	 * @param timeoutMillis the timeout
	 */
	public static synchronized void logCreateSubscription(String id, double timeoutMillis, double noiMillis) {
		if (SUBSCRIPTION_LOGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(createSubscriptionStr, id, timeoutMillis, noiMillis);
			SUBSCRIPTION_LOGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * Log delete subscription.
	 *
	 * @param id the id
	 */
	public static synchronized void logDeleteSubscription(String id) {
		if (SUBSCRIPTION_LOGGER.isDebugEnabled()) {
			Formatter format = new Formatter();
			format.format(deleteSubscriptionStr, id);
			SUBSCRIPTION_LOGGER.debug(format.toString());
			format.close();
		}
	}

	/**
	 * Log abort subscription.
	 *
	 * @param subscription the subscription
	 * @param reason the reason
	 */
	public static synchronized void logAbortSubscription(Subscription subscription, String reason) {
		if (SUBSCRIPTION_LOGGER.isInfoEnabled()) {
			Formatter format = new Formatter();
			String subscriptionId = subscription.getId();
			int noi = subscription.getNoDataIntervalMillis();
			Date creationTime = subscription.getCreationTime();
			format.format(abortSubscriptionStr, subscriptionId, noi, creationTime, reason);
			SUBSCRIPTION_LOGGER.info(format.toString());
			format.close();
		}
	}

	/**
	 * Log timeout subscription.
	 *
	 * @param subscription the subscription
	 */
	public static synchronized void logTimeoutSubscription(Subscription subscription) {
		if (SUBSCRIPTION_LOGGER.isInfoEnabled()) {
			Formatter format = new Formatter();
			String subscriptionId = subscription.getId();
			int noi = subscription.getNoDataIntervalMillis();
			Date creationTime = subscription.getCreationTime();
			format.format(timeoutSubscriptionStr, subscriptionId, noi, creationTime);
			SUBSCRIPTION_LOGGER.info(format.toString());
			format.close();
		}
	}

	/**
	 * Log start timeout scheduling.
	 *
	 * @param sessionId the session id
	 */
	public static synchronized void logScheduleTimeout(String sessionId, double timeout, long delay) {
		if (SUBSCRIPTION_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(scheduleTimeoutStr, sessionId, timeout, delay);
			SUBSCRIPTION_LOGGER.trace(format.toString());
			format.close();
		}
	}

	/**
	 * Log cancel timeout.
	 *
	 * @param sessionId the session id
	 */
	public static synchronized void logCancelTimeout(String sessionId) {
		if (SUBSCRIPTION_LOGGER.isTraceEnabled()) {
			Formatter format = new Formatter();
			format.format(cancelTimeoutStr, sessionId);
			SUBSCRIPTION_LOGGER.trace(format.toString());
			format.close();
		}
	}
}
