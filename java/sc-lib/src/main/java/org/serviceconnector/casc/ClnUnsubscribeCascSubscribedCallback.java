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

import org.serviceconnector.cmd.sc.ClnUnsubscribeCommandCallback;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SubscriptionMask;

public class ClnUnsubscribeCascSubscribedCallback implements ISCMPMessageCallback {

	/** The request. */
	protected IRequest request;
	/** The cascaded client. */
	private CascadedClient cascClient;
	private ClnUnsubscribeCommandCallback callback;

	public ClnUnsubscribeCascSubscribedCallback(IRequest request, ClnUnsubscribeCommandCallback callback) {
		this.request = request;
		this.callback = callback;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		// change cascade client mask, removing of client subscription id is done in call
		String newMask = this.request.getMessage().getHeader(SCMPHeaderAttributeKey.CASCADED_MASK);
		this.cascClient.setSubscriptionMask(new SubscriptionMask(newMask));
		// release permit in any case
		this.cascClient.getCascClientSemaphore().release();
		// forward reply to client
		this.callback.receive(reply);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		// change cascade client mask, removing of client subscription id is done in call
		String newMask = this.request.getMessage().getHeader(SCMPHeaderAttributeKey.CASCADED_MASK);
		this.cascClient.setSubscriptionMask(new SubscriptionMask(newMask));
		// release permit in any case
		this.cascClient.getCascClientSemaphore().release();
		// forward reply to client
		this.callback.receive(ex);
	}

	public void setCascClient(CascadedClient cascClient) {
		this.cascClient = cascClient;
	}
}
