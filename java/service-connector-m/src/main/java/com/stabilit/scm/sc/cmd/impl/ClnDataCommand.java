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

import com.stabilit.scm.common.cmd.IAsyncCommand;
import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.IResponderCallback;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class ClnDataCommand. Responsible for validation and execution of data command. Data command sends any data to a
 * server.
 * 
 * @author JTraber
 */
public class ClnDataCommand extends CommandAdapter implements IPassThroughPartMsg, IAsyncCommand {

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
	public void run(IRequest request, IResponse response, IResponderCallback communicatorCallback) throws Exception {
		ClnDataCommandCallback callback = new ClnDataCommandCallback(request, response, communicatorCallback);
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		Session session = getSessionById(sessionId);

		Server server = session.getServer();
		// try sending to backend server
		server.sendData(message, callback);
		return;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAsynchronous() {
		return true;
	}

	/**
	 * The Class ClnDataCommandValidator.
	 */
	private class ClnDataCommandValidator implements ICommandValidator {

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

				// TODO messageId

				// compression default = true
				Boolean compression = message.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION);
				if (compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION.getValue(), compression);
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

	private class ClnDataCommandCallback implements ISCMPCallback {
		private IResponderCallback callback;
		private IRequest request;
		private IResponse response;

		public ClnDataCommandCallback(IRequest request, IResponse response, IResponderCallback callback) {
			this.callback = callback;
			this.request = request;
			this.response = response;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) {
			scmpReply.setMessageType(getKey().getValue());
			this.response.setSCMP(scmpReply);
			this.callback.callback(request, response);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(Throwable th) {
			// TODO clean up!!! if th is SCServiceException
			this.callback.callback(response, th);
		}
	}
}