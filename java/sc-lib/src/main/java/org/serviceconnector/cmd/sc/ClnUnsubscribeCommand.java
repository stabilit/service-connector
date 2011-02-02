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
import org.serviceconnector.casc.CascSCUnsubscribeCallback;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
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
public class ClnUnsubscribeCommand extends CommandAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(ClnUnsubscribeCommand.class);

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
		Service abstractService = this.validateService(serviceName);
		String cascSubscriptionId = reqMessage.getHeader(SCMPHeaderAttributeKey.CASCADED_SUBSCRIPTION_ID);
		Subscription cascSubscription = this.subscriptionRegistry.getSubscription(cascSubscriptionId);
		String cascadedSCMask = reqMessage.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK);
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

		switch (abstractService.getType()) {
		case CASCADED_PUBLISH_SERVICE:
			CascadedPublishService cascadedPublishService = (CascadedPublishService) abstractService;
			// publish service is cascaded
			CascadedSC cascadedSC = cascadedPublishService.getCascadedSC();

			if (cascSubscription != null) {
				SubscriptionQueue<SCMPMessage> queue = ((IPublishService) cascSubscription.getService()).getSubscriptionQueue();
				if (cascadedSCMask == null) {
					// first remove subscription than unsubscribe
					this.subscriptionRegistry.removeSubscription(cascSubscription.getId());
					queue.unsubscribe(cascSubscription.getId());
					SubscriptionLogger.logUnsubscribe(serviceName, cascSubscription.getId());
				} else {
					// change subscription for cascaded SC
					SubscriptionMask cascSCMask = new SubscriptionMask(cascadedSCMask);
					queue.changeSubscription(cascSubscription.getId(), cascSCMask);
					cascSubscription.setMask(cascSCMask);
					SubscriptionLogger.logChangeSubscribe(serviceName, cascSubscription.getId(), cascadedSCMask);
				}
				// service is cascaded - unsubscribe is made by a cascaded SC
				CascSCUnsubscribeCallback callback = new CascSCUnsubscribeCallback(request, response, responderCallback,
						cascSubscription);
				cascadedSC.cascadedSCUnsubscribe(cascadedPublishService.getCascClient(), reqMessage, callback, oti);
			} else {
				// service is cascaded - unsubscribe is made by a normal client
				String subscriptionId = reqMessage.getSessionId();
				// lookup session and checks properness
				Subscription subscription = this.getSubscriptionById(subscriptionId);
				// looks up subscription queue and stops publish mechanism
				SubscriptionQueue<SCMPMessage> subscriptionQueue = this.getSubscriptionQueueById(subscriptionId);
				// first remove subscription than unsubscribe
				this.subscriptionRegistry.removeSubscription(subscription);
				subscriptionQueue.unsubscribe(subscriptionId);
				// free server from subscription
				cascadedSC.removeSession(subscription);
				ClnUnsubscribeCommandCallback callback = new ClnUnsubscribeCommandCallback(request, response, responderCallback,
						subscription);
				cascadedSC.clientUnsubscribe(cascadedPublishService.getCascClient(), reqMessage, callback, oti);
			}
			return;
		}

		StatefulServer server = null;
		Subscription subscription = null;
		if (cascSubscription != null) {
			// request is from cascadedSC
			SubscriptionQueue<SCMPMessage> queue = ((IPublishService) cascSubscription.getService()).getSubscriptionQueue();
			if (cascadedSCMask == null) {
				// no mask is set - unsubscribe cascadedSC and forward client id to server
				// first remove subscription than unsubscribe
				this.subscriptionRegistry.removeSubscription(cascSubscription.getId());
				queue.unsubscribe(cascSubscription.getId());
				SubscriptionLogger.logUnsubscribe(serviceName, cascSubscription.getId());
				if (reqMessage.getSessionId() == null) {
					// no session id set, cascadedSC is unsubscribing himself on his own initiative
					// no need to forward to server
					SCMPMessage reply = new SCMPMessage();
					reply.setIsReply(true);
					reply.setServiceName(serviceName);
					reply.setMessageType(getKey());
					response.setSCMP(reply);
					responderCallback.responseCallback(request, response);
					return;
				}
			} else {
				// cascadedSC unsubscribe - change subscription for cascadedSC and forward unsubscribe to server
				// change subscription for cascaded SC
				SubscriptionMask cascSCMask = new SubscriptionMask(cascadedSCMask);
				queue.changeSubscription(cascSubscription.getId(), cascSCMask);
				cascSubscription.setMask(cascSCMask);
				SubscriptionLogger.logChangeSubscribe(serviceName, cascSubscription.getId(), cascadedSCMask);
			}
			// use server of cascaded client to unsubscribe
			server = (StatefulServer) cascSubscription.getServer();
			subscription = cascSubscription;
		} else {
			String subscriptionId = reqMessage.getSessionId();
			// lookup session and checks properness
			subscription = this.getSubscriptionById(subscriptionId);
			// normal unsubscribe process of a client
			// looks up subscription queue and stops publish mechanism
			SubscriptionQueue<SCMPMessage> subscriptionQueue = this.getSubscriptionQueueById(subscriptionId);
			// first remove subscription than unsubscribe
			this.subscriptionRegistry.removeSubscription(subscription);
			subscriptionQueue.unsubscribe(subscriptionId);
			SubscriptionLogger.logUnsubscribe(serviceName, subscriptionId);
			server = (StatefulServer) subscription.getServer();
		}
		// unsubscribe on backend server
		ISCMPMessageCallback callback;
		int otiOnSCMillis = (int) (oti * basicConf.getOperationTimeoutMultiplier());
		int tries = (otiOnSCMillis / Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		int i = 0;
		// Following loop implements the wait mechanism in case of a busy connection pool
		do {
			try {
				if (cascSubscription != null) {
					// set up callback in case of a cascaded client unsubscribe operation
					callback = new CascSCUnsubscribeCallback(request, response, responderCallback, subscription);
				} else {
					// set up callback for normal client unsubscribe operation
					callback = new ClnUnsubscribeCommandCallback(request, response, responderCallback, subscription);
				}
				server.unsubscribe(reqMessage, callback, otiOnSCMillis - (i * Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS));
				// no exception has been thrown - get out of wait loop
				break;
			} catch (ConnectionPoolBusyException ex) {
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					server.abortSession(subscription, "unsubscribe subscription failed, busy connection pool to server");
					logger.debug(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + reqMessage.getServiceName()));
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION, "service="
							+ reqMessage.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
			} catch (Exception e) {
				if (cascSubscription != null) {
					// free server from subscription
					server.removeSession(subscription);
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
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLength(1, sessionId, 256, SCMPError.HV_WRONG_SESSION_ID);
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