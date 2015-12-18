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
import org.serviceconnector.api.SCSubscribeMessage;

/**
 * The Class SCPublishServerCallback. Abstract class provides basic functions for a publish server callback.
 * 
 * @author JTraber
 */
public abstract class SCPublishServerCallback implements ISCPublishServerCallback {

	/** The SC publish server. */
	protected SCPublishServer scPublishServer = null;

	/**
	 * Instantiates a new SCPublishServerCallback.
	 * 
	 * @param scPublishServer
	 *            the server
	 */
	public SCPublishServerCallback(SCPublishServer scPublishServer) {
		this.scPublishServer = scPublishServer;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCPublishServerCallback#subscribe(org.serviceconnector.api.SCSubscribeMessage, int)
	 */
	@Override
	public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
		return message;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCPublishServerCallback#changeSubscription(org.serviceconnector.api.SCSubscribeMessage, int)
	 */
	@Override
	public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutMillis) {
		return message;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCPublishServerCallback#unsubscribe(org.serviceconnector.api.SCSubscribeMessage, int)
	 */
	@Override
	public void unsubscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCPublishServerCallback#abortSubscription(org.serviceconnector.api.SCSubscribeMessage, int)
	 */
	@Override
	public void abortSubscription(SCSubscribeMessage scMessage, int operationTimeoutMillis) {
	}
}
