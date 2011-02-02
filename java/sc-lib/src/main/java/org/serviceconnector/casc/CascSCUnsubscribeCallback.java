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

import org.serviceconnector.cmd.casc.ClnCommandCascCallback;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;

/**
 * The Class CascSCUnsubscribeCallback.
 */
public class CascSCUnsubscribeCallback extends ClnCommandCascCallback implements ISubscriptionCallback {

	/** The cascaded client. */
	private CascadedClient cascClient;
	/** The subscription. */
	private Subscription cascSCSubscription;

	public CascSCUnsubscribeCallback(IRequest request, IResponse response, IResponderCallback callback,
			Subscription cascSCSubscription) {
		super(request, response, callback);
		this.cascSCSubscription = cascSCSubscription;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();

		if (this.cascClient != null) {
			// only if service cascaded update cascaded client with new subscription mask
			String newMask = reqMessage.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK);
			this.cascClient.setSubscriptionMask(new SubscriptionMask(newMask));
			// only if service is cascaded - release permit
			this.cascClient.getCascClientSemaphore().release();
		}
		// forward reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CLN_UNSUBSCRIBE);
		response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	public void setCascClient(CascadedClient cascClient) {
		this.cascClient = cascClient;
	}

	/** {@inheritDoc} */
	@Override
	public Subscription getSubscription() {
		return this.cascSCSubscription;
	}
}
