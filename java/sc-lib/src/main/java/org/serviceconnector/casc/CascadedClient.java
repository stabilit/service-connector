package org.serviceconnector.casc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.service.CascadedPublishService;
import org.serviceconnector.service.SubscriptionMask;

public class CascadedClient {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CascadedClient.class);

	private boolean subscribed;
	private String subscriptionId;
	/** The cascaded client semaphore. */
	private Semaphore cascClientSemaphore;

	private SubscriptionMask subscriptionMask;

	private List<String> clientSubscriptionIds;

	private CascadedSC cascadedSC;

	private CascadedPublishService publishService;

	private boolean destroyed;

	public CascadedClient(CascadedSC cascadedSC, CascadedPublishService publishService) {
		this.subscribed = false;
		this.subscriptionId = null;
		// binary semaphore, has two states: one permit available or zero permits, mutual exclusion lock, works as FIFO
		this.cascClientSemaphore = new Semaphore(1, true);
		this.clientSubscriptionIds = new ArrayList<String>();
		this.cascadedSC = cascadedSC;
		this.publishService = publishService;
		this.destroyed = false;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public synchronized void setSubscribed(boolean subscribed) {
		if (this.destroyed == true) {
			logger.warn("cascaded client can not be set subscribed it gots destroyed bevor");
			// client is already destroyed
			throw new RuntimeException("cascaded client already destroyed.");
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

	public SubscriptionMask getSubscriptionMask() {
		return subscriptionMask;
	}

	public String getServiceName() {
		return publishService.getName();
	}

	public CascadedPublishService getPublishService() {
		return this.publishService;
	}

	public List<String> getClientSubscriptionIds() {
		return clientSubscriptionIds;
	}

	public void addClientSubscriptionId(String clientSubscriptionId) {
		this.clientSubscriptionIds.add(clientSubscriptionId);
	}

	public void removeClientSubscriptionId(String clientSubscriptionId) {
		this.clientSubscriptionIds.remove(clientSubscriptionId);
	}

	public void receivePublication() {
		CascReceivePublicationCallback callback = new CascReceivePublicationCallback(this);
		// OTI for receive publication: DEFAULT_OPERATION_TIMEOUT_SECONDS + NO_DATA_INTERVAL
		int oti = (Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS + this.getPublishService().getNoDataIntervalInSeconds())
				* Constants.SEC_TO_MILLISEC_FACTOR;
		this.cascadedSC.receivePublication(this.publishService.getName(), this.subscriptionId, callback, oti);
	}

	public synchronized void destroy() {
		logger.warn("cascadedClient gets destroyed service=" + this.getServiceName());
		this.destroyed = true;
		this.subscribed = false;
		this.clientSubscriptionIds.clear();
		this.publishService.renewCascadedClient();
		this.publishService = null;
		this.cascadedSC = null;
		this.cascClientSemaphore = null;
	}
}
