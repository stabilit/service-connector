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


import javax.xml.bind.ValidationException;

import com.stabilit.scm.common.cmd.ICommandCallback;
import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.service.SCServiceException;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class ClnDataCommand. Responsible for validation and execution of data command. Data command sends any data to a
 * server.
 * 
 * @author JTraber
 */
public class ClnDataCommand extends AsyncCommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a new ClnDataCommand.
	 */
	public ClnDataCommand() {
		this.commandValidator = new ClnDataCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_DATA;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		Session session = getSessionById(sessionId);

		Server server = session.getServer();
		try {
			// try sending to backend server
			SCMPMessage scmpReply = server.sendData(message);
			scmpReply.setMessageType(getKey().getName());
			response.setSCMP(scmpReply);
		} catch (SCServiceException e) {
			// clnDatat failed, connection to backend server disturbed - clean up
			// TODO clean up??
			SessionRegistry.getCurrentInstance().removeSession(message.getSessionId());
			ExceptionPoint.getInstance().fireException(this, e);
			HasFaultResponseException communicationException = new SCMPCommunicationException(SCMPError.SERVER_ERROR);
			communicationException.setMessageType(getKey());
			throw communicationException;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, ICommandCallback callback) throws Exception {
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		Session session = getSessionById(sessionId);

		Server server = session.getServer();
		try {
			// try sending to backend server
			server.sendData(message, callback);
			return;
		} catch (SCServiceException e) {
			// clnDatat failed, connection to backend server disturbed - clean up
			// TODO clean up??
			SessionRegistry.getCurrentInstance().removeSession(message.getSessionId());
			ExceptionPoint.getInstance().fireException(this, e);
			HasFaultResponseException communicationException = new SCMPCommunicationException(SCMPError.SERVER_ERROR);
			communicationException.setMessageType(getKey());
			throw communicationException;
		}
	}

	/**
	 * The Class ClnDataCommandValidator.
	 */
	public class ClnDataCommandValidator implements ICommandValidator {
		
		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();
			try {
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessionId must be set!");
				}
				// serviceName
				String serviceName = message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
				// bodyLength
				String bodyLength = message.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH);
				ValidatorUtility.validateInt(0, bodyLength);
				request.setAttribute(SCMPHeaderAttributeKey.BODY_LENGTH.getName(), bodyLength);

				// TODO messageId

				// compression default = true
				Boolean compression = message.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION);
				if (compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION.getName(), compression);
				// messageInfo
				String messageInfo = (String) message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
				ValidatorUtility.validateString(0, messageInfo, 256);
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