/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.api.srv;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
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

	/**
	 * Exception caught.
	 * 
	 * @param ex
	 *            the ex
	 */
	public abstract void exceptionCaught(SCServiceException ex);
}