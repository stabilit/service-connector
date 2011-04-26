package org.serviceconnector.api.srv;

import org.serviceconnector.api.SCMessage;

public interface ISCSessionServerCallback {

	/**
	 * Creates the session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 * @return the sC message
	 */
	public abstract SCMessage createSession(SCMessage message, int operationTimeoutMillis);

	/**
	 * Delete session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 */
	public abstract void deleteSession(SCMessage message, int operationTimeoutMillis);

	/**
	 * Abort session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 */
	public abstract void abortSession(SCMessage message, int operationTimeoutMillis);

	/**
	 * Execute.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 * @return the sC message
	 */
	public abstract SCMessage execute(SCMessage message, int operationTimeoutMillis);

}