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
import java.util.Map;

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
 * The Class ClnSystemCommand. Responsible for validation and execution of system command. System command is used
 * for testing/maintaining reasons. Depending on header fields on which node system call executes or forwards to
 * next server.
 * 
 * @author JTraber
 */
public class ClnSystemCommand extends CommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a new ClnSystemCommand.
	 */
	public ClnSystemCommand() {
		this.commandValidator = new ClnSystemCommandValidator();
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_SYSTEM;
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
		Map<String, String> header = message.getHeader();

		SCMPMessage result = null;
		int maxNodes = message.getHeaderInt(SCMPHeaderAttributeKey.MAX_NODES);

		// adding ip of current node to header field ip address list
		String ipList = header.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getName());
		SocketAddress socketAddress = request.getLocalSocketAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			message.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		}

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
		header.remove(SCMPHeaderAttributeKey.MAX_NODES.getName());
		try {
			if (maxNodes == 2) {
				// forward to next node
				result = serviceRegistryItem.srvSystem(message);
			} else {
				// forward to next node where system call will be executed
				--maxNodes;
				header.put(SCMPHeaderAttributeKey.MAX_NODES.getName(), String.valueOf(maxNodes));
				result = serviceRegistryItem.clnSystem(message);
			}
		} catch (CommunicationException e) {
			// srvSystem or clnSystem failed, connection disturbed - clean up
			SessionRegistry.getCurrentInstance().removeSession(message.getSessionId());
			serviceRegistryItem.markObsolete();
			ExceptionPoint.getInstance().fireException(this, e);
			SCMPCommunicationException communicationException = new SCMPCommunicationException(
					SCMPError.SERVER_ERROR);
			communicationException.setMessageType(getResponseKeyName());
			throw communicationException;
		}
		result.setMessageType(getKey().getResponseName());
		result.removeHeader("kill");
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
	 * The Class ClnSystemCommandValidator.
	 */
	public class ClnSystemCommandValidator implements ICommandValidator {

		/**
		 * Validate request, nothing to validate in case of system.
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
