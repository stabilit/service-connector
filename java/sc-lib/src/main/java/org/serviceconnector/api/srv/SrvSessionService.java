/*
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
 */
package org.serviceconnector.api.srv;

import org.serviceconnector.net.req.IRequester;

/**
 * The Class SrvSessionService. Represent of a session service on backend server.
 */
public class SrvSessionService extends SrvService {

	/** The callback. */
	private SCSessionServerCallback callback;

	/**
	 * Instantiates a new srv session service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param maxSessions
	 *            the max sessions
	 * @param maxConnections
	 *            the max connections
	 * @param requester
	 *            the requester
	 * @param callback
	 *            the callback
	 */
	public SrvSessionService(String serviceName, int maxSessions, int maxConnections, IRequester requester,
			SCSessionServerCallback callback) {
		super(serviceName, maxSessions, maxConnections, requester);
		this.callback = callback;
	}

	/**
	 * Gets the callback.
	 * 
	 * @return the callback
	 */
	public SCSessionServerCallback getCallback() {
		return this.callback;
	}
}
