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

import com.stabilit.scm.cln.req.IClientSession;
import com.stabilit.scm.cln.req.IRequester;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMsgType;

/**
 * The Class SCMPClnDataCall. Call sends data to backend server over SC.
 * 
 * @author JTraber
 */
public class SCMPClnDataCall extends SCMPSessionCallAdapter {

	/**
	 * Instantiates a new SCMPClnDataCall.
	 */
	public SCMPClnDataCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPClnDataCall.
	 * 
	 * @param client
	 *            the client
	 */
	public SCMPClnDataCall(IRequester client) {
		super(client);
	}

	/**
	 * Instantiates a new SCMPClnDataCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 * @param scmpSession
	 *            the scmp session
	 */
	public SCMPClnDataCall(IRequester client, IClientSession scmpSession) {
		super(client, scmpSession);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester client) {
		return new SCMPClnDataCall(client);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester client, IClientSession clientSession) {
		return new SCMPClnDataCall(client, clientSession);
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
	 * Sets the message info.
	 * 
	 * @param messageInfo
	 *            the new message info
	 */
	public void setMessagInfo(String messageInfo) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.MSG_INFO, messageInfo);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_DATA;
	}
}
