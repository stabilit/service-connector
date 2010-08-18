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
package com.stabilit.scm.cln;

import java.security.InvalidParameterException;

import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.cln.service.Service;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.common.call.SCMPClnDataCall;
import com.stabilit.scm.common.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.service.ISCContext;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SCServiceException;

/**
 * The Class SessionService. SessionService is a remote interface in client API to a session service and provides
 * communication functions.
 * 
 * @author JTraber
 */
public class SessionService extends Service implements ISessionService {

	/**
	 * Instantiates a new session service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param context
	 *            the context
	 */
	public SessionService(String serviceName, ISCContext context) {
		super(serviceName, context);
		this.requester = new Requester(new RequesterContext(context.getConnectionPool(), this.msgId));
		this.serviceContext = new ServiceContext(context, this);
	}

	/** {@inheritDoc} */
	@Override
	public void createSession(String sessionInfo, int echoTimeoutInSeconds, int echoIntervalInSeconds) throws Exception {
		if (this.callback != null) {
			throw new SCServiceException("session already created - delete session first.");
		}
		if (sessionInfo == null) {
			throw new InvalidParameterException("Session info must be set.");
		}
		if (sessionInfo.getBytes().length < 256) {
			throw new InvalidParameterException("Session info too long, over 256 bytes.");
		}
		if (echoTimeoutInSeconds < 1 || echoTimeoutInSeconds > 3600) {
			throw new InvalidParameterException("Echo Timout not within limits 1 to 3600.");
		}
		if (echoIntervalInSeconds < 1 || echoIntervalInSeconds > 3600) {
			throw new InvalidParameterException("Echo Interval not within limits 1 to 3600.");
		}
		this.msgId.reset();
		this.callback = new ServiceCallback();
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(this.requester, this.serviceName);
		createSessionCall.setSessionInfo(sessionInfo);
		createSessionCall.setEchoTimeoutSeconds(echoTimeoutInSeconds);
		createSessionCall.setEchoIntervalSeconds(echoIntervalInSeconds);
		createSessionCall.invoke(this.callback);
		SCMPMessage reply = this.callback.getMessageSync();
		if (reply.isFault()) {
			SCMPFault fault = (SCMPFault) reply;
			throw new SCServiceException("create session failed", fault.getCause());
		}
		this.sessionId = reply.getSessionId();
	}

	/** {@inheritDoc} */
	@Override
	public void deleteSession() throws Exception {
		if (this.callback == null) {
			throw new SCServiceException("no session to delete - create session first.");
		}
		this.msgId.incrementMsgSequenceNr();
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(this.requester, this.serviceName, this.sessionId);
		deleteSessionCall.invoke(this.callback);
		this.callback.getMessageSync();
		this.callback = null;
		this.msgId = null;
	}

	/** {@inheritDoc} */
	@Override
	public SCMessage execute(ISCMessage requestMsg) throws Exception {
		if (requestMsg == null) {
			throw new InvalidParameterException("Message must be set.");
		}
		if (pendingRequest) {
			// already executed before - reply still outstanding
			throw new SCServiceException(
					"execute not possible, there is a pending request - two pending request are not allowed.");
		}
		this.pendingRequest = true;
		this.msgId.incrementMsgSequenceNr();
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		String msgInfo = requestMsg.getMessageInfo();
		if (msgInfo != null) {
			// message info optional
			clnDataCall.setMessagInfo(msgInfo);
		}
		clnDataCall.setCompressed(requestMsg.isCompressed());
		clnDataCall.setRequestBody(requestMsg.getData());
		// invoke asynchronous
		clnDataCall.invoke(this.callback);
		// wait for message in callback
		SCMPMessage reply = this.callback.getMessageSync();
		this.pendingRequest = false;
		if (reply.isFault()) {
			SCMPFault fault = (SCMPFault) reply;
			throw new SCServiceException("execute fails", fault.getCause());
		}
		SCMessage replyToClient = new SCMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		return replyToClient;
	}

	/** {@inheritDoc} */
	@Override
	public void execute(ISCMessage requestMsg, ISCMessageCallback messageCallback) throws Exception {
		if (messageCallback == null) {
			throw new InvalidParameterException("Callback must be set.");
		}
		if (requestMsg == null) {
			throw new InvalidParameterException("Message must be set.");
		}
		if (pendingRequest) {
			// already executed before - reply still outstanding
			throw new SCServiceException(
					"execute not possible, there is a pending request - two pending request are not allowed.");
		}
		this.pendingRequest = true;
		this.msgId.incrementMsgSequenceNr();
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		String msgInfo = requestMsg.getMessageInfo();
		if (msgInfo != null) {
			// message info optional
			clnDataCall.setMessagInfo(msgInfo);
		}
		clnDataCall.setCompressed(requestMsg.isCompressed());
		clnDataCall.setRequestBody(requestMsg.getData());
		ISCMPCallback scmpCallback = new ServiceCallback(this, messageCallback);
		clnDataCall.invoke(scmpCallback);
	}
}