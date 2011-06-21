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
package org.serviceconnector.casc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.casc.CascReceivePublicationCallback;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.service.CascadedPublishService;
import org.serviceconnector.service.InvalidMaskLengthException;
import org.serviceconnector.service.SubscriptionMask;

/**
 * The Class CascadedClient.
 */
public class CascadedClient {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CascadedClient.class);

	/** The subscribed. */
	private boolean subscribed;
	/** The subscription id. */
	private String subscriptionId;
	/** The cascaded client semaphore. */
	private Semaphore cascClientSemaphore;
	/** The subscription mask. */
	private SubscriptionMask subscriptionMask;
	/** The client subscription ids. */
	private Map<String, SubscriptionMask> clientSubscriptionIds;
	/** The cascaded sc. */
	private CascadedSC cascadedSC;
	/** The publish service. */
	private CascadedPublishService publishService;
	/** The destroyed. */
	private boolean destroyed;
	/** The service name. */
	private String serviceName;
	/** The msg sequence nr. */
	private SCMPMessageSequenceNr msgSequenceNr;

	/**
	 * Instantiates a new cascaded client.
	 * 
	 * @param cascadedSC
	 *            the cascaded sc
	 * @param publishService
	 *            the publish service
	 */
	public CascadedClient(CascadedSC cascadedSC, CascadedPublishService publishService) {
		this.subscribed = false;
		this.subscriptionId = null;
		// binary semaphore, has two states: one permit available or zero permits, mutual exclusion lock, works as FIFO
		this.cascClientSemaphore = new Semaphore(1, true);
		this.cascadedSC = cascadedSC;
		this.publishService = publishService;
		this.destroyed = false;
		this.serviceName = this.publishService.getName();
		this.subscriptionMask = null;
		this.clientSubscriptionIds = new HashMap<String, SubscriptionMask>();
		this.msgSequenceNr = new SCMPMessageSequenceNr();
	}

	/**
	 * Checks if is subscribed.
	 * 
	 * @return true, if is subscribed
	 */
	public boolean isSubscribed() {
		if (this.destroyed == true) {
			LOGGER.warn("cascaded client gots destroyed before");
			// client is already destroyed
			return false;
		}
		return subscribed;
	}

	/**
	 * Sets the subscribed.
	 * 
	 * @param subscribed
	 *            the new subscribed
	 */
	public void setSubscribed(boolean subscribed) {
		if (this.destroyed == true) {
			LOGGER.warn("cascaded client can not be set subscribed it gots destroyed before");
			// client is already destroyed
			this.subscribed = false;
		}
		this.subscribed = subscribed;
	}

	/**
	 * Sets the subscription id.
	 * 
	 * @param subscriptionId
	 *            the new subscription id
	 */
	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	/**
	 * Gets the subscription id.
	 * 
	 * @return the subscription id
	 */
	public String getSubscriptionId() {
		return subscriptionId;
	}

	/**
	 * Gets the casc client semaphore.
	 * 
	 * @return the casc client semaphore
	 */
	public Semaphore getCascClientSemaphore() {
		return this.cascClientSemaphore;
	}

	/**
	 * Sets the subscription mask.
	 * 
	 * @param newSubscriptionMask
	 *            the new subscription mask
	 */
	public void setSubscriptionMask(SubscriptionMask newSubscriptionMask) {
		this.subscriptionMask = newSubscriptionMask;
	}

	/**
	 * Eval subscription mask from client subscriptions.
	 * 
	 * @return the string
	 */
	public String evalSubscriptionMaskFromClientSubscriptions() {
		byte[] baseMask = null;
		for (SubscriptionMask clnMask : clientSubscriptionIds.values()) {
			String maskString = clnMask.getValue();
			byte[] mask = maskString.getBytes();
			if (baseMask == null) {
				baseMask = mask;
				continue;
			}
			try {
				baseMask = SubscriptionMask.masking(baseMask, mask);
			} catch (InvalidMaskLengthException e) {
				// exception should never occur, cascaded client should not allowed client with different mask lengths
				LOGGER.error("Masking of client masks for cascaded client failed", e);
			}
		}
		return new String(baseMask);
	}

	/**
	 * Gets the subscription mask.
	 * 
	 * @return the subscription mask
	 */
	public SubscriptionMask getSubscriptionMask() {
		return subscriptionMask;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return this.serviceName;
	}

	/**
	 * Gets the publish service.
	 * 
	 * @return the publish service
	 */
	public CascadedPublishService getPublishService() {
		return this.publishService;
	}

	/**
	 * Gets the client subscription ids.
	 * 
	 * @return the client subscription ids
	 */
	public Map<String, SubscriptionMask> getClientSubscriptionIds() {
		return clientSubscriptionIds;
	}

	/**
	 * Adds the client subscription id.
	 * 
	 * @param clientSubscriptionId
	 *            the client subscription id
	 * @param clientMask
	 *            the client mask
	 * @throws InvalidMaskLengthException
	 *             the invalid mask length exception
	 */
	public void addClientSubscriptionId(String clientSubscriptionId, SubscriptionMask clientMask) throws InvalidMaskLengthException {
		if (this.subscriptionMask != null && (this.subscriptionMask.getValue().length() != clientMask.getValue().length())) {
			// client mask has invalid (different than current mask of cascaded client) length
			throw new InvalidMaskLengthException("client mask has invalid length: clientSubscriptionId=" + clientSubscriptionId
					+ " clientMask=" + clientMask);
		}
		this.clientSubscriptionIds.put(clientSubscriptionId, clientMask);
	}

	/**
	 * Removes the client subscription id.
	 * 
	 * @param clientSubscriptionId
	 *            the client subscription id
	 */
	public void removeClientSubscriptionId(String clientSubscriptionId) {
		this.clientSubscriptionIds.remove(clientSubscriptionId);
	}

	/**
	 * Receive publication.
	 */
	public void receivePublication() {
		if (this.destroyed == true) {
			// client got destroyed, stop receive publication
			return;
		}
		CascReceivePublicationCallback callback = new CascReceivePublicationCallback(this);
		// OTI for receive publication: DEFAULT_OPERATION_TIMEOUT_SECONDS + NO_DATA_INTERVAL
		int oti = (Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS + this.getPublishService().getNoDataIntervalSeconds())
				* Constants.SEC_TO_MILLISEC_FACTOR;
		this.cascadedSC.receivePublication(this, callback, oti);
	}

	/**
	 * Gets the cascaded sc.
	 * 
	 * @return the cascaded sc
	 */
	public CascadedSC getCascadedSC() {
		return cascadedSC;
	}

	/**
	 * Checks if is destroyed.
	 * 
	 * @return true, if is destroyed
	 */
	public boolean isDestroyed() {
		return destroyed;
	}

	/**
	 * Gets the msg sequence nr.
	 * 
	 * @return the msg sequence nr
	 */
	public SCMPMessageSequenceNr getMsgSequenceNr() {
		return this.msgSequenceNr;
	}

	/**
	 * Destroy cascaded client. A cascaded can only be destroyed once. After destroying use of client is forbidden. The publish
	 * service gets a new instance of CascadedClient which holds current subscribers. Destroy should be called having a permit to
	 * avoid errors in proceeding subscribe/unsubscribe/changeSubscription. Destroy releases any thread waiting for a permit on
	 * semaphore.
	 */
	public void destroy() {
		if (this.destroyed == true) {
			// cascaded client got already destroyed
			return;
		}
		LOGGER.warn("cascadedClient gets destroyed service=" + this.getServiceName());
		this.destroyed = true;
		this.subscribed = false;
		for (String clientSubscriptionId : this.clientSubscriptionIds.keySet()) {
			// delete all client subscriptions
			AppContext.getSubscriptionRegistry().removeSubscription(clientSubscriptionId);
			// unsubscribe from queue
			this.publishService.getMessageQueue().unsubscribe(clientSubscriptionId);
		}
		this.publishService.getMessageQueue().removeNonreferencedNodes();
		// release threads waiting for permits, just allow any thread to continue
		this.cascClientSemaphore.release(Integer.MAX_VALUE);
		this.publishService.renewCascadedClient();
		this.clientSubscriptionIds.clear();
		this.publishService = null;
	}
}