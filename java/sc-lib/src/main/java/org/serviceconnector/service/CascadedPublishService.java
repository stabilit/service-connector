package org.serviceconnector.service;

import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.SCMPMessage;

public class CascadedPublishService extends Service {

	/** The subscription queue. */
	private SubscriptionQueue<SCMPMessage> subscriptionQueue;
	
	public CascadedPublishService(String name, ServiceType type) {
		super(name, type);
	}

	public void subscribe() {

	}
	
	/**
	 * Gets the subscription queue.
	 * 
	 * @return the subscription queue
	 */
	public SubscriptionQueue<SCMPMessage> getSubscriptionQueue() {
		return this.subscriptionQueue;
	}
}
