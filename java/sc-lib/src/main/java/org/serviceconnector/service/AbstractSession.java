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

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import org.serviceconnector.server.IServer;
import org.serviceconnector.util.TimeoutWrapper;
import org.serviceconnector.util.XMLDumpWriter;

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
	/** The service. */
	private Service service;

	/** The cascaded, indicates if session has been created by a cascaded client or a real client. */
	private boolean cascaded;

	/**
	 * Instantiates a new session.
	 * 
	 * @param sessionInfo
	 *            the session info
	 * @param ipAddressList
	 *            the ip address list
	 */
	public AbstractSession(String sessionInfo, String ipAddressList) {
		this(sessionInfo, ipAddressList, false);
	}

	/**
	 * Instantiates a new abstract session.
	 * 
	 * @param sessionInfo
	 *            the session info
	 * @param ipAddressList
	 *            the ip address list
	 * @param cascaded
	 *            the cascaded
	 */
	public AbstractSession(String sessionInfo, String ipAddressList, boolean cascaded) {
		UUID uuid = UUID.randomUUID();
		this.id = uuid.toString();
		this.server = null;
		this.timeout = null;
		this.ipAddressList = ipAddressList;
		this.sessionInfo = sessionInfo;
		this.cascaded = cascaded;
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
	 * Sets the service.
	 * 
	 * @param service
	 *            the new service
	 */
	public void setService(Service service) {
		this.service = service;
	}

	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	public Service getService() {
		return service;
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
	 * Checks if is cascaded session. Indicates if session has been created by a cascaded client or a real client.
	 * 
	 * @return true, if is cascaded
	 */
	public boolean isCascaded() {
		return this.cascaded;
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
	
	/**
	 * Dump the session into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public abstract void dump(XMLDumpWriter writer) throws Exception;
}
