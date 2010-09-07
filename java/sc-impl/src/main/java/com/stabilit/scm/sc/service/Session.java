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

import org.apache.log4j.Logger;

import com.stabilit.scm.common.util.TimerTaskWrapper;

/**
 * The Class Session. Provides unique id and an attribute map to store data. 
 * A session represents virtual relation between a client and a server.
 */
public class Session {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(Session.class);
	
	/** The id. */
	private String id;
	/** The server. */
	private Server server;
	/** The echo timeout. */
	private int echoTimeoutSeconds;
	/** The echo interval. */
	private int echoIntervalSeconds;
	/** The session timeouter - observes session timeout. */
	private TimerTaskWrapper sessionTimeouter;

	/**
	 * Instantiates a new session.
	 */
	public Session() {
		UUID uuid = UUID.randomUUID();
		this.id = uuid.toString();
		this.server = null;
		this.echoIntervalSeconds = 0;
		this.echoTimeoutSeconds = 0;
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
	 * Sets the echo timeout in seconds.
	 * 
	 * @param echoTimeoutSeconds
	 *            the new echo timeout
	 */
	public void setEchoTimeoutSeconds(int echoTimeoutSeconds) {
		this.echoTimeoutSeconds = echoTimeoutSeconds;
	}

	/**
	 * Sets the echo interval.
	 * 
	 * @param echoIntervalSeconds
	 *            the new echo interval
	 */
	public void setEchoIntervalSeconds(int echoIntervalSeconds) {
		this.echoIntervalSeconds = echoIntervalSeconds;
	}

	/**
	 * Gets the echo timeout seconds.
	 * 
	 * @return the echo timeout seconds
	 */
	public int getEchoTimeoutSeconds() {
		return echoTimeoutSeconds;
	}

	/**
	 * Gets the echo interval in seconds.
	 * 
	 * @return the echo interval in seconds
	 */
	public int getEchoIntervalSeconds() {
		return echoIntervalSeconds;
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

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return id + ":" + server;
	}
}
