/*-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.cmd.sc;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.Session;

/**
 * The Class ClnCreateSessionCommandCallback.
 */
public class ClnCreateSessionCommandCallback implements ISCMPMessageCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ClnCreateSessionCommandCallback.class);
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

	/** The session registry. */
	private SessionRegistry sessionRegistry = AppContext.getSessionRegistry();

	/**
	 * Instantiates a new ClnExecuteCommandCallback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param callback
	 *            the callback
	 * @param session
	 *            the session
	 */
	public ClnCreateSessionCommandCallback(IRequest request, IResponse response, IResponderCallback callback, Session session) {
		this.responderCallback = callback;
		this.request = request;
		this.response = response;
		this.session = session;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();

		if (reply.isFault()) {
			// response is an error - remove session id from header
			reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
			// remove session from server
			server.removeSession(session);
		} else {
			boolean rejectSessionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			if (rejectSessionFlag) {
				// session has been rejected by the server - remove session id from header
				reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
				// remove session from server
				server.removeSession(session);
			} else {
				// session has not accepted, add server to session
				session.setServer(server);
				// finally add session to the registry & schedule session timeout internal
				this.sessionRegistry.addSession(session.getId(), session);
			}
		}
		// forward server reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CLN_CREATE_SESSION);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		LOGGER.warn(ex);
		SCMPMessage fault = null;
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC cln create session");
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln create session");
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing cln create session failed");
		}
		fault.setIsReply(true);
		fault.setServiceName(serviceName);
		fault.setMessageType(SCMPMsgType.CLN_CREATE_SESSION);
		this.response.setSCMP(fault);
		this.responderCallback.responseCallback(request, response);
	}

	/**
	 * Sets the server.
	 * 
	 * @param server
	 *            the new server
	 */
	public void setServer(StatefulServer server) {
		this.server = server;
	}
}
