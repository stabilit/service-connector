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

import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMsgType;

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
		this(null, null, null);
	}

	/**
	 * Instantiates a new SCMPClnDataCall.
	 * 
	 * @param req
	 *            the requester to use when invoking call
	 * @param scSession
	 *            the sc session
	 */
	public SCMPClnDataCall(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester client, String serviceName, String sessionId) {
		return new SCMPClnDataCall(client, serviceName, sessionId);
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
