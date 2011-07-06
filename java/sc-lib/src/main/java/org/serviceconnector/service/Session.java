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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.util.TimeoutWrapper;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class Session. Provides unique id and an attribute map to store data. A session represents virtual relation between a client
 * and a server.
 */
public class Session extends AbstractSession {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Session.class);

	/** The session timeout milliseconds. */
	private double sessionTimeoutMillis;
	/** The pending request. */
	private boolean pendingRequest;
	/** The last execute time, timestamp of last execution for this session. */
	private Date lastExecuteTime;

	/**
	 * Instantiates a new session.
	 * 
	 * @param sessionInfo
	 *            the session info
	 * @param ipAdressList
	 *            the ip adress list
	 */
	public Session(String sessionInfo, String ipAdressList) {
		super(sessionInfo, ipAdressList);
		this.sessionTimeoutMillis = 0;
		this.pendingRequest = false;
	}

	/**
	 * Sets the session timeout seconds.
	 * 
	 * @param sessionTimeoutMillis
	 *            the new session timeout seconds
	 */
	public void setSessionTimeoutMillis(double sessionTimeoutMillis) {
		this.sessionTimeoutMillis = sessionTimeoutMillis;
	}

	/**
	 * Gets the session timeout seconds.
	 * 
	 * @return the session timeout seconds
	 */
	public double getSessionTimeoutMillis() {
		return sessionTimeoutMillis;
	}

	/**
	 * Gets the stateful server.
	 * 
	 * @return the stateful server
	 */
	public StatefulServer getStatefulServer() {
		return (StatefulServer) this.server;
	}

	/**
	 * Sets the pending request. Careful in use - take care of synchronization.
	 * 
	 * @param pendingRequest
	 *            the new pending request
	 */
	public void setPendingRequest(boolean pendingRequest) {
		this.pendingRequest = pendingRequest;
	}

	/**
	 * Gets the pending request. Careful in use - take care of synchronization. setPendingRequest
	 * 
	 * @return the pending request
	 */
	public boolean hasPendingRequest() {
		return this.pendingRequest;
	}

	/**
	 * Gets the last execute time.
	 * 
	 * @return the last execute time
	 */
	public Date getLastExecuteTime() {
		return this.lastExecuteTime;
	}

	/**
	 * Reset execute time.
	 */
	public void resetExecuteTime() {
		this.lastExecuteTime = Calendar.getInstance().getTime();
	}

	/**
	 * Dump the session into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("session");
		writer.writeAttribute("id", this.getId());
		writer.writeAttribute("sessionInfo", this.getSessionInfo());
		writer.writeAttribute("isCascaded", this.isCascaded());
		writer.writeAttribute("sessionTimeoutMillis", this.getSessionTimeoutMillis());
		writer.writeAttribute("hasPendingRequest", this.hasPendingRequest());
		ScheduledFuture<TimeoutWrapper> timeouter = this.getTimeout();
		if (timeouter != null) {
			writer.writeAttribute("timeoutSecs", timeouter.getDelay(TimeUnit.SECONDS));
		}
		writer.writeElement("ipAddressList", this.getIpAddressList());
		writer.writeElement("creationTime", this.getCreationTime().toString());
		if (this.lastExecuteTime != null) {
			writer.writeElement("lastExecuteTime", this.getLastExecuteTime().toString());
		}
		this.getService().dump(writer);
		writer.writeEndElement(); // session
	}
}