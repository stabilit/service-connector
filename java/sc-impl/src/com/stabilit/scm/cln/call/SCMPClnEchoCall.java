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
package com.stabilit.scm.cln.call;

import java.net.InetAddress;
import java.util.Map;

import com.stabilit.scm.cln.net.req.IServiceSession;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * The Class SCMPClnEchoCall. Call sends an echo.
 * 
 * @author JTraber
 */
public class SCMPClnEchoCall extends SCMPSessionCallAdapter {

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
	public SCMPClnEchoCall(IRequester client, IServiceSession scmpSession) {
		super(client, scmpSession);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage invoke() throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.CLIENT_ID, req.hashCode());
		this.requestMessage.setMessageType(getMessageType().getRequestName());
		this.responseMessage = req.sendAndReceive(this.requestMessage);
		if (this.responseMessage.isFault()) {
			throw new SCMPCallException((SCMPFault) responseMessage);
		}
		return this.responseMessage;
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester client, IServiceSession scmpSession) {
		return new SCMPClnEchoCall(client, scmpSession);
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

	/** {@inheritDoc} */
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
		this.requestMessage.setHeader(header);
	}

	/**
	 * Sets the max nodes. Number defines how many nodes echo call should pass through.
	 * 
	 * @param maxNodes
	 *            the new max nodes
	 */
	public void setMaxNodes(int maxNodes) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.MAX_NODES, String.valueOf(maxNodes));
	}
}