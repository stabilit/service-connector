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
import com.stabilit.sc.cln.scmp.SCMPSession;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class SCMPClnCreateSessionCall. Call creates a session.
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

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#invoke()
	 */
	@Override
	public SCMPSession invoke() throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.call.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		super.invoke();
		SCMPSession scmpSession = new SCMPSession(this.result); // register session in internal registry
		scmpSession.addSessionRegistry();
		return scmpSession;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#newInstance(com.stabilit.sc.cln.client.IClient)
	 */
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
		call.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	/**
	 * Sets the session info.
	 * 
	 * @param sessionInfo
	 *            the new session info
	 */
	public void setSessionInfo(String sessionInfo) {
		call.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, sessionInfo);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getMessageType()
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_CREATE_SESSION;
	}
}
