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
package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.cln.call.SCMPClnDataCall;
import com.stabilit.scm.cln.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.cln.service.ISCSession;
import com.stabilit.scm.cln.service.SCMessage;
import com.stabilit.scm.common.log.listener.RuntimePoint;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class SCDataSession. Represents a data session to a server over SC. Provides functionality to communicate with a
 * service.
 * 
 * @author JTraber
 */
public class SCDataSession implements ISCSession {

	/** The cln data call. */
	private SCMPClnDataCall clnDataCall;
	/** The scmp group call. */
	private ISCMPCall scmpGroupCall;
	/** The requester. */
	private IRequester req;
	/** The response message. */
	private SCMPMessage responseMessage;
	/** The session id. */
	private String sessionId;
	/** The service name. */
	private String serviceName;
	/** The message info. */
	private String messageInfo;
	/** The session info. */
	private String sessionInfo;

	/**
	 * Instantiates a new sc data session.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param req
	 *            the requester
	 */
	public SCDataSession(String serviceName, IRequester req) {
		this.serviceName = serviceName;
		this.req = req;
		this.scmpGroupCall = null;
		this.messageInfo = null;
		this.sessionInfo = null;
		this.messageInfo = null;
		this.clnDataCall = null;
	}

	/**
	 * Creates the session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void createSession() throws Exception {
		if (this.responseMessage != null) {
			RuntimePoint.getInstance().fireRuntime(this,
					"responseMessage already set - create session inoked two times!");
			return;
		}
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, this.serviceName);
		createSessionCall.setSessionInfo(this.sessionInfo);
		this.responseMessage = createSessionCall.invoke();

		if (this.responseMessage != null) {
			this.sessionId = this.responseMessage.getSessionId();
		}
		this.clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, this.serviceName,
				this.sessionId);
	}

	/**
	 * Execute.
	 * 
	 * @param message
	 *            the data
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public SCMessage execute(SCMessage message) throws Exception {
		SCMPMessage scmpReply = null;

		this.clnDataCall.setMessagInfo(this.messageInfo);
		this.clnDataCall.setCompression(message.isCompression());
		this.clnDataCall.setRequestBody(message.getData());

		// if group call is requested - invoke group call
		if (this.scmpGroupCall != null) {
			scmpReply = this.scmpGroupCall.invoke();
		} else {
			scmpReply = this.clnDataCall.invoke();
		}
		return new SCMessage(scmpReply.getBody());
	}

	/**
	 * Delete session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void deleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.req, this.serviceName, this.sessionId);
		deleteSessionCall.invoke();
	}

	/**
	 * Close group.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void closeGroup() throws Exception {
		this.scmpGroupCall.closeGroup(); // send REQ (no body content)
		this.scmpGroupCall = null;
	}

	/**
	 * Open group.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void openGroup() throws Exception {
		this.scmpGroupCall = this.clnDataCall.openGroup();
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the service name.
	 * 
	 * @param serviceName
	 *            the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Gets the session info.
	 * 
	 * @return the sessionInfo
	 */
	public String getSessionInfo() {
		return sessionInfo;
	}

	/**
	 * Sets the message info.
	 * 
	 * @param messageInfo
	 *            the new message info
	 */
	@Override
	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}

	/**
	 * Sets the session info.
	 * 
	 * @param sessionInfo
	 *            the new session info
	 */
	@Override
	public void setSessionInfo(String sessionInfo) {
		this.sessionInfo = sessionInfo;
	}
}
