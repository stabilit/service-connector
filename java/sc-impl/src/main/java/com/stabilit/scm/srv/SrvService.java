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
package com.stabilit.scm.srv;

/**
 * The Class SrvService. Represent of a service on backend server.
 * 
 * @author JTraber
 */
public class SrvService {

	/** The sc host. */
	private String scHost;
	/** The sc port. */
	private int scPort;
	/** The service name. */
	private String serviceName;
	/** The callback. */
	private ISCServerCallback callback;
	/** The max sessions. */
	private int maxSessions;
	/** The max connections. */
	private int maxConnections;

	/**
	 * Instantiates a new srv service.
	 * 
	 * @param scHost
	 *            the sc host
	 * @param scPort
	 *            the sc port
	 * @param serviceName
	 *            the service name
	 * @param maxSessions
	 *            the max sessions
	 * @param maxConnections
	 *            the max connections
	 * @param callback
	 *            the callback
	 */
	public SrvService(String scHost, int scPort, String serviceName, int maxSessions, int maxConnections,
			ISCServerCallback callback) {
		super();
		this.scHost = scHost;
		this.scPort = scPort;
		this.serviceName = serviceName;
		this.callback = callback;
		this.maxConnections = maxConnections;
		this.maxSessions = maxSessions;
	}	
	
	/**
	 * Gets the sc host.
	 *
	 * @return the sc host
	 */
	public String getScHost() {
		return scHost;
	}

	/**
	 * Gets the sc port.
	 *
	 * @return the sc port
	 */
	public int getScPort() {
		return scPort;
	}
	
	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions
	 */
	public int getMaxSessions() {
		return maxSessions;
	}

	/**
	 * Gets the max connections.
	 * 
	 * @return the max connections
	 */
	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * Gets the callback.
	 * 
	 * @return the callback
	 */
	public ISCServerCallback getCallback() {
		return callback;
	}
}
