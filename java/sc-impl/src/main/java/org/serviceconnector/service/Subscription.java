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
	public Subscription(SubscriptionMask mask) {
		super();
		this.mask = mask;
	}

	public SubscriptionMask getMask() {
		return mask;
	}
}
