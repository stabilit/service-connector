package org.serviceconnector.service;

import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.CascadedSC;

public class CascadedPublishService extends Service implements IPublishService {

	/** The subscription queue. */
	private SubscriptionQueue<SCMPMessage> subscriptionQueue;
	protected CascadedSC cascadedSC;

	public CascadedPublishService(String name, CascadedSC cascadedSC) {
		super(name, ServiceType.CASCADED_PUBLISH_SERVICE);
		this.cascadedSC = cascadedSC;
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
}
