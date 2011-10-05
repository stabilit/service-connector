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
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.ISubscriptionCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;

/**
 * The Class CscSubscribeInactiveCascClientCallback.
 */
public class CscSubscribeInactiveCascClientCallback implements ISCMPMessageCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CscSubscribeInactiveCascClientCallback.class);
	/** The command callback. */
	private ISubscriptionCallback commandCallback;
	/** The cascaded client. */
	private CascadedClient cascClient;
	/** The temporary cascaded mask. */
	private String tmpCscMask;

	/**
	 * Instantiates a new csc subscribe inactive casc client callback.
	 * 
	 * @param commandCallback
	 *            the command callback
	 * @param cascClient
	 *            the casc client
	 * @param tmpCscMask
	 *            the tmp csc mask
	 */
	public CscSubscribeInactiveCascClientCallback(ISubscriptionCallback commandCallback, CascadedClient cascClient,
			String tmpCscMask) {
		this.commandCallback = commandCallback;
		this.cascClient = cascClient;
		this.tmpCscMask = tmpCscMask;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		boolean rejectSubscriptionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
		if (reply.isFault() == false && rejectSubscriptionFlag == false) {
			Subscription cscScSubscription = this.commandCallback.getSubscription();
			try {
				// needs to be done before, reply changes in receive
				this.cascClient.setSubscriptionId(reply.getSessionId());
				// forward reply to client
				this.commandCallback.receive(reply);
				// subscription successfully created
				this.cascClient.setSubscribed(true);
				this.cascClient.setSubscriptionMask(new SubscriptionMask(tmpCscMask));
				this.cascClient.receivePublication();
				// adding client subscription id to cascaded client
				this.cascClient.addClientSubscriptionId(cscScSubscription.getId(), cscScSubscription.getMask());
				if (cscScSubscription.isCascaded() == true) {
					// update csc subscription id list for cascaded subscription
					cscScSubscription.addCscSubscriptionId(this.commandCallback.getRequest().getMessage().getSessionId(),
							new SubscriptionMask(this.tmpCscMask));
				}
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
			LOGGER.warn("receive rejected or fault reply failed " + e.toString());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		LOGGER.warn(ex);
		// release permit
		this.cascClient.getCascClientSemaphore().release();
		// forward reply to client
		this.commandCallback.receive(ex);
	}
}
