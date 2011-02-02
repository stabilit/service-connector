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

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.serviceconnector.casc.CascadedClient;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.CascadedSC;

public class CascadedPublishService extends Service implements IPublishService {
	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(CascadedPublishService.class);
	/** The subscription queue. */
	private SubscriptionQueue<SCMPMessage> subscriptionQueue;
	/** The cascaded sc. */
	protected CascadedSC cascadedSC;
	/** The cascaded client. */
	private CascadedClient cascClient;

	private int noDataIntervalSeconds = 0;

	public CascadedPublishService(String name, CascadedSC cascadedSC, int noDataIntervalSeconds) {
		super(name, ServiceType.CASCADED_PUBLISH_SERVICE);
		this.cascadedSC = cascadedSC;
		this.cascClient = new CascadedClient(cascadedSC, this);
		this.noDataIntervalSeconds = noDataIntervalSeconds;
		this.subscriptionQueue = new SubscriptionQueue<SCMPMessage>();
	}

	public SubscriptionQueue<SCMPMessage> getSubscriptionQueue() {
		return this.subscriptionQueue;
	}

	public void setCascadedSC(CascadedSC cascadedSC) {
		this.cascadedSC = cascadedSC;
	}

	public CascadedSC getCascadedSC() {
		return cascadedSC;
	}

	public CascadedClient getCascClient() {
		return this.cascClient;
	}

	public int getNoDataIntervalSeconds() {
		return noDataIntervalSeconds;
	}

	public void renewCascadedClient(Map<String, SubscriptionMask> clientSubscriptionIds) {
		logger.warn("cascaded publish service renew cascaded client service=" + this.getName());
		this.cascClient = new CascadedClient(cascadedSC, this);
		if (clientSubscriptionIds != null && clientSubscriptionIds.size() > 0) {
			// need to hand over client subscription ID's to the new cascaded client
			Set<Entry<String, SubscriptionMask>> subscriptionSet = clientSubscriptionIds.entrySet();
			for (Entry<String, SubscriptionMask> entry : subscriptionSet) {
				this.cascClient.addClientSubscriptionId(entry.getKey(), entry.getValue());
			}
			SubscriptionMask mask = new SubscriptionMask(this.cascClient.evalSubscriptionMaskFromClientSubscriptions());
			this.cascClient.setSubscriptionMask(mask);
		}
	}
}
