package org.serviceconnector.casc;

import org.apache.log4j.Logger;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;

public class CascReceivePublicationCallback implements ISCMPMessageCallback {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CascReceivePublicationCallback.class);

	private CascadedClient cascClient;

	public CascReceivePublicationCallback(CascadedClient cascClient) {
		this.cascClient = cascClient;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) throws Exception {
		// 3. receiving reply and error handling
		if (this.cascClient.isSubscribed() == false) {
			logger.debug("receive publication for cascaded client which is not subscribed anymore service="
					+ cascClient.getServiceName());
			// cascaded client is not subscribed anymore - stop continuing
			return;
		}
		if (reply.isFault()) {
			// operation failed
			logger.error("receive publication failed for cascaded client (set to be unsubscribed) service="
					+ cascClient.getServiceName());
			this.cascClient.destroy();
			return;
		}
		// 4. post process, reply to client
		boolean noData = reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
		if (noData == false) {
			// message received,insert in queue
			logger.debug("receive publication for cascaded client put message in queue service=" + cascClient.getServiceName());
			SubscriptionQueue<SCMPMessage> subscriptionQueue = this.cascClient.getPublishService().getSubscriptionQueue();
			subscriptionQueue.insert(reply);
		}
		// send next receive publication
		this.cascClient.receivePublication();
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		this.cascClient.destroy();
	}
}
