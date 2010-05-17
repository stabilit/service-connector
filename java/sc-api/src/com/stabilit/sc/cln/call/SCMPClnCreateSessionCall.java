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
package com.stabilit.sc.cln.call;

import java.net.InetAddress;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.scmp.SCMPSessionRegistry;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class SCMPClnCreateSessionCall. Call tries creating a session to a backend server over a SC.
 * 
 * @author JTraber
 */
public class SCMPClnCreateSessionCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPClnCreateSessionCall.
	 */
	public SCMPClnCreateSessionCall() {
		this(null);
	}

	/**
	 * Instantiates a new SCMPClnCreateSessionCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 */
	public SCMPClnCreateSessionCall(IClient client) {
		this.client = client;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage invoke() throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		super.invoke();
		String serviceName = this.responseMessage.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
		String sessionId = this.responseMessage.getSessionId();
		SCMPSessionRegistry sessionRegistry = SCMPSessionRegistry.getCurrentInstance();
		sessionRegistry.add(sessionId, serviceName);
		return this.responseMessage;
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPClnCreateSessionCall(client);
	}

	/**
	 * Sets the service name.
	 * 
	 * @param serviceName
	 *            the new service name
	 */
	public void setServiceName(String serviceName) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	/**
	 * Sets the session info.
	 * 
	 * @param sessionInfo
	 *            the new session info
	 */
	public void setSessionInfo(String sessionInfo) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, sessionInfo);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_CREATE_SESSION;
	}
}
