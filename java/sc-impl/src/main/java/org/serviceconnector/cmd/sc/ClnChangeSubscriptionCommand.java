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
import org.serviceconnector.cmd.ICommandValidator;
import org.serviceconnector.cmd.IPassThroughPartMsg;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.ISCMPSynchronousCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.IFilterMask;
import org.serviceconnector.service.SCMPMessageFilterMask;
import org.serviceconnector.service.Server;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ValidatorUtility;


/**
 * The Class ClnChangeSubscriptionCommand. Responsible for validation and execution of change subscription command.
 * Allows changing subscription mask on SC.
 * 
 * @author JTraber
 */
public class ClnChangeSubscriptionCommand extends CommandAdapter implements IPassThroughPartMsg {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnChangeSubscriptionCommand.class);

	/** The Constant subscriptionLogger. */
	private final static SubscriptionLogger subscriptionLogger = SubscriptionLogger.getInstance();

	/**
	 * Instantiates a new ClnChangeSubscriptionCommand.
	 */
	public ClnChangeSubscriptionCommand() {
		this.commandValidator = new ClnChangeSubscriptionCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_CHANGE_SUBSCRIPTION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String sessionId = reqMessage.getSessionId();
		String serviceName = reqMessage.getServiceName();

		Session session = this.getSubscriptionSessionById(sessionId);
		Server server = session.getServer();

		ISCMPSynchronousCallback callback = new CommandCallback(true);
		server.changeSubscription(reqMessage, callback, ((Integer) request
				.getAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT)));
		SCMPMessage reply = callback.getMessageSync();

		if (reply.isFault() == false) {
			boolean rejectSessionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			if (Boolean.FALSE.equals(rejectSessionFlag)) {
				// session has not been rejected
				String newMask = reqMessage.getHeader(SCMPHeaderAttributeKey.MASK);
				SubscriptionQueue<SCMPMessage> queue = this.getSubscriptionQueueById(sessionId);
				IFilterMask<SCMPMessage> filterMask = new SCMPMessageFilterMask(newMask);
				subscriptionLogger.logChangeSubscribe(serviceName, sessionId, newMask);
				queue.changeSubscription(sessionId, filterMask);
			} else {
				// session has been rejected - remove session id from header
				reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
			}
		} else {
			reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
		}
		// forward reply to client
		reply.setIsReply(true);
		reply.setMessageType(getKey());
		response.setSCMP(reply);
	}

	/**
	 * The Class ClnChangeSubscriptionCommandValidator.
	 */
	private class ClnChangeSubscriptionCommandValidator implements ICommandValidator {

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
				int oti = ValidatorUtility.validateInt(10, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
				request.setAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, oti);
				// mask
				String mask = (String) message.getHeader(SCMPHeaderAttributeKey.MASK);
				ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
				if (mask.indexOf("%") != -1) {
					// percent sign in mask not allowed
					throw new SCMPValidatorException(SCMPError.HV_WRONG_MASK, "Percent sign not allowed " + mask);
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