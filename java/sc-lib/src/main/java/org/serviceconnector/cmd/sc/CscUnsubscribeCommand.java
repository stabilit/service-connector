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
import org.serviceconnector.casc.CscUnsubscribeCallbackForCasc;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
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

/**
 * The Class ClnUnsubscribeCommand. Responsible for validation and execution of unsubscribe command. Allows unsubscribing from a
 * publish service.
 */
public class CscUnsubscribeCommand extends CommandAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(CscUnsubscribeCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CSC_UNSUBSCRIBE;
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

			SubscriptionQueue<SCMPMessage> queue = ((IPublishService) cascSubscription.getService()).getSubscriptionQueue();
			if (cascadedSCMask == null) {
				// unsubscribe made by cascaded SC on behalf of his last client
				this.subscriptionRegistry.removeSubscription(cascSubscription.getId());
				queue.unsubscribe(cascSubscription.getId());
				cascSubscription.getServer().removeSession(cascSubscription);
				SubscriptionLogger.logUnsubscribe(serviceName, cascSubscription.getId());
			} else {
				// unsubscribe made by cascaded SC on behalf of his clients
				SubscriptionMask cascSCMask = new SubscriptionMask(cascadedSCMask);
				queue.changeSubscription(cascSubscription.getId(), cascSCMask);
				cascSubscription.setMask(cascSCMask);
				SubscriptionLogger.logChangeSubscribe(serviceName, cascSubscription.getId(), cascadedSCMask);
			}
			CscUnsubscribeCallbackForCasc callback = new CscUnsubscribeCallbackForCasc(request, response, responderCallback,
					cascadedPublishService.getCascClient());
			cascadedSC.cascadedSCUnsubscribe(cascadedPublishService.getCascClient(), reqMessage, callback, oti);
			return;
		}

		SubscriptionQueue<SCMPMessage> queue = ((IPublishService) cascSubscription.getService()).getSubscriptionQueue();
		if (cascadedSCMask == null) {
			// unsubscribe made by cascaded SC on behalf of his last client
			this.subscriptionRegistry.removeSubscription(cascSubscription.getId());
			queue.unsubscribe(cascSubscription.getId());
			cascSubscription.getServer().removeSession(cascSubscription);
			SubscriptionLogger.logUnsubscribe(serviceName, cascSubscription.getId());

			if (reqMessage.getSessionId() == null) {
				// no session id set, cascadedSC unsubscribe on his own because of an error
				SCMPMessage reply = new SCMPMessage();
				reply.setIsReply(true);
				reply.setServiceName(serviceName);
				reply.setMessageType(getKey());
				// no need to forward to server
				response.setSCMP(reply);
				responderCallback.responseCallback(request, response);
				return;
			}
		} else {
			// unsubscribe made by cascaded SC on behalf of his clients
			SubscriptionMask cascSCMask = new SubscriptionMask(cascadedSCMask);
			queue.changeSubscription(cascSubscription.getId(), cascSCMask);
			cascSubscription.setMask(cascSCMask);
			SubscriptionLogger.logChangeSubscribe(serviceName, cascSubscription.getId(), cascadedSCMask);
		}
		// use server of cascaded client to unsubscribe
		StatefulServer server = (StatefulServer) cascSubscription.getServer();

		// unsubscribe on backend server
		CscUnsubscribeCommandCallback callback;
		int otiOnSCMillis = (int) (oti * basicConf.getOperationTimeoutMultiplier());
		int tries = (otiOnSCMillis / Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		int i = 0;
		// Following loop implements the wait mechanism in case of a busy connection pool
		do {
			try {
				// set up callback for normal client unsubscribe operation
				callback = new CscUnsubscribeCommandCallback(request, response, responderCallback, cascSubscription);
				server.unsubscribe(reqMessage, callback, otiOnSCMillis - (i * Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS));
				// no exception has been thrown - get out of wait loop
				break;
			} catch (ConnectionPoolBusyException ex) {
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					logger.debug(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + reqMessage.getServiceName()));
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION, "service="
							+ reqMessage.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
			} catch (Exception e) {
				if (cascadedSCMask == null) {
					// free server from subscription if cascaded SC unsubscribes himself
					server.removeSession(cascSubscription);
				}
				throw e;
			}
			// sleep for a while and then try again
			Thread.sleep(Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		} while (++i < tries);
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
			ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(1000, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// cascadedSubscriptionId mandatory
			String cascadedSubscriptionId = message.getHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID);
			ValidatorUtility.validateStringLength(1, cascadedSubscriptionId, 256, SCMPError.HV_WRONG_SESSION_ID);
			// sessionInfo optional
			ValidatorUtility.validateStringLengthIgnoreNull(1, message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO), 256,
					SCMPError.HV_WRONG_SESSION_INFO);
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