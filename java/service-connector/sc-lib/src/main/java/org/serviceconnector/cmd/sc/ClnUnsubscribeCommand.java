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

import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
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
import org.serviceconnector.service.Service;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.util.ValidatorUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ClnUnsubscribeCommand. Responsible for validation and execution of unsubscribe command. Allows unsubscribing from a publish service.
 */
public class ClnUnsubscribeCommand extends CommandAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ClnUnsubscribeCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_UNSUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();

		// check service is present
		Service abstractService = this.getService(serviceName);
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
		// service is cascaded - unsubscribe is made by a normal client
		String subscriptionId = reqMessage.getSessionId();
		// lookup session and checks properness
		Subscription subscription = this.getSubscriptionById(subscriptionId);
		// looks up publish message queue and stops publish mechanism
		PublishMessageQueue<SCMPMessage> publishMessageQueue = this.getPublishMessageQueueById(subscription);
		// first remove subscription than unsubscribe
		this.subscriptionRegistry.removeSubscription(subscription);
		publishMessageQueue.unsubscribe(subscriptionId);

		switch (abstractService.getType()) {
			case CASCADED_PUBLISH_SERVICE:
			case CASCADED_CACHE_GUARDIAN:
				CascadedPublishService cascadedPublishService = (CascadedPublishService) abstractService;
				// publish service is cascaded
				CascadedSC cascadedSC = cascadedPublishService.getCascadedSC();
				// free server from subscription
				cascadedSC.removeSession(subscription);
				ClnUnsubscribeCommandCallback callback = new ClnUnsubscribeCommandCallback(request, response, responderCallback, subscription);
				cascadedSC.cascadedSCUnsubscribe(cascadedPublishService.getCascClient(), reqMessage, callback, oti);
				return;
			default:
				// code for other types of services is below
				break;
		}
		StatefulServer server = (StatefulServer) subscription.getServer();
		ClnUnsubscribeCommandCallback callback = null;
		int otiOnSCMillis = (int) (oti * basicConf.getOperationTimeoutMultiplier());
		int tries = (otiOnSCMillis / Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		int i = 0;
		// Following loop implements the wait mechanism in case of a busy connection pool
		do {
			// reset msgType, might have been modified in below unsubscribe try
			reqMessage.setMessageType(this.getKey());
			// set up callback for normal client unsubscribe operation
			callback = new ClnUnsubscribeCommandCallback(request, response, responderCallback, subscription);
			try {
				server.unsubscribe(reqMessage, callback, otiOnSCMillis - (i * Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS));
				// delete unreferenced nodes in queue
				publishMessageQueue.removeNonreferencedNodes();
				// no exception has been thrown - get out of wait loop
				break;
			} catch (ConnectionPoolBusyException ex) {
				LOGGER.debug("ConnectionPoolBusyException caught in wait mec of unsubscribe, tries left=" + tries);
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					server.abortSession(subscription, "unsubscribe subscription failed, connection pool to server busy");
					LOGGER.debug(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + reqMessage.getServiceName()));
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION, "service=" + reqMessage.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
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
			ValidatorUtility.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME, SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(Constants.MIN_OTI_VALUE_CLN, otiValue, Constants.MAX_OTI_VALUE, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, sessionId, Constants.MAX_STRING_LENGTH_256, SCMPError.HV_WRONG_SESSION_ID);
			// sessionInfo optional
			ValidatorUtility.validateStringLengthIgnoreNull(1, message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO), Constants.MAX_STRING_LENGTH_256,
					SCMPError.HV_WRONG_SESSION_INFO);
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
