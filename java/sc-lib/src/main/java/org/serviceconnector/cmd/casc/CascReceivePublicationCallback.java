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
package org.serviceconnector.cmd.casc;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.casc.CascadedClient;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.CascadedSC;

/**
 * The Class CascReceivePublicationCallback.
 */
public class CascReceivePublicationCallback implements ISCMPMessageCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CascReceivePublicationCallback.class);

	/** The cascaded client. */
	private CascadedClient cascClient;

	/**
	 * Instantiates a new cascaded receive publication callback.
	 * 
	 * @param cascClient
	 *            the cascaded client
	 */
	public CascReceivePublicationCallback(CascadedClient cascClient) {
		this.cascClient = cascClient;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) throws Exception {
		if (this.cascClient.isDestroyed() == true) {
			// cascaded client already destroyed ignore reply
			return;
		}
		CascadedSC cascSC = this.cascClient.getCascadedSC();
		if (cascSC.tryAcquirePermitOnCascClientSemaphore(cascClient, Constants.WAIT_FOR_PERMIT_IN_RECEIVE_PUBLICATION_MILLIS, this) == false) {
			// could not get permit to process - response done inside method
			return;
		}
		String sid = reply.getSessionId();
		// try catch block to assure releasing permit in case if any error - very important!
		try {
			// got permit to continue
			// 3. receiving reply and error handling
			if (this.cascClient.isSubscribed() == false) {
				LOGGER.debug("receive publication for cascaded client which is not subscribed anymore service="
						+ cascClient.getServiceName() + " sid=" + sid);
				// cascaded client is not subscribed anymore - stop continuing
				return;
			}
			if (reply.isFault()) {
				// operation failed
				LOGGER.warn("receive publication failed for cascaded client (set to be unsubscribed) service="
						+ cascClient.getServiceName() + " sid=" + sid);
				this.cascClient.destroy();
				return;
			}
			// 4. post process, reply to client
			boolean noData = reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
			if (noData == false) {
				// message received,insert in queue
				LOGGER.debug("receive publication for cascaded client put message in queue service=" + cascClient.getServiceName()
						+ " sid=" + sid);
				PublishMessageQueue<SCMPMessage> publishMessageQueue = this.cascClient.getPublishService().getMessageQueue();
				publishMessageQueue.insert(reply);
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
		LOGGER.warn(ex + " sid=" + this.cascClient.getSubscriptionId() + " service=" + cascClient.getServiceName());
		if (this.cascClient.isDestroyed() == true) {
			// cascaded client already destroyed ignore exception
			return;
		}
		// destroy cascaded client, without having a permit, emergency!
		this.cascClient.destroy();
	}
}
