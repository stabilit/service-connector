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
package com.stabilit.sc.cln.scmp;

import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPClnCreateSessionCall;
import com.stabilit.sc.cln.call.SCMPClnDeleteSessionCall;
import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.client.IClientSession;
import com.stabilit.sc.listener.RuntimePoint;
import com.stabilit.sc.scmp.SCMPMessage;

/**
 * @author JTraber
 */
public class SCMPClientSession implements IClientSession  {

	private IClient client;
	private String sessionId;
	private String serviceName;
	private String sessionInfo;
	private SCMPMessage responseMessage;

	public SCMPClientSession(IClient client) {
		this(client, null, null);
	}

	/**
	 * @param client
	 */
	public SCMPClientSession(IClient client, String serviceName, String sessionInfo) {
		this.client = client;
		this.serviceName = serviceName;
		this.sessionInfo = sessionInfo;
		this.responseMessage = null;
		this.sessionId = null;
	}

	public void createSession() throws Exception {
		if (this.responseMessage != null) {
			RuntimePoint.getInstance().fireRuntime(this, "responseMessage already set - create session inoked two times!");
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

	public void deleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.client);
		deleteSessionCall.invoke();
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName
	 *            the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the sessionInfo
	 */
	public String getSessionInfo() {
		return sessionInfo;
	}

	/**
	 * @param sessionInfo
	 *            the sessionInfo to set
	 */
	public void setSessionInfo(String sessionInfo) {
		this.sessionInfo = sessionInfo;
	}
}
