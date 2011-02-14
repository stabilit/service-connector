package org.serviceconnector.casc;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.CascadedSC;

public class CascReceivePublicationCallback implements ISCMPMessageCallback {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(CascReceivePublicationCallback.class);

	private CascadedClient cascClient;

	public CascReceivePublicationCallback(CascadedClient cascClient) {
		this.cascClient = cascClient;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) throws Exception {
		CascadedSC cascSC = this.cascClient.getCascadedSC();
		if (cascSC.tryAcquirePermitOnCascClientSemaphore(cascClient, Constants.WAIT_FOR_PERMIT_IN_RECEIVE_PUBLICATION_MILLIS, this) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		// try catch block to assure releasing permit in case if any error - very important!
		try {
			// got permit to continue
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
				this.cascClient.getCascadedSC().unsubscribeCascadedClientInErrorCases(this.cascClient);
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
			// release permit
			this.cascClient.getCascClientSemaphore().release();
		} catch (Exception e) {
			// release permit
			this.cascClient.getCascClientSemaphore().release();
			throw e;
		}
		// send next receive publication
		this.cascClient.receivePublication();
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		if (this.cascClient.isDestroyed() == true) {
			// cascaded client already destroyed ignore exception
			return;
		}
		logger.error(ex);
		this.cascClient.getCascadedSC().unsubscribeCascadedClientInErrorCases(this.cascClient);
		// destroy cascaded client, without having a permit!
		this.cascClient.destroy();
	}
}
