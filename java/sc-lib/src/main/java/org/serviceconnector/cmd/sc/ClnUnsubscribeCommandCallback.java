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
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.ISubscriptionCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.IStatefulServer;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.InvalidMaskLengthException;
import org.serviceconnector.service.Subscription;

/**
 * The Class ClnUnsubscribeCommandCallback.
 */
public class ClnUnsubscribeCommandCallback implements ISCMPMessageCallback, ISubscriptionCallback {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ClnUnsubscribeCommandCallback.class);
	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The subscription. */
	private Subscription subscription;

	/**
	 * Instantiates a new cln unsubscribe command callback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param responderCallback
	 *            the responder callback
	 * @param subscription
	 *            the subscription
	 */
	public ClnUnsubscribeCommandCallback(IRequest request, IResponse response, IResponderCallback responderCallback,
			Subscription subscription) {
		this.responderCallback = responderCallback;
		this.request = request;
		this.response = response;
		this.subscription = subscription;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		// free server from subscription
		IStatefulServer server = this.subscription.getServer();
		server.removeSession(subscription);
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// forward server reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CLN_UNSUBSCRIBE);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
		if (reply.isFault()) {
			// delete subscription failed
			if (server instanceof StatefulServer) {
				((StatefulServer) server).abortSessionsAndDestroy("unsubscribe failed, fault reply received in callback");
			} else {
				server.abortSession(subscription, "unsubscribe failed, fault reply received in callback");
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		SCMPMessage reqMessage = request.getMessage();
		String sid = reqMessage.getSessionId();
		String serviceName = reqMessage.getServiceName();
		LOGGER.warn("receive exception sid=" + sid + " service=", ex);
		// free server from subscription
		IStatefulServer server = this.subscription.getServer();
		server.removeSession(subscription);
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC cln unsubscribe sid=" + sid);
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln unsubscribe sid=" + sid);
		} else if (ex instanceof InvalidMaskLengthException) {
			fault = new SCMPMessageFault(SCMPError.HV_WRONG_MASK, ex.getMessage() + "sid=" + sid);
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing cln unsubscribe failed sid=" + sid);
		}
		// forward server reply to client
		fault.setIsReply(true);
		fault.setServiceName(serviceName);
		fault.setMessageType(SCMPMsgType.CLN_UNSUBSCRIBE);
		this.response.setSCMP(fault);
		this.responderCallback.responseCallback(request, response);
		// delete subscription failed
		if (server instanceof StatefulServer) {
			((StatefulServer) server).abortSessionsAndDestroy("unsubscribe subscription failed, exception received in callback");
		} else {
			server.abortSession(subscription, "unsubscribe failed, exception received in callback");
		}
	}

	/** {@inheritDoc} */
	@Override
	public Subscription getSubscription() {
		return this.subscription;
	}

	@Override
	public IRequest getRequest() {
		return request;
	}
}
