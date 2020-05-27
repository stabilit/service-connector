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
package org.serviceconnector.cmd.casc;

import java.io.IOException;

import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPVersion;
import org.serviceconnector.service.InvalidMaskLengthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CommandCascCallback.
 */
public class CommandCascCallback implements ISCMPMessageCallback {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandCascCallback.class);
	/** The callback. */
	protected IResponderCallback responderCallback;
	/** The request. */
	protected IRequest request;
	/** The response. */
	protected IResponse response;
	/** The msg type. */
	protected String msgType;

	/**
	 * Instantiates a new CommandCascCallback.
	 *
	 * @param request the request
	 * @param response the response
	 * @param callback the callback
	 */
	public CommandCascCallback(IRequest request, IResponse response, IResponderCallback callback) {
		this.responderCallback = callback;
		this.request = request;
		this.response = response;
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
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		SCMPMessage reqMessage = this.request.getMessage();
		String sid = reqMessage.getSessionId();
		LOGGER.warn("receive exception sid=" + sid + " " + ex.toString());
		SCMPMessage fault = null;
		SCMPVersion scmpVersion = reqMessage.getSCMPVersion();
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling - SCMP Version request
			fault = new SCMPMessageFault(scmpVersion, SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC sid=" + sid);
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(scmpVersion, SCMPError.CONNECTION_EXCEPTION, "broken connection on SC sid=" + sid);
		} else if (ex instanceof InvalidMaskLengthException) {
			fault = new SCMPMessageFault(scmpVersion, SCMPError.HV_WRONG_MASK, ex.getMessage() + " sid=" + sid);
		} else {
			fault = new SCMPMessageFault(scmpVersion, SCMPError.SC_ERROR, "executing " + this.msgType + " failed sid=" + sid);
		}
		fault.setSessionId(this.request.getMessage().getSessionId());
		String serviceName = reqMessage.getServiceName();
		// forward server reply to client
		fault.setIsReply(true);
		fault.setServiceName(serviceName);
		fault.setMessageType(this.msgType);
		this.response.setSCMP(fault);
		this.responderCallback.responseCallback(request, response);
	}
}
