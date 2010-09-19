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
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;


/**
 * The Class SCMPSrvCreateSessionCall. Call tries getting a session on a backend server.
 * 
 * @author JTraber
 */
public class SCMPSrvCreateSessionCall extends SCMPServerCallAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPSrvCreateSessionCall.class);
	
	/**
	 * Instantiates a new SCMPSrvCreateSessionCall.
	 */
	public SCMPSrvCreateSessionCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPSrvCreateSessionCall.
	 * 
	 * @param req
	 *            the requester
	 * @param receivedMessage
	 *            the received message
	 */
	public SCMPSrvCreateSessionCall(IRequester req, SCMPMessage receivedMessage) {
		super(req, receivedMessage);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester req, SCMPMessage receivedMessage) {
		return new SCMPSrvCreateSessionCall(req, receivedMessage);
	}

	/** {@inheritDoc} */
	@Override
	public void invoke(ISCMPCallback scmpCallback, int timeoutInMillis) throws Exception {
		// adding ip of current unit to header field ip address list
		InetAddress localHost = InetAddress.getLocalHost();
		String ipList = this.requestMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		ipList += "/" + localHost.getHostAddress();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		super.invoke(scmpCallback, timeoutInMillis);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.SRV_CREATE_SESSION;
	}
}
