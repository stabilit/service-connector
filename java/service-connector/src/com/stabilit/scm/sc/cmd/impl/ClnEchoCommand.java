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
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.log.listener.ExceptionPoint;
import com.stabilit.scm.common.log.listener.LoggerPoint;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.service.SCServiceException;
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

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_ECHO;
	}

	/**
	 * Gets the command validator.
	 * 
	 * @return the command validator
	 */
	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	/**
	 * Run command.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
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

		int maxNodes = message.getHeaderInt(SCMPHeaderAttributeKey.MAX_NODES);
		if (LoggerPoint.getInstance().isDebug()) {
			LoggerPoint.getInstance().fireDebug(this,
					"Run command " + this.getKey() + " on Node: " + maxNodes);
		}
		
		Session session = getSessionById(message.getSessionId());	
		Server server = session.getServer();
		
		SCMPMessage result = null;

		try {
			if (maxNodes == 2) {
				// forward to next node
				result = server.srvEcho(message);
			} else {
				// forward to next node where cln echo will be executed
				--maxNodes;
				message.setHeader(SCMPHeaderAttributeKey.MAX_NODES.getName(), String.valueOf(maxNodes));
				result = server.clnEcho(message);
			}
		} catch (SCServiceException e) {
			// srvEcho or clnEcho failed, connection disturbed - clean up
			SessionRegistry.getCurrentInstance().removeSession(message.getSessionId());
			ExceptionPoint.getInstance().fireException(this, e);
			HasFaultResponseException communicationException = new SCMPCommunicationException(
					SCMPError.SERVER_ERROR);
			communicationException.setMessageType(getKey());
			throw communicationException;
		}
		result.setMessageType(getKey().getName());
		result.setHeader(SCMPHeaderAttributeKey.CLN_REQ_ID, request.getRemoteSocketAddress().hashCode());
		response.setSCMP(result);
	}

	/**
	 * New instance.
	 * 
	 * @return the factoryable
	 */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/**
	 * The Class ClnEchoCommandValidator.
	 */
	public class ClnEchoCommandValidator implements ICommandValidator {

		/**
		 * Validate request, nothing to validate in case of echo.
		 * 
		 * @param request
		 *            the request
		 * @throws SCMPValidatorException
		 *             the SCMP validator exception
		 */
		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
		}
	}
}
