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
package org.serviceconnector.api.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.net.req.SCRequester;

/**
 * The Class Service. Provides basic stuff for every kind of remote service interfaces.
 */
public abstract class SCService {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCService.class);

	/** The service name. */
	protected String serviceName;
	/** The session id, identifies current session context. */
	protected String sessionId;
	/** The requester to communicate. */
	protected SCRequester requester;
	/** The sc client. */
	protected SCClient scClient;
	/** The pending request, marks if a reply is outstanding or if service is ready for next. */
	protected volatile boolean pendingRequest;
	/** The session active, marks state of a session/subscription. */
	protected volatile boolean sessionActive = false;
	/** The message callback. */
	protected SCMessageCallback messageCallback;

	/**
	 * Instantiates a new service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param scContext
	 *            the context
	 */
	public SCService(SCClient scClient, String serviceName, SCRequester requester) {
		this.serviceName = serviceName;
		this.sessionActive = false;
		this.sessionId = null;
		this.pendingRequest = false;
		this.requester = requester;
		this.scClient = scClient;
	}

	/**
	 * Sets the request complete.
	 */
	synchronized void setRequestComplete() {
		this.pendingRequest = false;
	}

	public SCClient getScClient() {
		return scClient;
	}

	/**
	 * Checks if session in service is active.
	 * 
	 * @return true, if is active
	 */
	public boolean isActive() {
		return this.sessionActive;
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the session id
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}
}