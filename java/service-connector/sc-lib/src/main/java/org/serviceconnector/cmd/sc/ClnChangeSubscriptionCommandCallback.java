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
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.ISubscriptionCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.IPublishService;
import org.serviceconnector.service.InvalidMaskLengthException;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;

/**
 * The Class ClnChangeSubscriptionCommandCallback.
 */
public class ClnChangeSubscriptionCommandCallback implements ISCMPMessageCallback, ISubscriptionCallback {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ClnChangeSubscriptionCommandCallback.class);
	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The subscription. */
	private Subscription subscription;

	/**
	 * Instantiates a new cln change subscription command callback.
	 *
	 * @param request the request
	 * @param response the response
	 * @param responderCallback the responder callback
	 * @param subscription the subscription
	 */
	public ClnChangeSubscriptionCommandCallback(IRequest request, IResponse response, IResponderCallback responderCallback, Subscription subscription) {
		this.responderCallback = responderCallback;
		this.request = request;
		this.response = response;
		this.subscription = subscription;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		String subscriptionId = subscription.getId();
		if (reply.isFault() == false) {
			boolean rejectSubscriptionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			if (rejectSubscriptionFlag == false) {
				// session has not been rejected
				String newMask = reqMessage.getHeader(SCMPHeaderAttributeKey.MASK);
				PublishMessageQueue<SCMPMessage> queue = ((IPublishService) subscription.getService()).getMessageQueue();
				SubscriptionMask mask = new SubscriptionMask(newMask);
				SubscriptionLogger.logChangeSubscribe(serviceName, subscriptionId, newMask);
				queue.changeSubscription(subscriptionId, mask);
				subscription.setMask(mask);
			}
		}
		// forward reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CLN_CHANGE_SUBSCRIPTION);
		reply.setSessionId(subscriptionId);
		response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		String sid = subscription.getId();
		LOGGER.warn("receive exception sid=" + sid + " " + ex.toString());
		SCMPMessage fault = null;
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling - SCMP Version request
			fault = new SCMPMessageFault(reqMessage.getSCMPVersion(), SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC cln change subscription sid=" + sid);
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(reqMessage.getSCMPVersion(), SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln change subscription sid=" + sid);
		} else if (ex instanceof InvalidMaskLengthException) {
			fault = new SCMPMessageFault(reqMessage.getSCMPVersion(), SCMPError.HV_WRONG_MASK, ex.getMessage() + " sid=" + sid);
		} else {
			fault = new SCMPMessageFault(reqMessage.getSCMPVersion(), SCMPError.SC_ERROR, "executing cln change subscription failed sid=" + sid);
		}
		fault.setIsReply(true);
		fault.setServiceName(serviceName);
		fault.setMessageType(SCMPMsgType.CLN_CHANGE_SUBSCRIPTION);
		fault.setSessionId(sid);
		response.setSCMP(fault);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public IRequest getRequest() {
		return this.request;
	}

	/** {@inheritDoc} */
	@Override
	public Subscription getSubscription() {
		return this.subscription;
	}
}
