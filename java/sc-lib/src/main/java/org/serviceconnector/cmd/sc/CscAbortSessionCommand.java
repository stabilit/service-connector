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
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.cmd.casc.CscAbortSubscriptionCallback;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.CascadedPublishService;
import org.serviceconnector.service.IPublishService;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;
import org.serviceconnector.util.ValidatorUtility;

public class CscAbortSessionCommand extends CommandAdapter {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(CscAbortSessionCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CSC_ABORT_SESSION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();

		// check service is present
		Service abstractService = this.getService(serviceName);
		String cascSubscriptionId = reqMessage.getHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID);
		Subscription cascSubscription = this.getSubscriptionById(cascSubscriptionId);
		String cascadedSCMask = reqMessage.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK);
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

		switch (abstractService.getType()) {
		case CASCADED_PUBLISH_SERVICE:
			CascadedPublishService cascadedPublishService = (CascadedPublishService) abstractService;
			// publish service is cascaded
			CascadedSC cascadedSC = cascadedPublishService.getCascadedSC();

			PublishMessageQueue<SCMPMessage> queue = ((IPublishService) cascSubscription.getService()).getMessageQueue();
			if (cascadedSCMask == null) {
				// subscription abort made by cascaded SC on behalf of his last client
				this.subscriptionRegistry.removeSubscription(cascSubscription.getId());
				queue.unsubscribe(cascSubscription.getId());
				cascSubscription.getServer().removeSession(cascSubscription);
				SubscriptionLogger.logUnsubscribe(serviceName, cascSubscription.getId());
			} else {
				// unsubscribe made by cascaded SC on behalf of a clients, others are left
				SubscriptionMask cascSCMask = new SubscriptionMask(cascadedSCMask);
				queue.changeSubscription(cascSubscription.getId(), cascSCMask);
				cascSubscription.setMask(cascSCMask);
				SubscriptionLogger.logChangeSubscribe(serviceName, cascSubscription.getId(), cascadedSCMask);
			}
			CscAbortSubscriptionCallback callback = new CscAbortSubscriptionCallback(request, cascSubscription);
			cascadedSC.cascadedSCAbortSubscription(cascadedPublishService.getCascClient(), reqMessage, callback, oti);
			return;
		}
		StatefulServer server = (StatefulServer) cascSubscription.getServer();

		PublishMessageQueue<SCMPMessage> publishMessageQueue = ((IPublishService) cascSubscription.getService()).getMessageQueue();
		if (cascadedSCMask == null) {
			// subscription abort made by cascaded SC on behalf of his last client
			this.subscriptionRegistry.removeSubscription(cascSubscription.getId());
			publishMessageQueue.unsubscribe(cascSubscription.getId());
			cascSubscription.getServer().removeSession(cascSubscription);
			SubscriptionLogger.logUnsubscribe(serviceName, cascSubscription.getId());
		} else {
			// unsubscribe made by cascaded SC on behalf of a clients, others are left
			SubscriptionMask cascSCMask = new SubscriptionMask(cascadedSCMask);
			publishMessageQueue.changeSubscription(cascSubscription.getId(), cascSCMask);
			cascSubscription.setMask(cascSCMask);
			SubscriptionLogger.logChangeSubscribe(serviceName, cascSubscription.getId(), cascadedSCMask);
		}
		// set up abort message
		SCMPMessage abortMessage = new SCMPMessage();
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, SCMPError.SESSION_ABORT
				.getErrorText("Cascaded subscription abort received."));
		abortMessage.setServiceName(serviceName);
		abortMessage.setSessionId(reqMessage.getSessionId());
		abortMessage.setHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, oti);
		server.abortSessionAndWaitMech(oti, abortMessage, "Cascaded subscription abort received");
		// reply to client
		reqMessage.setIsReply(true);
		response.setSCMP(reqMessage);
		responderCallback.responseCallback(request, response);
		// delete unreferenced nodes in queue
		publishMessageQueue.removeNonreferencedNodes();
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
			ValidatorUtility.validateStringLengthTrim(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(1000, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// cascadedSubscriptionId mandatory
			String cascadedSubscriptionId = message.getHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID);
			ValidatorUtility.validateStringLengthTrim(1, cascadedSubscriptionId, 256, SCMPError.HV_WRONG_SESSION_ID);
			// subscriptionId mandatory
			String subscriptionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, subscriptionId, 256, SCMPError.HV_WRONG_SESSION_ID);
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