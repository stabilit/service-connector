/*
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
 */
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
