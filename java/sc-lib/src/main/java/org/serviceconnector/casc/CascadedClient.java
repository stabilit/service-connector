package org.serviceconnector.casc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.casc.CascReceivePublicationCallback;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.service.CascadedPublishService;
import org.serviceconnector.service.InvalidMaskLengthException;
import org.serviceconnector.service.SubscriptionMask;

public class CascadedClient {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(CascadedClient.class);

	private boolean subscribed;
	private String subscriptionId;
	/** The cascaded client semaphore. */
	private Semaphore cascClientSemaphore;

	private SubscriptionMask subscriptionMask;

	private Map<String, SubscriptionMask> clientSubscriptionIds;

	private CascadedSC cascadedSC;

	private CascadedPublishService publishService;

	private boolean destroyed;

	private String serviceName;

	private SCMPMessageSequenceNr msgSequenceNr;

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

	public boolean isSubscribed() {
		if (this.destroyed == true) {
			LOGGER.warn("cascaded client gots destroyed before");
			// client is already destroyed
			return false;
		}
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		if (this.destroyed == true) {
			LOGGER.warn("cascaded client can not be set subscribed it gots destroyed before");
			// client is already destroyed
			this.subscribed = false;
		}
		this.subscribed = subscribed;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public Semaphore getCascClientSemaphore() {
		return this.cascClientSemaphore;
	}

	public void setSubscriptionMask(SubscriptionMask newSubscriptionMask) {
		this.subscriptionMask = newSubscriptionMask;
	}

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

	public SubscriptionMask getSubscriptionMask() {
		return subscriptionMask;
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public CascadedPublishService getPublishService() {
		return this.publishService;
	}

	public Map<String, SubscriptionMask> getClientSubscriptionIds() {
		return clientSubscriptionIds;
	}

	public void addClientSubscriptionId(String clientSubscriptionId, SubscriptionMask clientMask) throws InvalidMaskLengthException {
		if (this.subscriptionMask != null && (this.subscriptionMask.getValue().length() != clientMask.getValue().length())) {
			// client mask has invalid (different than current mask of cascaded client) length
			throw new InvalidMaskLengthException("client mask has invalid length: clientSubscriptionId=" + clientSubscriptionId
					+ " clientMask=" + clientMask);
		}
		this.clientSubscriptionIds.put(clientSubscriptionId, clientMask);
	}

	public void removeClientSubscriptionId(String clientSubscriptionId) {
		this.clientSubscriptionIds.remove(clientSubscriptionId);
	}

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

	public CascadedSC getCascadedSC() {
		return cascadedSC;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

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
		// release threads waiting for permits, just allow any thread to continue
		this.cascClientSemaphore.release(Integer.MAX_VALUE);
		this.publishService.renewCascadedClient(this.clientSubscriptionIds);
		this.clientSubscriptionIds.clear();
		this.publishService = null;
	}
}