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

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.scmp.SCMPSession;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class SCMPClnDeleteSessionCall. Call deletes a session.
 * 
 * @author JTraber
 */
public class SCMPClnDeleteSessionCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPClnDeleteSessionCall.
	 */
	public SCMPClnDeleteSessionCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPClnDeleteSessionCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 * @param scmpSession
	 *            the scmp session
	 */
	public SCMPClnDeleteSessionCall(IClient client, SCMPMessage scmpSession) {
		super(client, scmpSession);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#newInstance(com.stabilit.sc.cln.client.IClient,
	 * com.stabilit.sc.scmp.SCMP)
	 */
	@Override
	public ISCMPCall newInstance(IClient client, SCMPMessage scmpSession) {
		return new SCMPClnDeleteSessionCall(client, scmpSession);
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

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#invoke()
	 */
	@Override
	public SCMPMessage invoke() throws Exception {
		super.invoke();
		if (this.scmpSession != null && this.scmpSession instanceof SCMPSession) {
			// remove session from internal registry
			((SCMPSession) this.scmpSession).removeSessionRegistry();
		}
		return this.responseMessage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getMessageType()
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_DELETE_SESSION;
	}
}
