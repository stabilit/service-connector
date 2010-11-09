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
	 * @param operationTimeoutInMillis
	 *            the operation timeout in milliseconds
	 * @return the sC message
	 */
	public SCMessage subscribe(SCMessage message, int operationTimeoutInMillis) {
		return message;
	}

	/**
	 * Unsubscribe.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutInMillis
	 *            the operation timeout in milliseconds
	 */
	public void unsubscribe(SCMessage message, int operationTimeoutInMillis) {
	}

	/**
	 * Change subscription.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutInMillis
	 *            the operation timeout in milliseconds
	 * @return the sC message
	 */
	public SCMessage changeSubscription(SCMessage message, int operationTimeoutInMillis) {
		return message;
	}
}
