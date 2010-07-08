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

import java.util.UUID;

import com.stabilit.scm.common.util.MapBean;
import com.stabilit.scm.common.util.TimerTaskWrapper;

/**
 * The Class Session. Provides unique id and an attribute map to store data. A session represents virtual relation
 * between a client and a server.
 */
public class Session extends MapBean<Object> {

	/** The id. */
	private String id;
	/** The server. */
	private Server server;
	/** The echo timeout. */
	private int echoTimeout;
	/** The echo interval. */
	private int echoInterval;
	/** The session timeouter - observes session timeout. */
	private TimerTaskWrapper sessionTimeouter;

	/**
	 * Instantiates a new session.
	 */
	public Session() {
		UUID uuid = UUID.randomUUID();
		this.id = uuid.toString();
		this.server = null;
		this.echoInterval = 0;
		this.echoTimeout = 0;
		this.sessionTimeouter = null;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Sets the server.
	 * 
	 * @param server
	 *            the new server
	 */
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Gets the server.
	 * 
	 * @return the server
	 */
	public Server getServer() {
		return this.server;
	}

	/**
	 * Sets the echo timeout.
	 * 
	 * @param echoTimeout
	 *            the new echo timeout
	 */
	public void setEchoTimeout(int echoTimeout) {
		this.echoTimeout = echoTimeout;
	}

	/**
	 * Sets the echo interval.
	 * 
	 * @param echoInterval
	 *            the new echo interval
	 */
	public void setEchoInterval(int echoInterval) {
		this.echoInterval = echoInterval;
	}

	/**
	 * Gets the echo timeout.
	 * 
	 * @return the echo timeout
	 */
	public int getEchoTimeout() {
		return echoTimeout;
	}

	/**
	 * Gets the echo interval.
	 * 
	 * @return the echo interval
	 */
	public int getEchoInterval() {
		return echoInterval;
	}

	/**
	 * Gets the session timeouter.
	 * 
	 * @return the session timeouter
	 */
	public TimerTaskWrapper getSessionTimeouter() {
		return sessionTimeouter;
	}

	/**
	 * Sets the session timeouter.
	 * 
	 * @param sessionTimeouter
	 *            the new session timeouter
	 */
	public void setSessionTimeouter(TimerTaskWrapper sessionTimeouter) {
		this.sessionTimeouter = sessionTimeouter;
	}

	/**
	 * To string.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		return id + ":" + server;
	}
}
