package org.serviceconnector.api.srv;

import org.serviceconnector.api.SCMessage;

/**
 * The Class SCSessionServerCallback.
 */
public abstract class SCSessionServerCallback {

	/**
	 * Creates the session.
	 * 
	 * @param message
	 *            the message
	 * @return the sC message
	 */
	public SCMessage createSession(SCMessage message, int operationTimeoutInMillis) {
		return message;
	}

	/**
	 * Delete session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutInMillis
	 *            the operation timeout in milliseconds
	 */
	public void deleteSession(SCMessage message, int operationTimeoutInMillis) {
	}

	/**
	 * Abort session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutInMillis TODO
	 */
	public void abortSession(SCMessage message, int operationTimeoutInMillis) {
	}

	/**
	 * Execute.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutInMillis
	 *            the operation timeout in milliseconds
	 * @return the sC message
	 */
	public abstract SCMessage execute(SCMessage message, int operationTimeoutInMillis);
}
