package org.serviceconnector.service;

import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.SCMPMessage;

public interface IPublishService {

	/**
	 * Gets the subscription queue.
	 * 
	 * @return the subscription queue
	 */
	public abstract SubscriptionQueue<SCMPMessage> getSubscriptionQueue();
}
