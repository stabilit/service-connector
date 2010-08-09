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
package com.stabilit.scm.sc.service;

import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.sc.registry.SubscriptionQueue;

/**
 * The Class PublishService.
 */
public class PublishService extends Service {

	/** The subscription queue. */
	private SubscriptionQueue<SCMPMessage> subscriptionQueue;

	/**
	 * Instantiates a new publish service.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 */
	public PublishService(String name) {
		super(name, ServiceType.PUBLISH_SERVICE);
		this.subscriptionQueue = new SubscriptionQueue<SCMPMessage>();
	}

	/**
	 * Gets the subscription queue.
	 * 
	 * @return the subscription queue
	 */
	public SubscriptionQueue<SCMPMessage> getSubscriptionQueue() {
		return this.subscriptionQueue;
	}

	/**
	 * Allocate server and subscribe.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param session
	 *            the session
	 * @return the server
	 * @throws Exception
	 *             the exception
	 */
	public synchronized Server allocateServerAndSubscribe(SCMPMessage msgToForward, ISCMPCallback callback,
			Session session) throws Exception {
		for (int i = 0; i < listOfServers.size(); i++) {
			serverIndex++;
			if (serverIndex >= listOfServers.size()) {
				// serverIndex reached the end of list no more servers
				serverIndex = 0;
			}
			Server server = listOfServers.get(serverIndex);
			if (server.hasFreeSession()) {
				server.subscribe(msgToForward, callback);
				server.addSession(session);
				return server;
			}
		}
		// no available server for this service
		SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_SERVER);
		scmpCommandException.setMessageType(msgToForward.getMessageType());
		throw scmpCommandException;
	}
}
