package org.serviceconnector.api.srv;

import org.serviceconnector.api.SCMessage;

/**
 * The Class SCPublishServerCallback.
 * 
 * @author JTraber
 */
public class SCPublishServerCallback {

	/**
	 * Subscribe.
	 * 
	 * @param message
	 *            the message
	 * @return the sC message
	 */
	public SCMessage subscribe(SCMessage message) {
		return null;
	}

	/**
	 * Unsubscribe.
	 * 
	 * @param message
	 *            the message
	 */
	public void unsubscribe(SCMessage message) {
	}

	/**
	 * Change subscription.
	 * 
	 * @param message
	 *            the message
	 * @return the sC message
	 */
	public SCMessage changeSubscription(SCMessage message) {
		return null;
	}
}
