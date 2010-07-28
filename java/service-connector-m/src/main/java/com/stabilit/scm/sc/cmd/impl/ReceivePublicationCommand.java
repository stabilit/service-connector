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

import com.stabilit.scm.common.cmd.IAsyncCommand;
import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.IResponderCallback;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.ISubscriptionPlace;

/**
 * The Class ReceivePublicationCommand. Tries polling messages from subscription queue. If no message is available a
 * listen is set up.
 * 
 * @author JTraber
 */
public class ReceivePublicationCommand extends CommandAdapter implements IPassThroughPartMsg, IAsyncCommand {

	public ReceivePublicationCommand() {
		this.commandValidator = new ClnReceivePublicationCommandValidator();
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
		String sessionId = reqMessage.getSessionId();

		// looks up subscription place
		ISubscriptionPlace<SCMPMessage> subscriptionPlace = this.getSubscriptionPlaceById(sessionId);
		// tries polling messages
		SCMPMessage message = subscriptionPlace.poll(sessionId);
		if (message != null) {
			// message found in subscription queue set up reply
			SCMPMessage reply = new SCMPMessage();
			reply.setServiceName((String) request.getAttribute(SCMPHeaderAttributeKey.SERVICE_NAME));
			reply.setSessionId((String) request.getAttribute(SCMPHeaderAttributeKey.SESSION_ID));
			reply.setMessageType((String) request.getAttribute(SCMPHeaderAttributeKey.MSG_TYPE));
			reply.setIsReply(true);
			reply.setBody(message.getBody());
			response.setSCMP(reply);
			// message already gotten from queue no asynchronous process necessary call callback right away
			communicatorCallback.callback(request, response);
		}
		// no message available, start listening for new message
		subscriptionPlace.listen(sessionId, request, response);
	}

	private class ClnReceivePublicationCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();
			try {
				// serviceName
				String serviceName = message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}

				// noDataInterval
				// TODO integer validierung
				String noDataIntervalValue = message.getHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);
				if (noDataIntervalValue == null) {
					request.setAttribute(SCMPHeaderAttributeKey.NO_DATA_INTERVAL, 360);
				} else {
					int noDataInterval = ValidatorUtility.validateInt(1, noDataIntervalValue, 3600);
					request.setAttribute(SCMPHeaderAttributeKey.NO_DATA_INTERVAL, noDataInterval);
				}
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
}