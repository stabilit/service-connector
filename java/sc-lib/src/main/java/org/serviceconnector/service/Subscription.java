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
package org.serviceconnector.service;

import org.serviceconnector.server.IStatefulServer;

public class Subscription extends AbstractSession {

	/** The mask in bytes. */
	private SubscriptionMask mask;
	/** The no data interval. */
	private int noDataInterval = 0;
	/** The subscription timeout seconds. */
	private double subscriptionTimeoutMillis;

	/**
	 * Instantiates a new subscription.
	 * 
	 * @param mask
	 *            the mask
	 */
	public Subscription(SubscriptionMask mask, String sessionInfo, String ipAddressList, int noDataInterval,
			double subscriptionTimeoutMillis) {
		super(sessionInfo, ipAddressList);
		this.mask = mask;
		this.noDataInterval = noDataInterval;
		this.subscriptionTimeoutMillis = subscriptionTimeoutMillis;
	}

	/**
	 * Gets the mask.
	 * 
	 * @return the mask
	 */
	public SubscriptionMask getMask() {
		return mask;
	}

	/**
	 * Sets the mask.
	 * 
	 * @param mask
	 *            the new mask
	 */
	public void setMask(SubscriptionMask mask) {
		this.mask = mask;
	}

	public double getSubscriptionTimeoutMillis() {
		return this.subscriptionTimeoutMillis;
	}

	/** {@inheritDoc} */
	@Override
	public IStatefulServer getServer() {
		return (IStatefulServer) this.server;
	}

	/**
	 * Gets the no data interval.
	 * 
	 * @return the no data interval
	 */
	public int getNoDataInterval() {
		return noDataInterval;
	}
}
