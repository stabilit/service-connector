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

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.util.ITimerRun;
import org.serviceconnector.util.TimerTaskWrapper;

/**
 * The Class SubscriptionRegistry. Registry stores entries for properly created subscriptions.
 * 
 * @author JTraber
 */
public class SubscriptionRegistry extends Registry<String, Subscription> {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SubscriptionRegistry.class);
	/** The Constant sessionLogger. */
	private final static SubscriptionLogger subscriptionLogger = SubscriptionLogger.getInstance();
	/** The timer. Timer instance is responsible to observe subscription timeouts. */
	private Timer timer;

	public SubscriptionRegistry() {
		this.timer = new Timer("SubscriptionRegistryTimer");
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
		subscriptionLogger.logCreateSubscription(subscription.getId());
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
		subscriptionLogger.logDeleteSubscription(key);
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

	public void scheduleSubscriptionTimeout(Subscription subscription) {
		if (subscription == null) {
			// no scheduling of Subscription timeout
			return;
		}
		// always cancel old timeouter before setting up a new one
		this.cancelSubscriptionTimeout(subscription);
		TimerTaskWrapper subscriptionTimeouter = subscription.getSessionTimeouter();

		// sets up subscription timeout
		subscriptionTimeouter = new TimerTaskWrapper(new SubscriptionTimerRun(subscription));
		subscription.setSessionTimeouter(subscriptionTimeouter);
		// schedule subscriptionTimeouter in registry timer
		this.timer.schedule(subscriptionTimeouter, AppContext.getBasicConfiguration().getSubscriptionTimeout());
	}

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
		TimerTask subscriptionTimeouter = subscription.getSessionTimeouter();
		if (subscriptionTimeouter == null) {
			// no subscription timeout has been set up for this subscription
			return;
		}
		subscriptionTimeouter.cancel();
		// important to set timeouter null - rescheduling of same instance not possible
		subscription.setSessionTimeouter(null);
	}

	public void cancelSubscriptionTimeout(String key) {
		Subscription subscription = this.get(key);
		this.cancelSubscriptionTimeout(subscription);
	}

	/**
	 * The Class SubscriptionTimerRun. Gets control when a subscription times out. Responsible for cleaning up when subscription gets
	 * broken.
	 */
	private class SubscriptionTimerRun implements ITimerRun {

		/** The session. */
		private Subscription subscription;

		/** The callback, callback to send abort subscription. */

		/**
		 * Instantiates a new subscription timer run.
		 * 
		 * @param subscription
		 *            the session
		 */
		public SubscriptionTimerRun(Subscription subscription) {
			this.subscription = subscription;
		}

		/**
		 * Timeout. Subscription timeout run out.
		 */
		@Override
		public void timeout() {
			/**
			 * broken subscription procedure<br>
			 * 1. unsubscribe (discard messages for client) subscription in queue<br>
			 * 2. abort subscription on backend server<br>
			 */
			SubscriptionQueue<SCMPMessage> subscriptionQueue = ((PublishService) subscription.getServer().getService())
					.getSubscriptionQueue();
			subscriptionQueue.unsubscribe(subscription.getId());

			StatefulServer server = subscription.getServer();
			server.abortSession(subscription);
			SubscriptionRegistry.this.subscriptionLogger.logAbortSubscription(subscription.getId());
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return AppContext.getBasicConfiguration().getSubscriptionTimeout();
		}
	}
}