/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.cln.call.SCMPClnDataCall;
import com.stabilit.scm.cln.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.cln.service.SCMessage;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * @author JTraber
 */
public class SessionService implements ISessionService {

	private String serviceName;
	private String sessionId;
	private IRequester requester;

	public SessionService(String serviceName, IRequester requester) {
		this.serviceName = serviceName;
		this.sessionId = null;
		this.requester = requester;
	}

	@Override
	public void createSession(String sessionInfo) throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(this.requester, this.serviceName);
		createSessionCall.setSessionInfo(sessionInfo);
		SCMPMessage reply = createSessionCall.invoke();
		this.sessionId = reply.getSessionId();
	}

	@Override
	public void deleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.requester, this.serviceName, this.sessionId);
		deleteSessionCall.invoke();
	}

	@Override
	public SCMessage execute(SCMessage requestMsg) throws Exception {
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		clnDataCall.setMessagInfo(requestMsg.getMessageInfo());
		clnDataCall.setRequestBody(requestMsg.getData());
		SCMPMessage reply = clnDataCall.invoke();
		SCMessage replyToClient = new SCMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION));
		return replyToClient;
	}
}
