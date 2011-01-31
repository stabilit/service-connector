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
package org.serviceconnector.registry;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.IStatefulServer;
import org.serviceconnector.service.IPublishService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.TimeoutWrapper;

/**
 * The Class SubscriptionRegistry. Registry stores entries for properly created subscriptions.
 * 
 * @author JTraber
 */
public class SubscriptionRegistry extends Registry<String, Subscription> {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SubscriptionRegistry.class);

	/** The timer. Timer instance is responsible to observe subscription timeouts. */
	private ScheduledThreadPoolExecutor subscriptionScheduler;

	public SubscriptionRegistry() {
		this.subscriptionScheduler = new ScheduledThreadPoolExecutor(1);
	}

	/**
	 * Adds the subscription.
	 * 
	 * @param key
	 *            the key
	 * @param subscription
	 *            the subscription
	 */
	public void addSubscription(String key, Subscription subscription) {
		SubscriptionLogger.logCreateSubscription(subscription.getId());
		this.put(key, subscription);
		this.scheduleSubscriptionTimeout(subscription);
	}

	/**
	 * Removes the subscription.
	 * 
	 * @param subscription
	 *            the subscription
	 */
	public void removeSubscription(Subscription subscription) {
		this.removeSubscription(subscription.getId());
	}

	/**
	 * Removes the subscription.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeSubscription(String key) {
		if (key == null) {
			return;
		}
		super.remove(key);
		this.cancelSubscriptionTimeout(key);
		SubscriptionLogger.logDeleteSubscription(key);
	}

	/**
	 * Gets the subscription.
	 * 
	 * @param key
	 *            the key
	 * @return the subscription
	 */
	public Subscription getSubscription(String key) {
		Subscription subscription = super.get(key);
		return subscription;
	}

	/**
	 * Schedule subscription timeout.
	 * 
	 * @param subscription
	 *            the subscription
	 */
	@SuppressWarnings("unchecked")
	public void scheduleSubscriptionTimeout(Subscription subscription) {
		if (subscription == null) {
			// no scheduling of Subscription timeout
			return;
		}
		// always cancel old timeout before setting up a new one
		this.cancelSubscriptionTimeout(subscription);
		// sets up subscription timeout
		TimeoutWrapper subscriptionTimeouter = new TimeoutWrapper(new SubscriptionTimeout(subscription));
		// schedule sessionTimeouter in registry timer
		ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) this.subscriptionScheduler.schedule(
				subscriptionTimeouter, AppContext.getBasicConfiguration().getSubscriptionTimeoutMillis(), TimeUnit.MILLISECONDS);
		subscription.setTimeout(timeout);
		logger.trace("schedule subscription timeout millis: " + AppContext.getBasicConfiguration().getSubscriptionTimeoutMillis()
				+ " id: " + subscription.getId());
	}

	/**
	 * Schedule subscription timeout.
	 * 
	 * @param key
	 *            the key
	 */
	public void scheduleSubscriptionTimeout(String key) {
		Subscription subscription = this.get(key);
		this.scheduleSubscriptionTimeout(subscription);
	}

	/**
	 * Cancel session timeout.
	 * 
	 * @param subscription
	 *            the session
	 */
	public void cancelSubscriptionTimeout(Subscription subscription) {
		if (subscription == null) {
			return;
		}
		ScheduledFuture<TimeoutWrapper> subscriptionTimeout = subscription.getTimeout();
		if (subscriptionTimeout == null) {
			// no subscription timeout has been set up for this subscription
			return;
		}
		logger.trace("cancel session timeout " + subscription.getId());
		boolean cancelSuccess = subscriptionTimeout.cancel(false);
		if (cancelSuccess == false) {
			SubscriptionLogger.warn("cancel of subscription timeout failed :" + subscription.getId() + " delay millis: "
					+ subscriptionTimeout.getDelay(TimeUnit.MILLISECONDS));
			boolean remove = this.subscriptionScheduler.remove(subscription.getTimeouterTask());
			if (remove == false) {
				SubscriptionLogger.warn("remove of subscription timeout failed :" + subscription.getId() + " delay millis: "
						+ subscriptionTimeout.getDelay(TimeUnit.MILLISECONDS));
			}
		}

		// tries removing canceled timeouts
		this.subscriptionScheduler.purge();
		// important to set timeout null - rescheduling of same instance not possible
		subscription.setTimeout(null);
		logger.trace("cancel subscription timeout " + subscription.getId());
	}

	public void cancelSubscriptionTimeout(String key) {
		Subscription subscription = this.get(key);
		this.cancelSubscriptionTimeout(subscription);
	}

	/**
	 * The Class SubscriptionTimeout. Gets control when a subscription times out. Responsible for cleaning up when subscription gets
	 * broken.
	 */
	private class SubscriptionTimeout implements ITimeout {

		/** The session. */
		private Subscription subscription;

		/** The callback, callback to send abort subscription. */

		/**
		 * Instantiates a new subscription timer run.
		 * 
		 * @param subscription
		 *            the session
		 */
		public SubscriptionTimeout(Subscription subscription) {
			this.subscription = subscription;
		}

		/**
		 * Timeout. Subscription timeout run out.
		 */
		@Override
		public void timeout() {
			/**
			 * broken subscription procedure<br>
			 * 1. remove subscription from registry<br>
			 * 2. unsubscribe (discard messages for client) subscription in queue<br>
			 * 3. abort subscription on backend server<br>
			 */
			SubscriptionRegistry.this.removeSubscription(subscription);
			SubscriptionQueue<SCMPMessage> subscriptionQueue = ((IPublishService) subscription.getService()).getSubscriptionQueue();
			subscriptionQueue.unsubscribe(subscription.getId());

			IStatefulServer server = subscription.getServer();
			server.abortSession(subscription, "subscription timed out in registry");
			SubscriptionLogger.logAbortSubscription(subscription.getId());
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return AppContext.getBasicConfiguration().getSubscriptionTimeoutMillis();
		}
	}
}