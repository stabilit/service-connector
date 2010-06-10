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

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.net.CommunicationException;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.registry.ServiceRegistryItem;
import com.stabilit.scm.sc.registry.SessionRegistry;
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
		SCMPMessage message = request.getSCMP();
		SCMPMessage result = null;
		int maxNodes = message.getHeaderInt(SCMPHeaderAttributeKey.MAX_NODES);
		if (LoggerPoint.getInstance().isDebug()) {
			LoggerPoint.getInstance().fireDebug(this,
					"Run command " + this.getKey() + " on Node: " + maxNodes);
		}
		// adding ip of current unit to ip address list
		String ipList = message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		SocketAddress socketAddress = request.getLocalSocketAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			message.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		}

//		if (message.getBodyLength() > 0) {
//			if (message.getBody().toString().length() > 100) {
//				System.out.println("ClnEchoCommand body = " + message.getBody().toString().substring(0, 100));
//			} else {
//				System.out.println("ClnEchoCommand body = " + message.getBody().toString());
//			}
//		} else {
//			System.out.println("ClnEchoCommand empty body");
//		}

		Session session = getSessionById(message.getSessionId());
		ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) session
				.getAttribute(ServiceRegistryItem.class.getName());

		if (serviceRegistryItem == null) {
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: serviceRegistryItem not found");
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.SERVER_ERROR);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		message.removeHeader(SCMPHeaderAttributeKey.MAX_NODES);

		try {
			if (maxNodes == 2) {
				// forward to next node
				result = serviceRegistryItem.srvEcho(message);
			} else {
				// forward to next node where echo will be executed
				--maxNodes;
				message.setHeader(SCMPHeaderAttributeKey.MAX_NODES.getName(), String.valueOf(maxNodes));
				result = serviceRegistryItem.clnEcho(message);
			}
		} catch (CommunicationException e) {
			// srvEcho or clnEcho failed, connection disturbed - clean up
			SessionRegistry.getCurrentInstance().removeSession(message.getSessionId());
			serviceRegistryItem.markObsolete();
			ExceptionPoint.getInstance().fireException(this, e);
			SCMPCommunicationException communicationException = new SCMPCommunicationException(
					SCMPError.SERVER_ERROR);
			communicationException.setMessageType(getResponseKeyName());
			throw communicationException;
		}
		result.setMessageType(getKey().getResponseName());
		result.setHeader(SCMPHeaderAttributeKey.CLN_REQ_ID, request.getContext().getSocketAddress().hashCode());
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
