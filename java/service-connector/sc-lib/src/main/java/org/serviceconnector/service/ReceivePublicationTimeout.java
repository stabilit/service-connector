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
package org.serviceconnector.service;

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.util.ITimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ReceivePublicationTimeout. ReceivePublicationTimeout defines action to get in place when receive publication times out or a new publish message arrives.
 */
public class ReceivePublicationTimeout implements ITimeout {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ReceivePublicationTimeout.class);
	/** The subscription registry. */
	private SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();
	/** The noDataIntervalMillis. */
	private int noDataIntervalMillis;
	/** The subscription queue. */
	private PublishMessageQueue<SCMPMessage> publishMessageQueue;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;

	/**
	 * Instantiates a new publish timer run.
	 *
	 * @param publishMessageQueue the publishMessageQueue
	 * @param noDataIntervalMillis the timeout
	 */
	public ReceivePublicationTimeout(PublishMessageQueue<SCMPMessage> publishMessageQueue, int noDataIntervalMillis) {
		this.request = null;
		this.response = null;
		this.noDataIntervalMillis = noDataIntervalMillis;
		this.publishMessageQueue = publishMessageQueue;
	}

	/** {@inheritDoc} */
	@Override
	public int getTimeoutMillis() {
		return this.noDataIntervalMillis;
	}

	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	public void setRequest(IRequest request) {
		this.request = request;
	}

	/**
	 * Sets the response.
	 *
	 * @param response the new response
	 */
	public void setResponse(IResponse response) {
		this.response = response;
	}

	/** {@inheritDoc} */
	@Override
	public void timeout() {
		LOGGER.trace("timeout receivePublicationTimeout");
		String subscriptionId = null;
		Subscription subscription = null;
		// extracting subscriptionId from request message
		SCMPMessage reqMsg = request.getMessage();

		try {
			// set up subscription timeout
			subscriptionId = reqMsg.getSessionId();

			LOGGER.trace("timeout receive publication timer datapointer subscriptionId " + subscriptionId);
			subscription = subscriptionRegistry.getSubscription(subscriptionId);
			if (subscription == null) {
				LOGGER.trace("subscription not found - already deleted subscriptionId=" + subscriptionId);
				// subscription has already been deleted
				SCMPMessageFault fault = new SCMPMessageFault(reqMsg.getSCMPVersion(), SCMPError.SUBSCRIPTION_NOT_FOUND, subscriptionId);
				fault.setMessageType(reqMsg.getMessageType());
				response.setSCMP(fault);
			} else {
				// tries polling from queue
				SCMPMessage message = this.publishMessageQueue.getMessage(subscriptionId);
				if (message == null) {
					LOGGER.trace("no message found on queue - subscription timeout set up no data message subscriptionId=" + subscriptionId);
					// no message found on queue - subscription timeout set up no data message
					reqMsg.setHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
					reqMsg.setIsReply(true);
					this.response.setSCMP(reqMsg);
				} else {
					// message polling successful
					LOGGER.trace("message found on queue - subscription timeout set up reply message subscriptionId=" + subscriptionId);
					// set up reply
					SCMPMessage reply = null;
					if (message.isPart()) {
						// message from queue is of type part - outgoing must be part too, no poll request
						reply = new SCMPPart(message.getSCMPVersion(), false, message.getHeader());
					} else {
						reply = new SCMPMessage(message.getSCMPVersion(), message.getHeader());
					}
					reply.setSessionId(subscriptionId);
					reply.setMessageType(reqMsg.getMessageType());
					reply.setIsReply(true);
					reply.setBody(message.getBody());
					this.response.setSCMP(reply);
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("timeout expired procedure failed, " + ex.getMessage());
			SCMPMessageFault scmpFault = new SCMPMessageFault(reqMsg.getSCMPVersion(), SCMPError.SERVER_ERROR, ex.getMessage());
			scmpFault.setMessageType(SCMPMsgType.RECEIVE_PUBLICATION);
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
		} finally {
			if (subscription != null) {
				// reset subscription timeout to ECI
				subscriptionRegistry.resetSubscriptionTimeout(subscription, subscription.getSubscriptionTimeoutMillis());
			}
			// send message back to client
			try {
				this.response.write();
			} catch (Exception e) {
				LOGGER.warn("timeout expired procedure failed, " + e.getMessage());
			}
		}
	}
}
