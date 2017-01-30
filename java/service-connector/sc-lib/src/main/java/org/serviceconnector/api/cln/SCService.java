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

import org.serviceconnector.net.req.SCRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Service. Provides basic stuff for every kind of remote service interfaces.<br />
 * <br />
 * More informations available in subclasses.
 */
public abstract class SCService {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SCService.class);

	/**
	 * The service name. The service name is an abstract name and represents the logical address of the service. In order to allow message routing the name must be unique in scope
	 * of the entire SC network. Service names must be agreed at the application level and are stored in the SC configuration.
	 */
	protected String serviceName;
	/** The session id - identifies session context of communication. */
	protected String sessionId;
	/** The requester to communicate. */
	protected SCRequester requester;
	/** The SC client. */
	protected SCClient scClient;
	/** The pending request, marks if a reply is outstanding or if service is ready for next. */
	protected volatile boolean pendingRequest;
	/** The session active, marks state of a session/subscription. */
	protected volatile boolean sessionActive = false;
	/** The message callback to use for replies. */
	protected SCMessageCallback messageCallback;

	/**
	 * Instantiates a new service.
	 * 
	 * @param scClient the sc client
	 * @param serviceName the service name
	 * @param requester the requester
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
	 * Sets the request complete. Used only internally (method visibility).
	 */
	void setRequestComplete() {
		this.pendingRequest = false;
	}

	/**
	 * Gets the associated SC client.
	 * 
	 * @return the SC client
	 */
	public SCClient getSCClient() {
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
