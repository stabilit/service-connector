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
package com.stabilit.sc.cln.scmp;

import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPClnCreateSessionCall;
import com.stabilit.sc.cln.call.SCMPClnDeleteSessionCall;
import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.client.IClientSession;
import com.stabilit.sc.listener.RuntimePoint;
import com.stabilit.sc.scmp.SCMPMessage;

/**
 * The Class SCMPClientSession. Represents a virtual connection between client and server. API programmer needs to
 * manage several client sessions on his own. Necessary to make session calls like SCMPClnDataCall. Needed calls
 * (CLN_CREATE_SESSION, CLN_DELETE_SESSION) to create/delete a session are wrapped inside method.
 * 
 * @author JTraber
 */
public class SCMPClientSession implements IClientSession {

	/** The client. */
	private IClient client;
	/** The session id. */
	private String sessionId;
	/** The service name. */
	private String serviceName;
	/** The session info. */
	private String sessionInfo;
	/** The response message. */
	private SCMPMessage responseMessage;

	/**
	 * Instantiates a new SCMPClientSession.
	 * 
	 * @param client
	 *            the client
	 */
	public SCMPClientSession(IClient client) {
		this(client, null, null);
	}

	/**
	 * The Constructor.
	 * 
	 * @param client
	 *            the client
	 * @param serviceName
	 *            the service name
	 * @param sessionInfo
	 *            the session info
	 */
	public SCMPClientSession(IClient client, String serviceName, String sessionInfo) {
		this.client = client;
		this.serviceName = serviceName;
		this.sessionInfo = sessionInfo;
		this.responseMessage = null;
		this.sessionId = null;
	}

	/**
	 * Creates the session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void createSession() throws Exception {
		if (this.responseMessage != null) {
			RuntimePoint.getInstance().fireRuntime(this,
					"responseMessage already set - create session inoked two times!");
			return;
		}
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(client);
		createSessionCall.setServiceName(this.serviceName);
		createSessionCall.setSessionInfo(this.sessionInfo);
		this.responseMessage = createSessionCall.invoke();

		if (this.responseMessage != null) {
			this.sessionId = this.responseMessage.getSessionId();
			this.client.setClientSession(this);
		}
	}

	/**
	 * Delete session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void deleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.client);
		deleteSessionCall.invoke();
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the service name.
	 * 
	 * @param serviceName
	 *            the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Gets the session info.
	 * 
	 * @return the sessionInfo
	 */
	public String getSessionInfo() {
		return sessionInfo;
	}

	/**
	 * Sets the session info.
	 * 
	 * @param sessionInfo
	 *            the sessionInfo to set
	 */
	public void setSessionInfo(String sessionInfo) {
		this.sessionInfo = sessionInfo;
	}
}
