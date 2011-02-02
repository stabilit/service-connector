/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.api.srv;

import org.apache.log4j.Logger;
import org.serviceconnector.net.req.IRequester;

/**
 * The Class SrvService. Represent of a service on backend server.
 * 
 * @author JTraber
 */
public abstract class SrvService {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(SrvService.class);

	/** The requester which connects to SC. */
	private IRequester requester;
	/** The service name. */
	private String serviceName;
	/** The max sessions. */
	private int maxSessions;
	/** The max connections. */
	private int maxConnections;

	/**
	 * Instantiates a new srv service.
	 *
	 * @param serviceName the service name
	 * @param maxSessions the max sessions
	 * @param maxConnections the max connections
	 * @param requester the requester
	 */
	public SrvService(String serviceName, int maxSessions, int maxConnections, IRequester requester) {
		super();
		this.requester = requester;
		this.serviceName = serviceName;
		this.maxConnections = maxConnections;
		this.maxSessions = maxSessions;
	}

	/**
	 * Gets the requester.
	 * 
	 * @return the requester
	 */
	public IRequester getRequester() {
		return this.requester;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return this.serviceName;
	}

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions
	 */
	public int getMaxSessions() {
		return this.maxSessions;
	}

	/**
	 * Gets the max connections.
	 * 
	 * @return the max connections
	 */
	public int getMaxConnections() {
		return this.maxConnections;
	}
}
