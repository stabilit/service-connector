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
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class ClnEchoCommand. Responsible for validation and execution of echo command. Simply sends back incoming
 * content. Depending on header fields on which node echo executes or forwards to next server.
 * 
 * @author JTraber
 */
public class ClnEchoCommand extends CommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a new ClnEchoCommand.
	 */
	public ClnEchoCommand() {
		this.commandValidator = new ClnEchoCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_ECHO;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Throwable {
		SCMPMessage message = request.getMessage();
		Session session = this.getSessionById(message.getSessionId());
		Server server = session.getServer();

		message.removeHeader(SCMPHeaderAttributeKey.CLN_REQ_ID);
		ClnEchoCommandCallback callback = new ClnEchoCommandCallback();
		server.serverEcho(message, callback);
		// TODO careful timeout is in seconds
		SCMPMessage result = callback.getMessageSync(session.getEchoTimeout() * Constants.SEC_TO_MILISEC_FACTOR);

		if (result.isFault()) {
			/**
			 * error in echo process<br>
			 * 1. delete session on SC<br>
			 * 2. remove session on server instance<br>
			 * 3. EXC message to client<br>
			 **/
			this.sessionRegistry.removeSession(message.getSessionId());
			server.removeSession(session);
			ExceptionPoint.getInstance().fireException(this, new Exception("genau1"));
		}
		result.removeHeader(SCMPHeaderAttributeKey.SRV_RES_ID);
		result.setMessageType(getKey());
		// result.setHeader(SCMPHeaderAttributeKey.SC_RES_ID, request.getRemoteSocketAddress().hashCode());
		response.setSCMP(result);
	}

	/**
	 * The Class ClnEchoCommandValidator.
	 */
	private class ClnEchoCommandValidator implements ICommandValidator {

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
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException("sessionId must be set!");
				}
			} catch (HasFaultResponseException ex) {
				ExceptionPoint.getInstance().fireException(this, new Exception("genau2"));
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

	private class ClnEchoCommandCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
