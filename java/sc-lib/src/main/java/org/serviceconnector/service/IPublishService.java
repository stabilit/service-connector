package org.serviceconnector.service;

import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.scmp.SCMPMessage;

public interface IPublishService {

	/**
	 * Gets the subscription queue.
	 * 
	 * @return the subscription queue
	 */
	public abstract PublishMessageQueue<SCMPMessage> getMessageQueue();
}
