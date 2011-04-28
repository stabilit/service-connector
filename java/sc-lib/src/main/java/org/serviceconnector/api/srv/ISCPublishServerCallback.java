package org.serviceconnector.api.srv;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;

public interface ISCPublishServerCallback {

	/**
	 * Subscribe.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 * @return the sC message
	 */
	public abstract SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutMillis);

	/**
	 * Change subscription.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 * @return the sC message
	 */
	public abstract SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutMillis);

	/**
	 * Unsubscribe.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 */
	public abstract void unsubscribe(SCSubscribeMessage message, int operationTimeoutMillis);

	/**
	 * Abort subscription.
	 * 
	 * @param scMessage
	 *            the sc message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 */
	public abstract void abortSubscription(SCSubscribeMessage scMessage, int operationTimeoutMillis);

}