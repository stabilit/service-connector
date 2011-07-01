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

import org.apache.log4j.Logger;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.ISubscriptionCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.Subscription;

/**
 * The Class CscAbortSubscriptionCommandCallback.
 */
public class CscAbortSubscriptionCommandCallback implements ISubscriptionCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CscAbortSubscriptionCommandCallback.class);
	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The subscription. */
	private Subscription cascSubscription;

	/**
	 * Instantiates a new csc abort subscription command callback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param responderCallback
	 *            the responder callback
	 */
	public CscAbortSubscriptionCommandCallback(IRequest request, IResponse response, IResponderCallback responderCallback,
			Subscription cascSubscription) {
		this.responderCallback = responderCallback;
		this.request = request;
		this.response = response;
		this.cascSubscription = cascSubscription;
	}

	@Override
	public void receive(SCMPMessage reply) {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// forward server reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CSC_ABORT_SUBSCRIPTION);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	@Override
	public void receive(Exception ex) {
		SCMPMessage reqMessage = this.request.getMessage();
		String sid = reqMessage.getSessionId();
		LOGGER.warn("receive exception sid=" + sid, ex);
		String serviceName = reqMessage.getServiceName();
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC csc abort subscription sid="
					+ sid);
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC csc abort subscription sid="
					+ sid);
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing csc abort subscription failed sid=" + sid);
		}
		// forward server reply to client
		fault.setIsReply(true);
		fault.setServiceName(serviceName);
		fault.setMessageType(SCMPMsgType.CSC_ABORT_SUBSCRIPTION);
		this.response.setSCMP(fault);
		this.responderCallback.responseCallback(request, response);
	}

	@Override
	public Subscription getSubscription() {
		return this.cascSubscription;
	}

	@Override
	public IRequest getRequest() {
		return this.request;
	}
}
