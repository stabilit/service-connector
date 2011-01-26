package org.serviceconnector.casc;

import java.util.concurrent.Semaphore;

import org.serviceconnector.service.SubscriptionMask;

public class CascadedClient {

	private boolean subscribed;
	private String subscriptionId;
	/** The cascaded client semaphore. */
	private Semaphore cascClientSemaphore;

	private SubscriptionMask subscriptionMask;

	public CascadedClient() {
		this.subscribed = false;
		this.subscriptionId = null;
		// binary semaphore, has two states: one permit available or zero permits, mutual exclusion lock, works as FIFO
		this.cascClientSemaphore = new Semaphore(1, true);
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
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
}
