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
package com.stabilit.scm.sc.cmd.impl;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.log.IExceptionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.service.IFilterMask;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.SubscriptionQueue;
import com.stabilit.scm.sc.service.SCMPMessageFilterMask;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class ClnChangeSubscriptionCommand. Responsible for validation and execution of change subscription command.
 * Allows changing subscription mask on SC.
 * 
 * @author JTraber
 */
public class ClnChangeSubscriptionCommand extends CommandAdapter implements IPassThroughPartMsg {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnChangeSubscriptionCommand.class);

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

		Session session = this.getSubscriptionSessionById(sessionId);
		Server server = session.getServer();

		ISCMPSynchronousCallback callback = new CommandCallback();
		server.changeSubscription(reqMessage, callback, (Integer) request
				.getAttribute(SCMPHeaderAttributeKey.OP_TIMEOUT));
		SCMPMessage reply = callback.getMessageSync();

		if (reply.isFault() == false) {
			boolean rejectSessionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			if (Boolean.FALSE.equals(rejectSessionFlag)) {
				// session has not been rejected
				String newMask = reqMessage.getHeader(SCMPHeaderAttributeKey.MASK);
				SubscriptionQueue<SCMPMessage> queue = this.getSubscriptionQueueById(sessionId);
				IFilterMask<SCMPMessage> filterMask = new SCMPMessageFilterMask(newMask);
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
				String otiValue = message.getHeader(SCMPHeaderAttributeKey.OP_TIMEOUT.getValue());
				int oti = ValidatorUtility.validateInt(1, otiValue, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
				request.setAttribute(SCMPHeaderAttributeKey.OP_TIMEOUT, oti);
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
				IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
				exceptionLogger.logErrorException(logger, this.getClass().getName(), "validate", ex);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}
}