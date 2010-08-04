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

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.req.netty.OperationTimeoutException;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.service.SCSessionException;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class ClnChangeSubscriptionCommand. Responsible for validation and execution of change subscription command.
 * Allows changing subscription mask on SC.
 * 
 * @author JTraber
 */
public class ClnChangeSubscriptionCommand extends CommandAdapter implements IPassThroughPartMsg {

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
	public void run(IRequest request, IResponse response) throws Throwable {
		SCMPMessage reqMessage = request.getMessage();
		String sessionId = reqMessage.getSessionId();

		Session session = this.getSessionById(sessionId);
		Server server = session.getServer();

		ClnChangeSubscriptionCommandCallback callback = new ClnChangeSubscriptionCommandCallback();
		server.changeSubscription(reqMessage, callback);
		SCMPMessage reply = callback.getMessageSync();

		if (reply.isFault()) {
			// exception handling
			SCMPFault fault = (SCMPFault) reply;
			Throwable th = fault.getCause();
			if (th instanceof OperationTimeoutException) {
				// operation timeout handling
				HasFaultResponseException scmpEx = new SCMPCommandException(SCMPError.OPERATION_TIMEOUT);
				scmpEx.setMessageType(getKey());
				throw scmpEx;
			}
			throw th;
		}
		Boolean rejectSessionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
		if (Boolean.TRUE.equals(rejectSessionFlag)) {
			reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
			// server rejected session - throw exception with server errors
			SCSessionException e = new SCSessionException(SCMPError.SESSION_REJECTED, reply.getHeader());
			e.setMessageType(getKey());
			throw e;
		}

		// forward reply to client
		reply.setIsReply(true);
		reply.setMessageType(getKey());
		reply.setSessionId(sessionId);
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
					throw new SCMPValidatorException("messageId must be set!");
				}
				// serviceName
				String serviceName = message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
				// mask
				String mask = (String) message.getHeader(SCMPHeaderAttributeKey.MASK);
				if (mask == null) {
					throw new SCMPValidatorException("mask must be set!");
				}
				if (mask.indexOf("%") != -1) {
					// percent sign in mask not allowed
					throw new SCMPValidatorException("percent sign found in mask - not allowed.");
				}
				ValidatorUtility.validateString(1, mask, 256);
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(getKey());
				throw ex;
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}

	/**
	 * The Class ClnChangeSubscriptionCommandCallback.
	 */
	private class ClnChangeSubscriptionCommandCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}