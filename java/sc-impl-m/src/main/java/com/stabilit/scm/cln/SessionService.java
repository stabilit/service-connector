/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.cln;

import com.stabilit.scm.cln.service.IServiceContext;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.common.call.SCMPClnDataCall;
import com.stabilit.scm.common.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.service.ISCContext;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCMessage;

/**
 * @author JTraber
 */
public class SessionService implements ISessionService {

	private String serviceName;
	private String sessionId;
	private IServiceContext serviceContext;
	private IRequester requester;
	private ISCMPSynchronousCallback callback;

	public SessionService(String serviceName, ISCContext context) {
		this.serviceName = serviceName;
		this.sessionId = null;
		this.requester = new Requester(new RequesterContext(context.getConnectionPool()));
		this.serviceContext = new ServiceContext(context, this);
		this.callback = new ServiceCallback();
	}

	@Override
	public void createSession(String sessionInfo, int echoTimeout, int echoInterval) throws Exception {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(this.requester, this.serviceName);
		createSessionCall.setSessionInfo(sessionInfo);
		createSessionCall.setEchoTimeout(echoTimeout);
		createSessionCall.setEchoInterval(echoInterval);
		createSessionCall.invoke(this.callback);
		SCMPMessage reply = this.callback.getMessageSync();
		this.sessionId = reply.getSessionId();
	}

	@Override
	public void deleteSession() throws Exception {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.requester, this.serviceName, this.sessionId);
		deleteSessionCall.invoke(this.callback);
		this.callback.getMessageSync();
	}

	@Override
	public SCMessage execute(ISCMessage requestMsg) throws Exception {
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		String msgInfo = requestMsg.getMessageInfo();
		if (msgInfo != null) {
			// message info optional
			clnDataCall.setMessagInfo(msgInfo);
		}
		clnDataCall.setRequestBody(requestMsg.getData());
		// invoke asynchronous
		clnDataCall.invoke(this.callback);
		// wait for message in callback
		SCMPMessage reply = this.callback.getMessageSync();
		SCMessage replyToClient = new SCMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION));
		return replyToClient;
	}

	@Override
	public void execute(ISCMessage requestMsg, ISCMessageCallback messageCallback) throws Exception {
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		String msgInfo = requestMsg.getMessageInfo();
		if (msgInfo != null) {
			// message info optional
			clnDataCall.setMessagInfo(msgInfo);
		}
		clnDataCall.setRequestBody(requestMsg.getData());
		ISCMPCallback scmpCallback = new ServiceCallback(messageCallback);
		clnDataCall.invoke(scmpCallback);
		return;
	}

	@Override
	public IServiceContext getContext() {
		return this.serviceContext;
	}
}
