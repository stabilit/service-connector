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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.server.IStatefulServer;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.NamedPriorityThreadFactory;
import org.serviceconnector.util.TimeoutWrapper;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class SubscriptionRegistry. Registry stores entries for properly created subscriptions.
 *
 * @author JTraber
 */
public class SubscriptionRegistry extends Registry<String, Subscription> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRegistry.class);

	/** The timer. Timer instance is responsible to observe subscription timeouts. */
	private ScheduledThreadPoolExecutor subscriptionScheduler;

	/**
	 * Instantiates a new subscription registry.
	 */
	public SubscriptionRegistry() {
		this.subscriptionScheduler = new ScheduledThreadPoolExecutor(1, new NamedPriorityThreadFactory("SubscriptionTimeout"));
	}

	/**
	 * Adds the subscription.
	 *
	 * @param key the key
	 * @param subscription the subscription
	 */
	public void addSubscription(String key, Subscription subscription) {
		SubscriptionLogger.logCreateSubscription(subscription.getId(), subscription.getSubscriptionTimeoutMillis(), subscription.getNoDataIntervalMillis());
		this.put(key, subscription);
		this.scheduleSubscriptionTimeout(subscription, subscription.getSubscriptionTimeoutMillis());
	}

	/**
	 * Removes the subscription.
	 *
	 * @param subscription the subscription
	 */
	public void removeSubscription(Subscription subscription) {
		if (subscription == null) {
			return;
		}
		synchronized (subscription) {
			// sync on subscription avoids timer schedule and removing race condition
			this.cancelSubscriptionTimeout(subscription);
			super.remove(subscription.getId());
		}
		SubscriptionLogger.logDeleteSubscription(subscription.getId());
	}

	/**
	 * Removes the subscription.
	 *
	 * @param key the key
	 */
	public void removeSubscription(String key) {
		Subscription subscription = this.getSubscription(key);
		this.removeSubscription(subscription);
	}

	/**
	 * Gets the subscription.
	 *
	 * @param key the key
	 * @return the subscription
	 */
	public Subscription getSubscription(String key) {
		Subscription subscription = super.get(key);
		return subscription;
	}

	/**
	 * Gets all subscriptions.
	 *
	 * @return the subscriptions
	 */
	public Subscription[] getSubscriptions() {
		try {
			Set<Entry<String, Subscription>> entries = this.registryMap.entrySet();
			Subscription[] subscriptions = new Subscription[entries.size()];
			int index = 0;
			for (Entry<String, Subscription> entry : entries) {
				// String key = entry.getKey();
				Subscription subscription = entry.getValue();
				subscriptions[index++] = subscription;
			}
			return subscriptions;
		} catch (Exception e) {
			LOGGER.error("getSubscriptions", e);
		}
		return null;
	}

	/**
	 * Gets all subscriptions for given service name.
	 *
	 * @param serviceName the service name
	 * @return the subscriptions
	 */
	public Subscription[] getSubscriptions(String serviceName) {
		if (serviceName == null) {
			return this.getSubscriptions();
		}
		try {
			List<Subscription> subscriptionList = new ArrayList<Subscription>();
			Set<Entry<String, Subscription>> entries = this.registryMap.entrySet();
			for (Entry<String, Subscription> entry : entries) {
				// String key = entry.getKey();
				Subscription subscription = entry.getValue();
				String subscriptionServiceName = subscription.getService().getName();
				if (subscriptionServiceName.equals(serviceName)) {
					subscriptionList.add(subscription);
				}
			}
			Subscription[] sa = new Subscription[0];
			return subscriptionList.toArray(sa);
		} catch (Exception e) {
			LOGGER.error("getSubscriptions", e);
		}
		return null;
	}

	/**
	 * Schedule subscription timeout.
	 *
	 * @param subscription the subscription
	 */
	@SuppressWarnings("unchecked")
	private void scheduleSubscriptionTimeout(Subscription subscription, double newTimeoutMillis) {
		if (subscription == null) {
			// no scheduling of Subscription timeout
			return;
		}
		// always cancel old timeout before setting up a new one
		this.cancelSubscriptionTimeout(subscription);
		// sets up subscription timeout
		TimeoutWrapper subscriptionTimeouter = new TimeoutWrapper(new SubscriptionTimeout(subscription));
		// schedule sessionTimeouter in registry timer
		ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) this.subscriptionScheduler.schedule(subscriptionTimeouter, (long) newTimeoutMillis,
				TimeUnit.MILLISECONDS);
		subscription.setTimeout(timeout);
		if (SubscriptionLogger.isTraceEnabled()) {
			SubscriptionLogger.logScheduleTimeout(subscription.getId(), newTimeoutMillis, timeout.getDelay(TimeUnit.MILLISECONDS));
		}
	}

	/**
	 * Cancel session timeout.
	 *
	 * @param subscription the session
	 */
	private void cancelSubscriptionTimeout(Subscription subscription) {
		if (subscription == null) {
			return;
		}
		ScheduledFuture<TimeoutWrapper> subscriptionTimeout = subscription.getTimeout();
		if (subscriptionTimeout == null) {
			// no subscription timeout has been set up for this subscription
			return;
		}
		if (SubscriptionLogger.isTraceEnabled()) {
			SubscriptionLogger.logCancelTimeout(subscription.getId());
		}
		boolean cancelSuccess = subscriptionTimeout.cancel(false);
		if (cancelSuccess == false) {
			LOGGER.error("cancel of subscription timeout failed sid=" + subscription.getId() + " delay=" + subscriptionTimeout.getDelay(TimeUnit.MILLISECONDS) + "ms");
			boolean remove = this.subscriptionScheduler.remove(subscription.getTimeouterTask());
			if (remove == false) {
				LOGGER.error("remove of subscription timeout failed sid=" + subscription.getId() + " delay=" + subscriptionTimeout.getDelay(TimeUnit.MILLISECONDS) + " ms");
			}
		}

		// tries removing canceled timeouts
		this.subscriptionScheduler.purge();
		// important to set timeout null - rescheduling of same instance not possible
		subscription.setTimeout(null);
	}

	/**
	 * Cancel subscription timeout.
	 *
	 * @param key the key
	 */
	public void resetSubscriptionTimeout(Subscription subscription, double newTimeoutMillis) {
		synchronized (subscription) {
			// sync on subscription avoids removing race condition
			if (this.containsKey(subscription.getId()) == false) {
				// subscription got deleted in meantime - don't schedule timer again
				return;
			}
			this.cancelSubscriptionTimeout(subscription);
			this.scheduleSubscriptionTimeout(subscription, newTimeoutMillis);
		}
	}

	/**
	 * The Class SubscriptionTimeout. Gets control when a subscription times out. Responsible for cleaning up when subscription gets broken.
	 */
	private class SubscriptionTimeout implements ITimeout {

		/** The session. */
		private Subscription subscription;

		/** The callback, callback to send abort subscription. */

		/**
		 * Instantiates a new subscription timer run.
		 *
		 * @param subscription the session
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
			 * broken subscription procedure<br />
			 * 1. remove subscription from registry<br />
			 * 2. unsubscribe (discard messages for client) subscription in queue<br />
			 * 3. abort subscription on backend server<br />
			 */
			SubscriptionRegistry.this.removeSubscription(subscription);
			IStatefulServer server = subscription.getServer();
			server.abortSession(subscription, "subscription timed out in registry");
			SubscriptionLogger.logTimeoutSubscription(subscription);
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return (int) this.subscription.getSubscriptionTimeoutMillis();
		}
	}

	/**
	 * Dump the su8bscriptions into the xml writer.
	 *
	 * @param writer the writer
	 * @throws Exception the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("subscriptions");
		writer.writeAttribute("subscriptionScheduler_poolSize", this.subscriptionScheduler.getPoolSize());
		writer.writeAttribute("subscriptionScheduler_maximumPoolSize", this.subscriptionScheduler.getMaximumPoolSize());
		writer.writeAttribute("subscriptionScheduler_corePoolSize", this.subscriptionScheduler.getCorePoolSize());
		writer.writeAttribute("subscriptionScheduler_largestPoolSize", this.subscriptionScheduler.getLargestPoolSize());
		writer.writeAttribute("subscriptionScheduler_activeCount", this.subscriptionScheduler.getActiveCount());

		Set<Entry<String, Subscription>> entries = this.registryMap.entrySet();
		for (Entry<String, Subscription> entry : entries) {
			Subscription subscriptions = entry.getValue();
			subscriptions.dump(writer);
		}
		writer.writeEndElement(); // end of subscriptions
	}
}
