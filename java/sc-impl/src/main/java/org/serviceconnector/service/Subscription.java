package org.serviceconnector.service;


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

	public SubscriptionMask getMask() {
		return mask;
	}

	public void setMask(SubscriptionMask mask) {
		this.mask = mask;
	}
}
