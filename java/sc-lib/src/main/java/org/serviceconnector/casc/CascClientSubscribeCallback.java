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

import java.util.concurrent.Semaphore;

import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class CascClientCallback.
 */
public class CascClientSubscribeCallback implements ISCMPMessageCallback {

	/** The command callback. */
	private ISCMPMessageCallback commandCallback;
	/** The cascaded client semaphore. */
	private Semaphore cascClientSemaphore;
	/** The cascaded client. */
	private CascadedClient cascClient;

	public CascClientSubscribeCallback(ISCMPMessageCallback commandCallback, CascadedClient cascClient,
			Semaphore cascClientSemaphore) {
		this.commandCallback = commandCallback;
		this.cascClientSemaphore = cascClientSemaphore;
		this.cascClient = cascClient;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		boolean rejectSubscriptionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
		if (reply.isFault() == false && rejectSubscriptionFlag == false) {
			// subscription successfully created
			this.cascClient.setSubscribed(true);
			this.cascClient.setSubscriptionId(reply.getSessionId());
			// TODO JOT start receivePublication
		}
		try {
			// forward reply to client
			this.commandCallback.receive(reply);
			// release permit
			this.cascClientSemaphore.release();
		} catch (Exception e) {
			this.cascClient.setSubscribed(false);
			this.cascClient.setSubscriptionId(reply.getSessionId());
			// release permit
			this.cascClientSemaphore.release();
			this.commandCallback.receive(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		// release permit
		this.cascClientSemaphore.release();
		// forward reply to client
		this.commandCallback.receive(ex);
	}
}
