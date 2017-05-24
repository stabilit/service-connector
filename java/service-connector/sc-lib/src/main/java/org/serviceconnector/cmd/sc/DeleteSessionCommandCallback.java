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
package org.serviceconnector.cmd.sc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPVersion;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.Session;

/**
 * The Class DeleteSessionCommandCallback.
 */
public class DeleteSessionCommandCallback implements ISCMPMessageCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DeleteSessionCommandCallback.class);
	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The session. */
	private Session session;
	/** The server. */
	private StatefulServer server;
	/** The msg type. */
	private String msgType;

	/**
	 * Instantiates a new delete session command callback.
	 *
	 * @param request the request
	 * @param response the response
	 * @param callback the callback
	 * @param session the session
	 * @param server the server
	 */
	public DeleteSessionCommandCallback(IRequest request, IResponse response, IResponderCallback callback, Session session, StatefulServer server) {
		this.responderCallback = callback;
		this.request = request;
		this.response = response;
		this.session = session;
		this.server = server;
		this.msgType = request.getMessage().getMessageType();
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// forward server reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(this.msgType);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
		if (reply.isFault()) {
			// delete session failed destroy server abort!
			server.abortSession(session, "deleting session failed, fault reply in callback received");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		SCMPMessage reqMessage = this.request.getMessage();
		String sid = reqMessage.getSessionId();
		LOGGER.warn("receive exception sid=" + sid + " " + ex.toString());
		// free server from session
		server.removeSession(session);
		SCMPMessage fault = null;
		String serviceName = reqMessage.getServiceName();
		SCMPVersion scmpVersion = reqMessage.getSCMPVersion();
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling - SCMP Version request
			fault = new SCMPMessageFault(scmpVersion, SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC delete session sid=" + sid);
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(scmpVersion, SCMPError.CONNECTION_EXCEPTION, "broken connection on SC delete session sid=" + sid);
		} else {
			fault = new SCMPMessageFault(scmpVersion, SCMPError.SC_ERROR, "executing delete session failed sid=" + sid);
		}
		// forward server reply to client
		fault.setIsReply(true);
		fault.setServiceName(serviceName);
		fault.setMessageType(this.msgType);
		this.response.setSCMP(fault);
		this.responderCallback.responseCallback(request, response);
		// delete session failed destroy server abort!
		server.abortSession(session, "deleting session failed, exception received in callback");
	}
}
