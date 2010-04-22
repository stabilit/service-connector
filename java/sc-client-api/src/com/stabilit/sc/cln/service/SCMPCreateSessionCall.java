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
package com.stabilit.sc.cln.service;

import java.net.InetAddress;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.io.SCMPSession;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;

/**
 * @author JTraber
 * 
 */
public class SCMPCreateSessionCall extends SCMPCallAdapter {
	
	public SCMPCreateSessionCall() {
		this(null);
	}
	
	public SCMPCreateSessionCall(IClient client) {
		this.client = client;
	}

	@Override
	public SCMPSession invoke() throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.call.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST,localHost.getHostAddress());
		super.invoke();
		SCMPSession scmpSession = new SCMPSession(this.result); // register session in internal registry
		scmpSession.addSessionRegistry();
		return scmpSession;
	}

	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPCreateSessionCall(client);
	}
	
	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}
	
	public void setSessionInfo(String sessionInfo) {
		call.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, sessionInfo);
	}
	
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CREATE_SESSION;
	}
}
