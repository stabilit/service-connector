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

import javax.xml.bind.ValidationException;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.ctx.IRequestContext;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.ServerRegistry;
import com.stabilit.scm.sc.registry.ServiceRegistry;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Service;

/**
 * The Class RegisterServiceCommand. Responsible for validation and execution of register command. Used to register
 * backend server in SC. Backend server will be registered in server registry of SC.
 * 
 * @author JTraber
 */
public class RegisterServiceCommand extends CommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a new RegisterServiceCommand.
	 */
	public RegisterServiceCommand() {
		this.commandValidator = new RegisterServiceCommandValidator();
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REGISTER_SERVICE;
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
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		request.setAttribute(SocketAddress.class.getName(), socketAddress);

		// if service is not here - a new service gets stored
		// server will be added to service
		SCMPMessage message = request.getSCMP();
		String serviceName = message.getServiceName();

		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		Service service = serviceRegistry.getService(serviceName);

		if (service == null) {
			SCMPCommunicationException communicationException = new SCMPCommunicationException(
					SCMPError.UNKNOWN_SERVICE);
			communicationException.setMessageType(getResponseKeyName());
			throw communicationException;
		}

		Server server = serverRegistry.getServer(socketAddress + serviceName);

		if (server != null) {
			SCMPCommunicationException communicationException = new SCMPCommunicationException(
					SCMPError.SERVER_ALREADY_REGISTERED);
			communicationException.setMessageType(getResponseKeyName());
			throw communicationException;
		}

		int maxSessions = (Integer) request.getAttribute(SCMPHeaderAttributeKey.MAX_SESSIONS);
		int portNr = (Integer) request.getAttribute(SCMPHeaderAttributeKey.PORT_NR);
		boolean immediateConnect = (Boolean) request.getAttribute(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT);

		server = new Server((InetSocketAddress) socketAddress, portNr, maxSessions);
		try {
			if (immediateConnect) {
				// server connections gets connected immediately
				server.immediateConnect();
			}
		} catch (Exception ex) {
			ExceptionPoint.getInstance().fireException(this, ex);
			SCMPCommunicationException communicationException = new SCMPCommunicationException(
					SCMPError.IMMEDIATE_CONNECT_FAILED);
			communicationException.setMessageType(getResponseKeyName());
			throw communicationException;
		}
		// add server to service
		service.addServer(server);

		// TODO ... key
		// add server to server registry
		serverRegistry.addServer(server.getSocketAddress() + serviceName, server);

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
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
	 * The Class RegisterServiceCommandValidator.
	 */
	public class RegisterServiceCommandValidator implements ICommandValidator {

		/**
		 * Validate request.
		 * 
		 * @param request
		 *            the request
		 * @throws Exception
		 *             the exception
		 */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getSCMP();
			try {
				// TODO fields changed

				// serviceName
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("ServiceName must be set!");
				}
				
				// maxSessions
				String maxSessions = (String) message.getHeader(SCMPHeaderAttributeKey.MAX_SESSIONS);
				// validate with lowest limit 1
				int maxSessionsInt = ValidatorUtility.validateInt(1, maxSessions);
				request.setAttribute(SCMPHeaderAttributeKey.MAX_SESSIONS, maxSessionsInt);
				
				// immmediateConnect
				String immediateConnect = (String) message.getHeader(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT);
				boolean immediateConnectBool = ValidatorUtility.validateBoolean(immediateConnect);
				request.setAttribute(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT, immediateConnectBool);
				
				// portNr
				String portNr = (String) message.getHeader(SCMPHeaderAttributeKey.PORT_NR);
				int portNrInt = ValidatorUtility.validateInt(1, portNr, 99999);
				request.setAttribute(SCMPHeaderAttributeKey.PORT_NR, portNrInt);
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}
