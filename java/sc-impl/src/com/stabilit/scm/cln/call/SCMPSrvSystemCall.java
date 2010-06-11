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

import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * The Class SCMPSrvSystemCall. Call causes ending specific threads on backend server. Used for testing connection
 * failures.
 * 
 * @author JTraber
 */
public class SCMPSrvSystemCall extends SCMPServerCallAdapter {

	public SCMPSrvSystemCall() {
		this(null, null);
	}

	public SCMPSrvSystemCall(IRequester requester) {
		super(requester);
	}
	
	public SCMPSrvSystemCall(IRequester req, SCMPMessage receivedMessage) {
		super(req, receivedMessage);
	}

	@Override
	public ISCMPCall newInstance(IRequester req, SCMPMessage receivedMessage) {
		return new SCMPSrvSystemCall(req, receivedMessage);
	}
	
	
	@Override
	public ISCMPCall newInstance(IRequester requester) {
		return new SCMPSrvSystemCall(requester);
	}

	/**
	 * Invoke.
	 * 
	 * @return the scmp
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public SCMPMessage invoke() throws Exception {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_REQ_ID, requester.hashCode());

		// adding ip of current unit to header field ip address list
		InetAddress localHost = InetAddress.getLocalHost();
		String ipList = this.requestMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		ipList += "/" + localHost.getHostAddress();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);

		return super.invoke();
	}

	/**
	 * Sets the message info.
	 * 
	 * @param messageInfo
	 *            the new message info
	 */
	public void setMessageInfo(String messageInfo) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.MSG_INFO, messageInfo);
	}

	/**
	 * Gets the message type.
	 * 
	 * @return the message type
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.SRV_SYSTEM;
	}
}
