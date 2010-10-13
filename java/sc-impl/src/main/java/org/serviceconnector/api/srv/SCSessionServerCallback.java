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
	public SCMessage createSession(SCMessage message) {
		return null;
	}

	/**
	 * Delete session.
	 * 
	 * @param message
	 *            the message
	 */
	public void deleteSession(SCMessage message) {
	}

	/**
	 * Abort session.
	 * 
	 * @param message
	 *            the message
	 */
	public void abortSession(SCMessage message) {
	}

	/**
	 * Execute.
	 * 
	 * @param message
	 *            the message
	 * @return the sC message
	 */
	public abstract SCMessage execute(SCMessage message);
}
