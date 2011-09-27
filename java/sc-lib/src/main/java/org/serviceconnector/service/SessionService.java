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
package org.serviceconnector.service;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.sc.CreateSessionCommandCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.StatefulServer;

/**
 * The Class SessionService. SessionService is a remote interface to a session service and provides communication functions.
 */
public class SessionService extends StatefulService {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SessionService.class);

	/**
	 * Instantiates a new session service.
	 * 
	 * @param name
	 *            the name
	 */
	public SessionService(String name) {
		super(name, ServiceType.SESSION_SERVICE);
	}

	/**
	 * Allocate server and create session.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @param session
	 *            the session
	 * @param timeoutMillis
	 *            the timeout in milliseconds
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void allocateServerAndCreateSession(SCMPMessage msgToForward, CreateSessionCommandCallback callback,
			Session session, int timeoutMillis) throws Exception {
		int numberOfServer = this.listOfServers.size();
		if (numberOfServer == 0) {
			// no server registered for this service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_SERVER, "service="
					+ msgToForward.getServiceName());
			scmpCommandException.setMessageType(msgToForward.getMessageType());
			throw scmpCommandException;
		}
		for (int i = 0; i < numberOfServer; i++) {
			this.serverIndex++;
			if (this.serverIndex >= numberOfServer) {
				// serverIndex reached the end of list no more servers
				this.serverIndex = 0;
			}
			StatefulServer server = this.listOfServers.get(serverIndex);
			if (server.hasFreeSession()) {
				callback.setServer(server);
				server.addSession(session);
				try {
					server.createSession(msgToForward, callback, timeoutMillis);
				} catch (Exception e) {
					server.removeSession(session);
					callback.setServer(null);
					throw e;
				}
				return;
			}
		}
		// no free session available
		NoFreeServerException noFreeSessionExc = new NoFreeServerException(SCMPError.NO_FREE_SERVER, "service="
				+ msgToForward.getServiceName());
		noFreeSessionExc.setMessageType(msgToForward.getMessageType());
		throw noFreeSessionExc;
	}
}
