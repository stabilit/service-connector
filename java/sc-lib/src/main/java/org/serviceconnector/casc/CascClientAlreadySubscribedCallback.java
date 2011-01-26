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
package org.serviceconnector.casc;

import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SubscriptionMask;

public class CascClientAlreadySubscribedCallback implements ISCMPMessageCallback {

	/** The cascaded client semaphore. */
	private CascadedClient cascClient;
	private ISCMPMessageCallback callback;
	private SubscriptionMask changedMask;

	public CascClientAlreadySubscribedCallback(ISCMPMessageCallback callback, SubscriptionMask changeMask) {
		this.callback = callback;
		this.changedMask = changeMask;
	}

	@Override
	public void receive(SCMPMessage reply) {
		try {
			// forward reply to client
			this.callback.receive(reply);
			this.cascClient.setSubscriptionMask(this.changedMask);
			// release permit
			this.cascClient.getCascClientSemaphore().release();
		} catch (Exception e) {
			// release permit
			this.cascClient.getCascClientSemaphore().release();
			this.callback.receive(e);
		}
	}

	@Override
	public void receive(Exception ex) {
		// release permit
		this.cascClient.getCascClientSemaphore().release();
		// forward reply to client
		this.callback.receive(ex);
	}

	public void setCascClient(CascadedClient cascClient) {
		this.cascClient = cascClient;
	}
}
