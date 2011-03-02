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

import org.apache.log4j.Logger;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SCMPClnUnsubscribeCall. Call unsubscribes from a publish service.
 * 
 * @author JTraber
 */
public class SCMPClnUnsubscribeCall extends SCMPCallAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(SCMPClnUnsubscribeCall.class);

	/**
	 * Instantiates a new SCMPClnUnsubscribeCall.
	 * 
	 * @param req
	 *            the requester
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 */
	public SCMPClnUnsubscribeCall(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	public SCMPClnUnsubscribeCall(Requester requester, SCMPMessage msgToSend) {
		super(requester, msgToSend);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_UNSUBSCRIBE;
	}

	/**
	 * Sets the session info.
	 * 
	 * @param sessionInfo
	 *            the new session info
	 */
	public void setSessionInfo(String sessionInfo) {
		if (sessionInfo == null) {
			return;
		}
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, sessionInfo);
	}
}
