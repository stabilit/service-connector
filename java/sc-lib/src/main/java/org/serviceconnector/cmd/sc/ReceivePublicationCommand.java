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

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ReceivePublicationCommand. Tries polling messages from subscription queue. If no message is available a listen is set
 * up. Receive publication command runs asynchronously and passes through any parts messages.
 * 
 * @author JTraber
 */
public class ReceivePublicationCommand extends CommandAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ReceivePublicationCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.RECEIVE_PUBLICATION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String subscriptionId = reqMessage.getSessionId();
		SCMPMessage message = null;
		Subscription subscription = this.getSubscriptionById(subscriptionId);
		// looks up subscription queue
		PublishMessageQueue<SCMPMessage> publishMessageQueue = this.getPublishMessageQueueById(subscription);
		synchronized (publishMessageQueue) {
			// reset subscription timeout to NOI+ECI
			this.subscriptionRegistry.resetSubscriptionTimeout(subscription,
					subscription.getNoDataIntervalMillis() + subscription.getSubscriptionTimeoutMillis());
			// tries polling message
			message = publishMessageQueue.getMessageOrListen(subscriptionId, request, response);
			if (message == null) {
				// no message available, switched to listening mode for new message
				return;
			}
		}
		LOGGER.debug("CRP message found in queue subscriptionId " + subscriptionId);
		// message found in subscription queue set up reply
		SCMPMessage reply = new SCMPMessage();
		if (message.isPart()) {
			// message from queue is of type part - outgoing must be part too, no poll request
			reply = new SCMPPart(false);
		}
		reply.setServiceName(reqMessage.getServiceName());
		reply.setSessionId(reqMessage.getSessionId());
		reply.setMessageType(reqMessage.getMessageType());
		reply.setIsReply(true);
		reply.setBody(message.getBody());
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, message.getMessageSequenceNr());
		String messageInfo = message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
		if (messageInfo != null) {
			reply.setHeader(SCMPHeaderAttributeKey.MSG_INFO, messageInfo);
		}
		reply.setHeader(SCMPHeaderAttributeKey.MASK, message.getHeader(SCMPHeaderAttributeKey.MASK));
		response.setSCMP(reply);
		// reset subscription timeout to ECI
		this.subscriptionRegistry.resetSubscriptionTimeout(subscription, subscription.getSubscriptionTimeoutMillis());
		// message already gotten from queue no asynchronous process necessary call callback right away
		responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();
		try {
			// msgSequenceNr mandatory
			String msgSequenceNr = message.getMessageSequenceNr();
			ValidatorUtility.validateLong(1, msgSequenceNr, SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR);
			// serviceName mandatory
			String serviceName = message.getServiceName();
			ValidatorUtility.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME,
					SCMPError.HV_WRONG_SERVICE_NAME);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, sessionId, Constants.MAX_STRING_LENGTH_256, SCMPError.HV_WRONG_SESSION_ID);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			LOGGER.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}
}
