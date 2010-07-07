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
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.net.ICommunicatorCallback;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.ISubscriptionPlace;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.registry.SubscriptionPlaceException;
import com.stabilit.scm.sc.registry.SubscriptionSessionRegistry;
import com.stabilit.scm.sc.service.Session;

public class ReceivePublicationCommand extends CommandAdapter implements IPassThroughPartMsg, IAsyncCommand {

	public ReceivePublicationCommand() {
		this.commandValidator = new ClnReceivePublicationCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.RECEIVE_PUBLICATION;
	}

	@Override
	public boolean isAsynchronous() {
		return true;
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, ICommunicatorCallback communicatorCallback) throws Exception {
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		try {
			ISubscriptionPlace subscriptionPlace = this.getSubscriptionPlace(sessionId);
			if (subscriptionPlace == null) {
				throw new SubscriptionPlaceException("no place found for session id = " + sessionId);
			}
			Object data = subscriptionPlace.poll(message); // no callback necessary, returns immediately if data is
			// ready otherwise a
			// future publish will check this poll
			if (data != null) {
				SCMPMessage reply = new SCMPMessage();
				reply.setServiceName((String) request.getAttribute(SCMPHeaderAttributeKey.SERVICE_NAME));
				reply.setSessionId((String) request.getAttribute(SCMPHeaderAttributeKey.SESSION_ID));
				reply.setMessageType((String) request.getAttribute(SCMPHeaderAttributeKey.MSG_TYPE));
				reply.setIsReply(true);
				if (data instanceof SCMPMessage) {
					reply.setBody(((SCMPMessage) data).getBody());
				}
				response.setSCMP(reply);
				try {
					response.write();
					return;
				} catch (Exception e) {
					ExceptionPoint.getInstance().fireException(this, e);
					return;
				} finally {
				}
			}
			// no data available, start listening for new data
			subscriptionPlace.listen(sessionId, request, response);
		} catch (SCServiceException e) {
			// failed, connection to backend server disturbed - clean up
			// TODO clean up??
			SessionRegistry.getCurrentInstance().removeSession(message.getSessionId());
			ExceptionPoint.getInstance().fireException(this, e);
			HasFaultResponseException communicationException = new SCMPCommunicationException(SCMPError.SERVER_ERROR);
			communicationException.setMessageType(getKey());
			throw communicationException;
		}
	}

	private ISubscriptionPlace getSubscriptionPlace(String sessionId) throws Exception {
		SubscriptionSessionRegistry subscriptionSessionRegistry = SubscriptionSessionRegistry.getCurrentInstance();
		Session session = subscriptionSessionRegistry.getSession(sessionId);

		if (session == null) {
			// incoming session not found
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: no session found for id :" + sessionId);
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_SESSION_FOUND);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return session.getServer().getService().getSubscriptionPlace();
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
					int noDataInterval = ValidatorUtility.validateInt(1, noDataIntervalValue, 3601);
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