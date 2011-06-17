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

import org.apache.log4j.Logger;
import org.serviceconnector.casc.CascadedClient;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.ISubscriptionCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;

/**
 * The Class CscSubscribeActiveCascClientCallback.
 */
public class CscSubscribeActiveCascClientCallback implements ISCMPMessageCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CscSubscribeActiveCascClientCallback.class);

	/** The request. */
	protected IRequest request;
	/** The cascaded client. */
	private CascadedClient cascClient;

	/** The command callback. */
	private ISubscriptionCallback commandCallback;

	/**
	 * Instantiates a new csc subscribe active casc client callback.
	 * 
	 * @param cascClient
	 *            the casc client
	 * @param request
	 *            the request
	 * @param callback
	 *            the callback
	 */
	public CscSubscribeActiveCascClientCallback(CascadedClient cascClient, IRequest request, ISubscriptionCallback callback) {
		this.request = request;
		this.commandCallback = callback;
		this.cascClient = cascClient;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		boolean rejectSubscriptionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
		if (reply.isFault() == false && rejectSubscriptionFlag == false) {
			// subscription successfully created
			Subscription cscScSubscription = this.commandCallback.getSubscription();
			try {
				// forward reply to client
				this.commandCallback.receive(reply);
				// adding client subscription id to cascaded client
				this.cascClient.addClientSubscriptionId(cscScSubscription.getId(), cscScSubscription.getMask());
				this.cascClient.setSubscriptionMask(new SubscriptionMask(this.request.getMessage().getHeader(
						SCMPHeaderAttributeKey.CASCADED_MASK)));
				// release permit
				this.cascClient.getCascClientSemaphore().release();
				return;
			} catch (Exception e) {
				// release permit
				this.cascClient.getCascClientSemaphore().release();
				this.commandCallback.receive(e);
				return;
			}
		}
		// release permit
		this.cascClient.getCascClientSemaphore().release();
		try {
			this.commandCallback.receive(reply);
		} catch (Exception e) {
			LOGGER.warn("receive rejected or fault reply failed, sid=" + this.request.getMessage().getSessionId(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		SCMPMessage reqMessage = this.request.getMessage();
		String sid = reqMessage.getSessionId();
		LOGGER.warn("receive exception sid=" + sid, ex);
		// release permit
		this.cascClient.getCascClientSemaphore().release();
		// forward reply to client
		this.commandCallback.receive(ex);
	}
}
