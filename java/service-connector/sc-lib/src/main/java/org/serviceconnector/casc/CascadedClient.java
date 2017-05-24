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
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.casc.CscReceivePublicationCallback;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(CascadedClient.class);

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
	/** The permit denial count, counts how many times semaphore denies to give permits in time. */
	private AtomicInteger permitDenialCounter;
	/** The destroy lock object. */
	private Object destroyLock = new Object();

	/**
	 * Instantiates a new cascaded client.
	 *
	 * @param cascadedSC the cascaded sc
	 * @param publishService the publish service
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
		this.permitDenialCounter = new AtomicInteger();
	}

	/**
	 * Checks if is subscribed.
	 *
	 * @return true, if is subscribed
	 */
	public boolean isSubscribed() {
		if (this.destroyed == true) {
			LOGGER.debug("cascaded client gots destroyed before");
			// client is already destroyed
			return false;
		}
		return subscribed;
	}

	/**
	 * Sets the subscribed.
	 *
	 * @param subscribed the new subscribed
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
	 * @param subscriptionId the new subscription id
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
	 * Increment and checks semaphore permit denial counter. If the counter achieves 5 this cascaded client destroys himself. It should never appear. It under some circumstances
	 * releasing the semaphore fails it continues running fine after destroying.
	 */
	public int incrementAndCheckSemaphorePermitDenialCounter() {
		int counter = this.permitDenialCounter.incrementAndGet();

		if (counter == 5) {
			// After 5 times denial of permit something is wrong and we destroy the cascaded client.
			LOGGER.error("cascaded client got destroyed because semaphore denied giving permits 5 times in series, this operation should never appear inform STABILIT (JOT)");
			this.destroy();
		}
		return counter;
	}

	/**
	 * Reset semaphore permit denial counter.
	 */
	public void resetSemaphorePermitDenialCounter() {
		this.permitDenialCounter.set(0);
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
	 * @param newSubscriptionMask the new subscription mask
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
	 * @param clientSubscriptionId the client subscription id
	 * @param clientMask the client mask
	 * @throws InvalidMaskLengthException the invalid mask length exception
	 */
	public void addClientSubscriptionId(String clientSubscriptionId, SubscriptionMask clientMask) throws InvalidMaskLengthException {
		if (this.subscriptionMask != null && (this.subscriptionMask.getValue().length() != clientMask.getValue().length())) {
			// client mask has invalid (different than current mask of cascaded client) length
			throw new InvalidMaskLengthException("client mask has invalid length: clientSubscriptionId=" + clientSubscriptionId + " clientMask=" + clientMask);
		}
		this.clientSubscriptionIds.put(clientSubscriptionId, clientMask);
	}

	/**
	 * Removes the client subscription id.
	 *
	 * @param clientSubscriptionId the client subscription id
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
		CscReceivePublicationCallback callback = new CscReceivePublicationCallback(this);
		// OTI for receive publication: DEFAULT_OPERATION_TIMEOUT_SECONDS + NO_DATA_INTERVAL
		int oti = (Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS + this.getPublishService().getNoDataIntervalSeconds()) * Constants.SEC_TO_MILLISEC_FACTOR;
		this.cascadedSC.receivePublication(this, callback, oti, false);
	}

	/**
	 * Receive publication part - needed for polling large publication.
	 */
	public void receivePublicationPart() {
		if (this.destroyed == true) {
			// client got destroyed, stop receive publication
			return;
		}
		CscReceivePublicationCallback callback = new CscReceivePublicationCallback(this);
		// OTI for receive publication: DEFAULT_OPERATION_TIMEOUT_SECONDS + NO_DATA_INTERVAL
		int oti = (Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS + this.getPublishService().getNoDataIntervalSeconds()) * Constants.SEC_TO_MILLISEC_FACTOR;
		this.cascadedSC.receivePublication(this, callback, oti, true);
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
	 * Checks if is destroyed. Careful in use - destroy method renews the client. This method is only to use if u are still holding the same instance. On the service it might be a
	 * new cascaded client instance in the meantime.
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
	 * Destroy cascaded client. A cascaded can only be destroyed once. After destroying use of client is forbidden. The publish service gets a new instance of CascadedClient which
	 * holds current subscribers. Destroy should be called having a permit to avoid errors in proceeding subscribe/unsubscribe/changeSubscription. Destroy releases any thread
	 * waiting for a permit on semaphore. It unsubscribes the current cascaded client.
	 */
	public void destroy() {
		// synchronization avoids changing destroyed of more than one thread at the same time
		synchronized (this.destroyLock) {
			if (this.destroyed == true) {
				// cascaded client got already destroyed
				return;
			}
			this.destroyed = true;
		}
		this.publishService.renewCascadedClient();
		// release threads waiting for permits, just allow any thread to continue after destroy no one continues
		this.cascClientSemaphore.release(this.cascClientSemaphore.getQueueLength());
		LOGGER.info("cascadedClient gets destroyed service=" + this.getServiceName());

		String[] clientSubscriptionIdsArray = this.clientSubscriptionIds.keySet().toArray(new String[0]);
		for (String clientSubscriptionId : clientSubscriptionIdsArray) {
			// delete all client subscriptions
			AppContext.getSubscriptionRegistry().removeSubscription(clientSubscriptionId);
			// unsubscribe from queue
			this.publishService.getMessageQueue().unsubscribe(clientSubscriptionId);
		}
		this.publishService.getMessageQueue().removeNonreferencedNodes();
		this.cascadedSC.unsubscribeCascadedClientInErrorCases(this);
		// needs to be after unsubscribe
		this.subscribed = false;
		AppContext.getSCCache().removeManagedDataForGuardian(publishService.getName());
		this.clientSubscriptionIds.clear();
		this.publishService = null;
	}
}
