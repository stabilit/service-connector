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
package org.serviceconnector.cmd.casc;

import org.serviceconnector.casc.CascadedClient;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.ISubscriptionCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SubscriptionMask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CascSCUnsubscribeCallback.
 */
public class CscUnsubscribeCallbackActiveCascClient implements ISCMPMessageCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CscUnsubscribeCallbackActiveCascClient.class);
	/** The cascaded client. */
	private CascadedClient cascClient;
	/** The command callback. */
	private ISubscriptionCallback commandCallback;
	/** The request. */
	protected IRequest request;

	/**
	 * Instantiates a new csc unsubscribe callback active casc client.
	 *
	 * @param cascClient the casc client
	 * @param callback the callback
	 */
	public CscUnsubscribeCallbackActiveCascClient(CascadedClient cascClient, ISubscriptionCallback callback) {
		this.cascClient = cascClient;
		this.commandCallback = callback;
		this.request = callback.getRequest();
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		SCMPMessage reqMessage = request.getMessage();
		// only if service cascaded update cascaded client with new subscription mask
		String newMask = reqMessage.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK);
		this.cascClient.setSubscriptionMask(new SubscriptionMask(newMask));
		// only if service is cascaded - release permit
		this.cascClient.getCascClientSemaphore().release();
		try {
			this.commandCallback.receive(reply);
		} catch (Exception e) {
			this.commandCallback.receive(e);
		}
	}

	@Override
	public void receive(Exception ex) {
		SCMPMessage reqMessage = this.request.getMessage();
		String sid = reqMessage.getSessionId();
		LOGGER.warn("receive exception sid=" + sid + " " + ex.toString());
		// release permit
		this.cascClient.getCascClientSemaphore().release();
		// forward reply to client
		this.commandCallback.receive(ex);
	}
}
