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
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class ClnDeleteSessionCommand. Responsible for validation and execution of delete session command. Deleting a
 * session means: Free up backend server from session and delete session entry in SC session registry.
 * 
 * @author JTraber
 */
public class ClnDeleteSessionCommand extends CommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a new ClnDeleteSessionCommand.
	 */
	public ClnDeleteSessionCommand() {
		this.commandValidator = new ClnDeleteSessionCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_DELETE_SESSION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		// lookup session and checks properness
		Session session = this.getSessionById(sessionId);

		Server server = session.getServer();
		SCMPMessage reply = null;
		ClnDeleteSessionCommandCallback callback = new ClnDeleteSessionCommandCallback();
		try {
			server.deleteSession(message, callback);
			reply = callback.getMessageSync();
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
			/**
			 * error in deleting session process<br>
			 * 1. delete session on SC<br>
			 * 2. deregister server from service<br>
			 * 3. SRV_ABORT_SESSION (SAS) to server<br>
			 * 4. destroy server<br>
			 * 5. EXC message to client<br>
			 **/
			this.sessionRegistry.removeSession(sessionId);
			server.getService().removeServer(server);
			server.removeSession(session);
			// set up server abort session message - don't forward messageId & include error stuff
			message.removeHeader(SCMPHeaderAttributeKey.MESSAGE_ID);
			message.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SC_ERROR.getErrorCode());
			message.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, SCMPError.SC_ERROR.getErrorText());
			message.setBody("ServerDeleteSession failed for sessionId " + sessionId);
			server.serverAbortSession(message, callback);
			callback.getMessageSync(Constants.SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS_SHORT);
			server.destroy();
			// set up client EXC message
			SCMPFault fault = new SCMPFault(SCMPError.SERVER_ERROR);
			fault.setMessageType(getKey());
			response.setSCMP(fault);
			return;
		}
		// delete session on server successful - delete entry from session registry
		this.sessionRegistry.removeSession(session);
		// free server from session
		server.removeSession(session);
		// forward server reply to client
		reply.setIsReply(true);
		reply.setMessageType(getKey());
		response.setSCMP(reply);
	}

	/**
	 * The Class ClnDeleteSessionCommandValidator.
	 */
	private class ClnDeleteSessionCommandValidator implements ICommandValidator {

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
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException("sessonId must be set!");
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

	/**
	 * The Class ClnDeleteSessionCommandCallback.
	 */
	private class ClnDeleteSessionCommandCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
