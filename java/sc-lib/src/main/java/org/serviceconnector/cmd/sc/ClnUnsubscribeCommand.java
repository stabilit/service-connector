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
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnUnsubscribeCommand. Responsible for validation and execution of unsubscribe command. Allows unsubscribing from a
 * publish service.
 */
public class ClnUnsubscribeCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnUnsubscribeCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_UNSUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String subscriptionId = reqMessage.getSessionId();
		this.subscriptionRegistry.getSubscription(subscriptionId);

		// lookup session and checks properness
		Subscription subscription = this.getSubscriptionById(subscriptionId);
		// delete entry from session registry
		// looks up subscription queue and stops publish mechanism
		SubscriptionQueue<SCMPMessage> subscriptionQueue = this.getSubscriptionQueueById(subscriptionId);
		// first remove subscription than unsubscribe
		this.subscriptionRegistry.removeSubscription(subscription);
		subscriptionQueue.unsubscribe(subscriptionId);
		// cancel subscription timeout
		this.subscriptionRegistry.cancelSubscriptionTimeout(subscription);
		String serviceName = reqMessage.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
		SubscriptionLogger.logUnsubscribe(serviceName, subscriptionId);

		// unsubscribe on backend server
		StatefulServer server = subscription.getServer();

		ClnUnsubscribeCommandCallback callback;
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
		int otiOnSCMillis = (int) (oti * basicConf.getOperationTimeoutMultiplier());
		int tries = (otiOnSCMillis / Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
		int i = 0;
		// Following loop implements the wait mechanism in case of a busy connection pool
		do {
			callback = new ClnUnsubscribeCommandCallback(request, response, responderCallback, subscription, server);
			try {
				server.unsubscribe(reqMessage, callback, otiOnSCMillis - (i * Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS));
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
			}
			// sleep for a while and then try again
			Thread.sleep(Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
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