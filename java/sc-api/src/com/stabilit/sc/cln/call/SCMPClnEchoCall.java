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
import java.util.Map;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class SCMPClnEchoCall. Call sends an echo.
 * 
 * @author JTraber
 */
public class SCMPClnEchoCall extends SCMPCallAdapter {

	/**
	 * Instantiates a SCMPClnEchoCall.
	 */
	public SCMPClnEchoCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPClnEchoCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 * @param scmpSession
	 *            the scmp session
	 */
	public SCMPClnEchoCall(IClient client, SCMP scmpSession) {
		super(client, scmpSession);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#invoke()
	 */
	@Override
	public SCMP invoke() throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.call.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		this.call.setHeader(SCMPHeaderAttributeKey.CLIENT_ID, client.hashCode());
		this.call.setMessageType(getMessageType().getRequestName());
		this.result = client.sendAndReceive(this.call);
		if (this.result.isFault()) {
			throw new SCMPCallException((SCMPFault) result);
		}
		return this.result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#newInstance(com.stabilit.sc.cln.client.IClient,
	 * com.stabilit.sc.scmp.SCMP)
	 */
	@Override
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		return new SCMPClnEchoCall(client, scmpSession);
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

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getMessageType()
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_ECHO;
	}

	/**
	 * Sets the header.
	 * 
	 * @param header
	 *            the header
	 */
	public void setHeader(Map<String, String> header) {
		this.call.setHeader(header);
	}

	/**
	 * Sets the max nodes. Number defines how many nodes echo call should pass through.
	 * 
	 * @param maxNodes
	 *            the new max nodes
	 */
	public void setMaxNodes(int maxNodes) {
		this.call.setHeader(SCMPHeaderAttributeKey.MAX_NODES, String.valueOf(maxNodes));
	}
}