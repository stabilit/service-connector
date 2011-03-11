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
package org.serviceconnector.call;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SCMPCscCreateSessionCall. Call tries creating a session to a backend server over a SC.
 * 
 * @author JTraber
 */
public class SCMPCscCreateSessionCall extends SCMPCallAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCMPCscCreateSessionCall.class);

	/**
	 * Instantiates a new SCMP csc create session call.
	 * 
	 * @param req
	 *            the req
	 * @param messageToForward
	 *            the message to forward
	 */
	public SCMPCscCreateSessionCall(IRequester req, SCMPMessage messageToForward) {
		super(req, messageToForward);
	}

	/** {@inheritDoc} */
	@Override
	public void invoke(ISCMPMessageCallback scmpCallback, int timeoutMillis) throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		super.invoke(scmpCallback, timeoutMillis);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CSC_CREATE_SESSION;
	}
}
