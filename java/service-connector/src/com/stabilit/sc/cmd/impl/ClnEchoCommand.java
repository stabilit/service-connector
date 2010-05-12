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

import com.stabilit.sc.cln.net.CommunicationException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMP;
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
 * The Class ClnEchoCommand. Responsible for validation and execution of echo command. Simply sends back incoming
 * content. Depending on header fields on which node echo executes or forwards to next server.
 * 
 * @author JTraber
 */
public class ClnEchoCommand extends CommandAdapter implements IPassThrough {

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
		SCMP scmp = request.getSCMP();
		SCMP result = null;
		int maxNodes = scmp.getHeaderInt(SCMPHeaderAttributeKey.MAX_NODES);
		if (LoggerListenerSupport.getInstance().isDebug()) {
			LoggerListenerSupport.getInstance().fireDebug(this,
					"Run command " + this.getKey() + " on Node: " + maxNodes);
		}
		// adding ip of current unit to ip address list
		String ipList = scmp.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		SocketAddress socketAddress = request.getSocketAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			scmp.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		}

		if (scmp.getBodyLength() > 0) {
			if (scmp.getBody().toString().length() > 100) {
				System.out.println("ClnEchoCommand body = " + scmp.getBody().toString().substring(0, 100));
			} else {
				System.out.println("ClnEchoCommand body = " + scmp.getBody().toString());
			}
		} else {
			System.out.println("ClnEchoCommand empty body");
		}

		Session session = getSessionById(scmp.getSessionId());
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
		scmp.removeHeader(SCMPHeaderAttributeKey.MAX_NODES);

		try {
			if (maxNodes == 2) {
				// forward to next node
				result = serviceRegistryItem.srvEcho(scmp);
			} else {
				// forward to next node where echo will be executed
				--maxNodes;
				scmp.setHeader(SCMPHeaderAttributeKey.MAX_NODES.getName(), String.valueOf(maxNodes));
				result = serviceRegistryItem.clnEcho(scmp);
			}
		} catch (CommunicationException e) {
			// srvEcho or clnEcho failed, connection disturbed - clean up
			SessionRegistry.getCurrentInstance().remove(scmp.getSessionId());
			serviceRegistryItem.markObsolete();
			ExceptionListenerSupport.getInstance().fireException(this, e);
			SCMPCommunicationException communicationException = new SCMPCommunicationException(
					SCMPErrorCode.SERVER_ERROR);
			communicationException.setMessageType(getResponseKeyName());
			throw communicationException;
		}
		result.setMessageType(getKey().getResponseName());
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
