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
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SCMPEchoCall. Call sends an echo.
 * 
 * @author JTraber
 */
public class SCMPEchoCall extends SCMPCallAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCMPEchoCall.class);

	/**
	 * Instantiates a new SCMPEchoCall.
	 * 
	 * @param req
	 *            the requester to use when invoking call
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 */
	public SCMPEchoCall(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	/**
	 * Instantiates a new sCMP echo call.
	 * 
	 * @param requester
	 *            the requester
	 * @param msgToSend
	 *            the msg to send
	 */
	public SCMPEchoCall(Requester requester, SCMPMessage msgToSend) {
		super(requester, msgToSend);
	}

	/** {@inheritDoc} */
	@Override
	public void invoke(ISCMPMessageCallback scmpCallback, int timeoutMillis) throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		String ipList = this.requestMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		if (ipList == null) {
			ipList = localHost.getHostAddress();
		} else {
			// in case of cascaded echo, there is already an ip in list
			ipList += "/" + localHost.getHostAddress();
		}
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		super.invoke(scmpCallback, timeoutMillis);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.ECHO;
	}
}