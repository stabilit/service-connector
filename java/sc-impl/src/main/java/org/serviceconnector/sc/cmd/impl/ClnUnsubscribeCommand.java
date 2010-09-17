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
package org.serviceconnector.sc.cmd.impl;

import org.apache.log4j.Logger;
import org.serviceconnector.common.cmd.ICommandValidator;
import org.serviceconnector.common.cmd.IPassThroughPartMsg;
import org.serviceconnector.common.cmd.SCMPValidatorException;
import org.serviceconnector.common.conf.Constants;
import org.serviceconnector.common.log.ISubscriptionLogger;
import org.serviceconnector.common.log.impl.SubscriptionLogger;
import org.serviceconnector.common.scmp.HasFaultResponseException;
import org.serviceconnector.common.scmp.IRequest;
import org.serviceconnector.common.scmp.IResponse;
import org.serviceconnector.common.scmp.ISCMPSynchronousCallback;
import org.serviceconnector.common.scmp.SCMPError;
import org.serviceconnector.common.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.scmp.SCMPMsgType;
import org.serviceconnector.common.util.ValidatorUtility;
import org.serviceconnector.sc.registry.SubscriptionQueue;
import org.serviceconnector.sc.registry.SubscriptionSessionRegistry;
import org.serviceconnector.sc.service.Server;
import org.serviceconnector.sc.service.Session;


/**
 * The Class ClnUnsubscribeCommand. Responsible for validation and execution of unsubscribe command. Allows
 * unsubscribing from a publish service.
 */
public class ClnUnsubscribeCommand extends CommandAdapter implements IPassThroughPartMsg {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnUnsubscribeCommand.class);

	/** The Constant subscriptionLogger. */
	private final static ISubscriptionLogger subscriptionLogger = SubscriptionLogger.getInstance();

	public ClnUnsubscribeCommand() {
		this.commandValidator = new ClnUnsubscribeCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_UNSUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String sessionId = reqMessage.getSessionId();
		SubscriptionSessionRegistry.getCurrentInstance().getSession(sessionId);

		// lookup session and checks properness
		Session session = this.getSubscriptionSessionById(sessionId);
		// looks up subscription queue and stops publish mechanism
		SubscriptionQueue<SCMPMessage> subscriptionQueue = this.getSubscriptionQueueById(sessionId);
		subscriptionQueue.unsubscribe(sessionId);
		String serviceName = reqMessage.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
		subscriptionLogger.logUnsubscribe(serviceName, sessionId);
		// delete entry from session registry
		this.subscriptionRegistry.removeSession(session);

		// unsubscribe on backend server
		Server server = session.getServer();
		SCMPMessage reply = null;
		ISCMPSynchronousCallback callback = new CommandCallback(true);
		server.unsubscribe(reqMessage, callback,
				((Integer) request.getAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT)));
		reply = callback.getMessageSync();
		// no specific error handling in case of fault - everything is done anyway

		server.removeSession(session);
		// forward reply to client
		reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
		reply.setIsReply(true);
		reply.setMessageType(this.getKey());
		response.setSCMP(reply);
	}

	/**
	 * The Class ClnUnsubscribeCommandValidator.
	 */
	private class ClnUnsubscribeCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();
			try {
				// messageId
				String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID);
				if (messageId == null || messageId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_ID, "messageId must be set");
				}
				// serviceName
				String serviceName = message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
				}
				// operation timeout
				String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT.getValue());
				int oti = ValidatorUtility.validateInt(1, otiValue, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
				request.setAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, oti);
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
				}
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(getKey());
				throw ex;
			} catch (Throwable ex) {
				logger.error("validate", ex);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}
}