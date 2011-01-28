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
import org.serviceconnector.server.StatefulServer;

/**
 * The Class Session. Provides unique id and an attribute map to store data. A session represents virtual relation between a client
 * and a server.
 */
public class Session extends AbstractSession {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(Session.class);

	private double sessionTimeoutSeconds;
	private boolean pendingRequest;

	public Session(String sessionInfo, String ipAdressList) {
		super(sessionInfo, ipAdressList);
		this.sessionTimeoutSeconds = 0;
		this.pendingRequest = false;
	}

	/**
	 * Sets the session timeout seconds.
	 * 
	 * @param sessionTimeoutSeconds
	 *            the new session timeout seconds
	 */
	public void setSessionTimeoutSeconds(double sessionTimeoutSeconds) {
		this.sessionTimeoutSeconds = sessionTimeoutSeconds;
	}

	/**
	 * Gets the session timeout seconds.
	 * 
	 * @return the session timeout seconds
	 */
	public double getSessionTimeoutSeconds() {
		return sessionTimeoutSeconds;
	}

	/**
	 * Gets the stateful server.
	 * 
	 * @return the stateful server
	 */
	public StatefulServer getStatefulServer() {
		return (StatefulServer) this.server;
	}

	/**
	 * Sets the pending request.
	 * 
	 * @param pendingRequest
	 *            the new pending request
	 */
	public void setPendingRequest(boolean pendingRequest) {
		this.pendingRequest = pendingRequest;
	}

	/**
	 * Gets the pending request.
	 * 
	 * @return the pending request
	 */
	public boolean hasPendingRequest() {
		return this.pendingRequest;
	}
}