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
package com.stabilit.sc.cln.scmp;

import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;

/**
 * The Class SCMPSession. Represents a connection to a server. Can be registered in an internal registry.
 * 
 * @author JTraber
 */
public class SCMPSession extends SCMPMessage {		// TODO (TRN) Session is NOT a message! This is completely wrong.

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5900008165666082494L;

	/**
	 * Instantiates a new sCMP session.
	 * 
	 * @param message
	 *            the scmp message
	 */
	public SCMPSession(SCMPMessage message) {
		String sessionId = message.getSessionId();
		this.setSessionId(sessionId);
		String serviceName = message.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
		this.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		String msgType = message.getHeader(SCMPHeaderAttributeKey.MSG_TYPE);
		this.setHeader(SCMPHeaderAttributeKey.MSG_TYPE, msgType);
	}

	/**
	 * Adds the session to registry.
	 */
	public void addSessionRegistry() {
		String sessionId = getSessionId();
		this.setSessionId(sessionId);
		String serviceName = getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
		SCMPSessionRegistry sessionRegistry = SCMPSessionRegistry.getCurrentInstance();
		sessionRegistry.add(sessionId, serviceName);
	}

	/**
	 * Removes the session from registry.
	 */
	public void removeSessionRegistry() {
		String sessionId = getSessionId();
		this.setSessionId(sessionId);
		String serviceName = getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
		SCMPSessionRegistry sessionRegistry = SCMPSessionRegistry.getCurrentInstance();
		sessionRegistry.remove(sessionId, serviceName);
	}
}
