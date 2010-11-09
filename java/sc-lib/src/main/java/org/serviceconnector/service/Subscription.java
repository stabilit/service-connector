package org.serviceconnector.service;

import org.serviceconnector.server.StatefulServer;

public class Subscription extends AbstractSession {

	/** The mask in bytes. */
	private SubscriptionMask mask;

	/**
	 * Instantiates a new subscription.
	 * 
	 * @param mask
	 *            the mask
	 */
	public Subscription(SubscriptionMask mask, String sessionInfo, String ipAddressList) {
		super(sessionInfo, ipAddressList);
		this.mask = mask;
	}

	/**
	 * Gets the mask.
	 * 
	 * @return the mask
	 */
	public SubscriptionMask getMask() {
		return mask;
	}

	/**
	 * Sets the mask.
	 * 
	 * @param mask
	 *            the new mask
	 */
	public void setMask(SubscriptionMask mask) {
		this.mask = mask;
	}

	/** {@inheritDoc} */
	@Override
	public StatefulServer getServer() {
		return (StatefulServer) this.server;
	}
}
