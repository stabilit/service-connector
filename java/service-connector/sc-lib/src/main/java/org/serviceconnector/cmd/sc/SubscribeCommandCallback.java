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

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.ISubscriptionCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPVersion;
import org.serviceconnector.service.IPublishService;
import org.serviceconnector.service.InvalidMaskLengthException;
import org.serviceconnector.service.ReceivePublicationTimeout;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ClnSubscribeCommandCallback.
 */
public class SubscribeCommandCallback implements ISCMPMessageCallback, ISubscriptionCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SubscribeCommandCallback.class);
	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The subscription. */
	private Subscription tempSubscription;
	/** The subscription registry. */
	private SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();
	/** The req message. */
	private SCMPMessage reqMessage;
	/** The msg type. */
	private String msgType;

	/**
	 * Instantiates a new ClnExecuteCommandCallback.
	 *
	 * @param request the request
	 * @param response the response
	 * @param callback the callback
	 * @param tempSubscription the subscription
	 */
	public SubscribeCommandCallback(IRequest request, IResponse response, IResponderCallback callback, Subscription tempSubscription) {
		this.responderCallback = callback;
		this.request = request;
		this.response = response;
		this.tempSubscription = tempSubscription;
		this.reqMessage = request.getMessage();
		this.msgType = reqMessage.getMessageType();
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		String serviceName = this.reqMessage.getServiceName();
		int noDataIntervalMillis = this.tempSubscription.getNoDataIntervalMillis();

		if (reply.isFault() == false) {
			boolean rejectSubscriptionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			if (rejectSubscriptionFlag == false) {
				if (tempSubscription.isCascaded() == true) {
					// update csc subscription id list for cascaded subscription
					tempSubscription.addCscSubscriptionId(this.reqMessage.getSessionId(), new SubscriptionMask(reqMessage.getHeader(SCMPHeaderAttributeKey.MASK)));
				}
				// subscription has not been rejected, add server to subscription
				PublishMessageQueue<SCMPMessage> publishMessageQueue = ((IPublishService) this.tempSubscription.getService()).getMessageQueue();
				ReceivePublicationTimeout crpTimeout = new ReceivePublicationTimeout(publishMessageQueue, noDataIntervalMillis);
				SubscriptionMask subscriptionMask = tempSubscription.getMask();
				publishMessageQueue.subscribe(tempSubscription.getId(), subscriptionMask, crpTimeout);
				// finally add subscription to the registry & schedule subscription timeout internal
				this.subscriptionRegistry.addSubscription(tempSubscription.getId(), tempSubscription);
				SubscriptionLogger.logSubscribe(serviceName, tempSubscription.getId(), subscriptionMask.getValue());
				// forward local subscription no matter what server sends
				reply.setSessionId(tempSubscription.getId());
			} else {
				// subscription has been rejected - remove subscription id from header
				reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
				// creation failed remove from server
				this.tempSubscription.getServer().removeSession(tempSubscription);
			}
		} else {
			reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
			// creation failed remove from server
			this.tempSubscription.getServer().removeSession(tempSubscription);
		}
		// forward reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(msgType);
		response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		String sid = this.reqMessage.getSessionId();
		LOGGER.warn("receive exception sid=" + sid + " " + ex.toString());
		// creation failed remove from server
		this.tempSubscription.getServer().removeSession(tempSubscription);
		SCMPMessage fault = null;
		String serviceName = this.reqMessage.getServiceName();
		SCMPVersion scmpVersion = this.reqMessage.getSCMPVersion();
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling - SCMP Version request
			fault = new SCMPMessageFault(scmpVersion, SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC cln subscribe sid=" + sid);
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(scmpVersion, SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln subscribe sid=" + sid);
		} else if (ex instanceof InterruptedException) {
			fault = new SCMPMessageFault(scmpVersion, SCMPError.SC_ERROR, "executing cln subscribe failed, thread interrupted sid=" + sid);
		} else if (ex instanceof InvalidMaskLengthException) {
			fault = new SCMPMessageFault(scmpVersion, SCMPError.HV_WRONG_MASK, ex.getMessage() + " sid=" + sid);
		} else {
			fault = new SCMPMessageFault(scmpVersion, SCMPError.SC_ERROR, "executing cln subscribe failed sid=" + sid);
		}
		// forward reply to client
		fault.setIsReply(true);
		fault.setServiceName(serviceName);
		fault.setMessageType(msgType);
		response.setSCMP(fault);
		this.responderCallback.responseCallback(request, response);
	}

	@Override
	public Subscription getSubscription() {
		return this.tempSubscription;
	}

	@Override
	public IRequest getRequest() {
		return request;
	}
}
