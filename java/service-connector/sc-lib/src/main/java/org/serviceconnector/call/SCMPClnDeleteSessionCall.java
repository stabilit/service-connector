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

import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SCMPClnDeleteSessionCall. Call deletes a session.
 *
 * @author JTraber
 */
public class SCMPClnDeleteSessionCall extends SCMPCallAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SCMPClnDeleteSessionCall.class);

	/**
	 * Instantiates a new SCMPClnDeleteSessionCall.
	 *
	 * @param req the requester to use when invoking call
	 * @param serviceName the service name
	 * @param sessionId the session id
	 */
	public SCMPClnDeleteSessionCall(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_DELETE_SESSION;
	}

	/**
	 * Sets the session info.
	 *
	 * @param sessionInfo the new session info
	 */
	public void setSessionInfo(String sessionInfo) {
		this.requestMessage.setHeaderCheckNull(SCMPHeaderAttributeKey.SESSION_INFO, sessionInfo);
	}
}
