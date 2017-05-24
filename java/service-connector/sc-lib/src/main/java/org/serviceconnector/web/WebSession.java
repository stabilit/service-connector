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
package org.serviceconnector.web;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.util.TimeoutWrapper;
import org.serviceconnector.web.ctx.WebContext;

public class WebSession {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSession.class);
	/** The user agent. */
	private String userAgent;

	/** The local server host. */
	private String host;

	/** The local server port. */
	private int port;

	/** The remote host. */
	private String remoteHost;

	/** The remote port. */
	private int remotePort;

	/** The session id. */
	private String id;

	/** The session timeout. */
	private ScheduledFuture<TimeoutWrapper> timeout;
	/** The timeouter task. */
	private TimeoutWrapper timeouterTask;
	private WebCredentials credentials;

	/**
	 * Instantiates a new web session.
	 */
	public WebSession() {
		UUID uuid = UUID.randomUUID();
		this.userAgent = null;
		this.host = null;
		this.remoteHost = null;
		this.port = 0;
		this.remotePort = 0;
		this.credentials = null;
		this.id = uuid.toString();
		LOGGER.debug("New web session created, id = " + this.id);
	}

	/**
	 * Gets the session timeout seconds.
	 *
	 * @return the session timeout seconds
	 */
	public double getSessionTimeoutSeconds() {
		return WebContext.getWebConfiguration().getWebSessionTimeoutMinutes() * 60;
	}

	/**
	 * Gets the session timeout.
	 *
	 * @return the session timeout
	 */
	public ScheduledFuture<TimeoutWrapper> getTimeout() {
		return timeout;
	}

	/**
	 * Sets the session timeout.
	 *
	 * @param timeout the new session timeout
	 */
	public void setTimeout(ScheduledFuture<TimeoutWrapper> timeout) {
		this.timeout = timeout;
	}

	/**
	 * Sets the timeouter task.
	 *
	 * @param timeouterTask the new timeouter task
	 */
	public void setTimeouterTask(TimeoutWrapper timeouterTask) {
		this.timeouterTask = timeouterTask;
	}

	/**
	 * Gets the timeouter task.
	 *
	 * @return the timeouter task
	 */
	public TimeoutWrapper getTimeouterTask() {
		return this.timeouterTask;
	}

	public String getUserAgent() {
		return this.userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getRemoteHost() {
		return this.remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public int getRemotePort() {
		return this.remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public WebCredentials getCredentials() {
		return credentials;
	}

	public void setCredentials(WebCredentials credentials) {
		this.credentials = credentials;
	}

	public String getId() {
		return this.id;
	}
}
