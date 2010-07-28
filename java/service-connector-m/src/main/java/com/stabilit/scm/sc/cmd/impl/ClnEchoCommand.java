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
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.net.req.netty.OperationTimeoutException;
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
		if (message.getBodyLength() > 0) {
			if (message.getBody().toString().length() > 100) {
				System.out.println("ClnEchoCommand body = " + message.getBody().toString().substring(0, 100));
			} else {
				System.out.println("ClnEchoCommand body = " + message.getBody().toString());
			}
		} else {
			System.out.println("ClnEchoCommand empty body");
		}

		Session session = getSessionById(message.getSessionId());
		Server server = session.getServer();

		SCMPMessage result = null;
		ClnEchoCommandCallback callback = new ClnEchoCommandCallback();
		server.serverEcho(message, callback);
		// TODO echo timeout in callback.getMessagSync()
		result = callback.getMessageSync(Constants.getServiceLevelOperationTimeoutMillis());

		if (result.isFault()) {
			// exception handling
			SCMPFault fault = (SCMPFault) result;
			Throwable th = fault.getCause();
			if (th instanceof OperationTimeoutException) {
				// operation timeout handling
				HasFaultResponseException scmpEx = new SCMPCommandException(SCMPError.OPERATION_TIMEOUT);
				scmpEx.setMessageType(getKey());
				throw scmpEx;
			}
			throw th;
		}
		// TODO echo failed
		// // srvEcho or clnEcho failed, connection disturbed - clean up
		// SessionRegistry.getCurrentInstance().removeSession(message.getSessionId());
		// ExceptionPoint.getInstance().fireException(this, e);
		// HasFaultResponseException communicationException = new SCMPCommunicationException(SCMPError.SERVER_ERROR);
		// communicationException.setMessageType(getKey());
		// throw communicationException;

		result.setMessageType(getKey().getValue());
		result.setHeader(SCMPHeaderAttributeKey.SC_REQ_ID, request.getRemoteSocketAddress().hashCode());
		response.setSCMP(result);
	}

	/**
	 * The Class ClnEchoCommandValidator.
	 */
	private class ClnEchoCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
		}
	}

	private class ClnEchoCommandCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
