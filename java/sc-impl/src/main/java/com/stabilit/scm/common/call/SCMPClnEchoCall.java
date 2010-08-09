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
package com.stabilit.scm.common.call;

import java.net.InetAddress;
import java.util.Map;

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
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
		this(null, null, null);
	}

	/**
	 * Instantiates a new SCMPClnEchoCall.
	 * 
	 * @param req
	 *            the requester to use when invoking call
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 */
	public SCMPClnEchoCall(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public void invoke(ISCMPCallback scmpCallback) throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.CLN_REQ_ID, requester.hashCode());
		super.invoke(scmpCallback);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester req, String serviceName, String sessionId) {
		return new SCMPClnEchoCall(req, serviceName, sessionId);
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
}