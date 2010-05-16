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
package com.stabilit.sc.cmd.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

import com.stabilit.sc.cln.net.CommunicationException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.Session;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;
import com.stabilit.sc.srv.net.SCMPCommunicationException;

/**
 * The Class ClnSystemCommand. Responsible for validation and execution of system command. System command is used
 * for testing/maintaining reasons. Depending on header fields on which node system call executes or forwards to
 * next server.
 * 
 * @author JTraber
 */
public class ClnSystemCommand extends CommandAdapter implements IPassThrough {

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
		SCMPMessage message = request.getMessage();
		Map<String, String> header = message.getHeader();

		SCMPMessage result = null;
		int maxNodes = message.getHeaderInt(SCMPHeaderAttributeKey.MAX_NODES);

		// adding ip of current node to header field ip address list
		String ipList = header.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getName());
		SocketAddress socketAddress = request.getSocketAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			message.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		}

		Session session = getSessionById(message.getSessionId());
		ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) session
				.getAttribute(ServiceRegistryItem.class.getName());

		if (serviceRegistryItem == null) {
			if (LoggerListenerSupport.getInstance().isWarn()) {
				LoggerListenerSupport.getInstance().fireWarn(this, "command error: serviceRegistryItem not found");
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPErrorCode.SERVER_ERROR);
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
			SessionRegistry.getCurrentInstance().remove(message.getSessionId());
			serviceRegistryItem.markObsolete();
			ExceptionListenerSupport.getInstance().fireException(this, e);
			SCMPCommunicationException communicationException = new SCMPCommunicationException(
					SCMPErrorCode.SERVER_ERROR);
			communicationException.setMessageType(getResponseKeyName());
			throw communicationException;
		}
		result.setMessageType(getKey().getResponseName());
		result.removeHeader("kill");
		result.setHeader(SCMPHeaderAttributeKey.SCSERVER_ID, request.getContext().getSocketAddress().hashCode());
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
