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
import org.serviceconnector.cmd.IAsyncCommand;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;

/**
 * The Class ReceivePublicationCommand. Tries polling messages from subscription queue. If no message is available a listen is set
 * up. Receive publication command runs asynchronously and passes through any parts messages.
 * 
 * @author JTraber
 */
public class ReceivePublicationCommand extends CommandAdapter implements IAsyncCommand {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ReceivePublicationCommand.class);

	/**
	 * Instantiates a new ReceivePublicationCommand.
	 */
	public ReceivePublicationCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.RECEIVE_PUBLICATION;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAsynchronous() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback communicatorCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String subscriptionId = reqMessage.getSessionId();
		SCMPMessage message = null;
		// looks up subscription queue
		SubscriptionQueue<SCMPMessage> subscriptionQueue = this.getSubscriptionQueueById(subscriptionId);
		synchronized (subscriptionQueue) {
			// cancel subscription timeout
			this.subscriptionRegistry.cancelSubscriptionTimeout(subscriptionId);
			// tries polling message
			message = subscriptionQueue.getMessageOrListen(subscriptionId, request, response);
			if (message == null) {
				// no message available, switched to listening mode for new message
				return;
			}
		}
		logger.debug("CRP message found in queue subscriptionId " + subscriptionId);
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
		// set up subscription timeout again
		this.subscriptionRegistry.scheduleSubscriptionTimeout(subscriptionId);
		// message already gotten from queue no asynchronous process necessary call callback right away
		communicatorCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();
		try {
			// msgSequenceNr
			String msgSequenceNr = message.getMessageSequenceNr();
			if (msgSequenceNr == null || msgSequenceNr.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR, "msgSequenceNr must be set");
			}
			// serviceName
			String serviceName = message.getServiceName();
			if (serviceName == null || serviceName.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
			}
			// sessionId
			String sessionId = message.getSessionId();
			if (sessionId == null || sessionId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
			}
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			logger.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}
}
