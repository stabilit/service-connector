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

import java.util.Map;

import com.stabilit.scm.cln.req.IRequester;
import com.stabilit.scm.cln.req.IServiceSession;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMsgType;

/**
 * The Class SCMPSrvDataCall. Call send data to backend server.
 * 
 * @author JTraber
 */
public class SCMPSrvDataCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPSrvDataCall.
	 */
	public SCMPSrvDataCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPSrvDataCall.
	 * 
	 * @param client
	 *            the client
	 * @param scmpSession
	 *            the scmp session
	 */
	public SCMPSrvDataCall(IRequester client, IServiceSession scmpSession) {
		super(client, scmpSession);
	}

	/**
	 * New instance.
	 * 
	 * @param client
	 *            the client
	 * @return the iSCMP call
	 */
	@Override
	public ISCMPCall newInstance(IRequester client) {
		return new SCMPSrvDataCall(client, null);
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
	 * Sets the messag info.
	 * 
	 * @param messageInfo
	 *            the new messag info
	 */
	public void setMessagInfo(String messageInfo) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.MSG_INFO, messageInfo);
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
	 * Gets the message type.
	 * 
	 * @return the message type
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.SRV_DATA;
	}
}
