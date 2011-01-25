/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.service;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import org.serviceconnector.server.IServer;
import org.serviceconnector.util.TimeoutWrapper;

/**
 * The Class AbstractSession.
 */
public abstract class AbstractSession {

	/** The id. */
	private String id;
	/** The server. */
	protected IServer server;
	/** The ip address list. */
	protected String ipAddressList;
	/** The session info. */
	private String sessionInfo;

	/** The session timeout. */
	private ScheduledFuture<TimeoutWrapper> timeout;

	/** The timeouter task. */
	private TimeoutWrapper timeouterTask;

	/**
	 * Instantiates a new session.
	 * 
	 * @param sessionInfo
	 *            the session info
	 * @param ipAddressList
	 *            the ip address list
	 */
	public AbstractSession(String sessionInfo, String ipAddressList) {
		UUID uuid = UUID.randomUUID();
		this.id = uuid.toString();
		this.server = null;
		this.timeout = null;
		this.ipAddressList = ipAddressList;
		this.sessionInfo = sessionInfo;
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
	public void setServer(IServer server) {
		this.server = server;
	}

	/**
	 * Gets the server.
	 * 
	 * @return the server
	 */
	public IServer getServer() {
		return this.server;
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
	 * @param timeout
	 *            the new session timeout
	 */
	public void setTimeout(ScheduledFuture<TimeoutWrapper> timeout) {
		this.timeout = timeout;
	}

	/**
	 * Gets the ip address list.
	 * 
	 * @return the ip address list
	 */
	public String getIpAddressList() {
		return ipAddressList;
	}

	/**
	 * Gets the session info.
	 * 
	 * @return the session info
	 */
	public String getSessionInfo() {
		return sessionInfo;
	}

	/**
	 * Sets the timeouter task.
	 * 
	 * @param timeouterTask
	 *            the new timeouter task
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

	/**
	 * To string.
	 * 
	 * @return the string {@inheritDoc}
	 */
	@Override
	public String toString() {
		return id + ":" + server;
	}
}
