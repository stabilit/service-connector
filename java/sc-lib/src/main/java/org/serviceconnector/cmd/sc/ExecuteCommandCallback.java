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

import org.apache.log4j.Logger;
import org.serviceconnector.cache.SCCache;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.service.Session;

/**
 * The Class ExecuteCommandCallback.
 */
public class ExecuteCommandCallback implements ISCMPMessageCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ExecuteCommandCallback.class);
	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The request message. */
	private SCMPMessage requestMessage;
	/** The session id. */
	private String sid;
	/** The request service name. */
	private String requestServiceName;
	/** The session registry. */
	private SessionRegistry sessionRegistry = AppContext.getSessionRegistry();
	/** The msg type. */
	private String msgType;

	/**
	 * Instantiates a new ExecuteCommandCallback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param callback
	 *            the callback
	 * @param sessionId
	 *            the session id
	 */
	public ExecuteCommandCallback(IRequest request, IResponse response, IResponderCallback callback, String sessionId) {
		this.responderCallback = callback;
		this.request = request;
		this.response = response;
		this.sid = sessionId;
		this.requestMessage = this.request.getMessage();
		this.requestServiceName = requestMessage.getServiceName();
		this.msgType = request.getMessage().getMessageType();
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		SCCache cache = AppContext.getSCCache();
		if (cache.isCacheEnabled() == true) {
			cache.cacheMessage(this.requestMessage, reply);
		}
		// forward server reply to client
		reply.setIsReply(true);
		reply.setMessageType(this.msgType);
		reply.setServiceName(this.requestServiceName);
		this.response.setSCMP(reply);
		// schedule session timeout
		Session session = this.sessionRegistry.getSession(this.sid);
		if (session != null) {
			synchronized (session) {
				// reset session timeout to ECI
				this.sessionRegistry.resetSessionTimeout(session, session.getSessionTimeoutMillis());
				session.setPendingRequest(false); // IMPORTANT - set false after reset - because of parallel echo call
			}
		}
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		LOGGER.warn("receive exception sid=" + sid + " " + ex.toString());
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC sid=" + this.sid);
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection to server sid=" + this.sid);
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "error executing " + this.msgType + " sid=" + this.sid);
		}
		// caching
		SCCache cache = AppContext.getSCCache();
		if (cache.isCacheEnabled() == true) {
			cache.cacheMessage(this.requestMessage, fault);
		}
		// forward server reply to client
		fault.setSessionId(this.sid);
		fault.setIsReply(true);
		fault.setMessageType(this.msgType);
		fault.setServiceName(this.requestServiceName);
		this.response.setSCMP(fault);
		// schedule session timeout
		Session session = this.sessionRegistry.getSession(this.sid);
		if (session != null) {
			synchronized (session) {
				// reset session timeout to ECI
				this.sessionRegistry.resetSessionTimeout(session, session.getSessionTimeoutMillis());
				session.setPendingRequest(false); // IMPORTANT - set false after reset - because of parallel echo call
			}
		}
		this.responderCallback.responseCallback(request, response);
	}
}
