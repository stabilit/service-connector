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
import java.util.Date;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.scmp.internal.KeepAlive;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.ServerRegistry;
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

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REGISTER_SERVICE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SocketAddress socketAddress = request.getRemoteSocketAddress();
		request.setAttribute(SocketAddress.class.getName(), socketAddress);

		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		// lookup service and checks properness
		Service service = this.validateService(serviceName);

		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
		Server server = serverRegistry.getServer(serviceName + "_" + socketAddress);
		// controls that server not has been registered before for specific service
		this.validateServerNotRegistered(server);

		int maxSessions = (Integer) request.getAttribute(SCMPHeaderAttributeKey.MAX_SESSIONS);
		int portNr = (Integer) request.getAttribute(SCMPHeaderAttributeKey.PORT_NR);
		boolean immediateConnect = (Boolean) request.getAttribute(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT);
		// create new server
		server = new Server((InetSocketAddress) socketAddress, serviceName, portNr, maxSessions);
		try {
			if (immediateConnect) {
				// server connections get connected immediately
				server.immediateConnect();
			}
		} catch (Exception ex) {
			ExceptionPoint.getInstance().fireException(this, ex);
			HasFaultResponseException communicationException = new SCMPCommunicationException(
					SCMPError.IMMEDIATE_CONNECT_FAILED);
			communicationException.setMessageType(getKey());
			throw communicationException;
		}
		// add server to service
		service.addServer(server);
		// add service to server
		server.setService(service);

		// add server to server registry TODO ... key
		serverRegistry.addServer(serviceName + "_" + server.getSocketAddress(), server);

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
	}

	private void validateServerNotRegistered(Server server) throws SCMPCommunicationException {
		if (server != null) {
			// server registered two times for this service
			SCMPCommunicationException communicationException = new SCMPCommunicationException(
					SCMPError.SERVER_ALREADY_REGISTERED);
			communicationException.setMessageType(getKey());
			throw communicationException;
		}
	}

	/**
	 * The Class RegisterServiceCommandValidator.
	 */
	public class RegisterServiceCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();
			try {
				// serviceName
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("ServiceName must be set!");
				}

				// maxSessions
				String maxSessions = (String) message.getHeader(SCMPHeaderAttributeKey.MAX_SESSIONS);
				// validate with lowest limit 1
				int maxSessionsInt = ValidatorUtility.validateInt(1, maxSessions);
				request.setAttribute(SCMPHeaderAttributeKey.MAX_SESSIONS, maxSessionsInt);

				// immmediateConnect - default = true
				String immediateConnect = (String) message.getHeader(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT);
				Boolean immediateConnectBool = ValidatorUtility.validateBoolean(immediateConnect, true);
				request.setAttribute(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT, immediateConnectBool);

				// portNr
				String portNr = (String) message.getHeader(SCMPHeaderAttributeKey.PORT_NR);
				int portNrInt = ValidatorUtility.validateInt(1, portNr, 99999);
				request.setAttribute(SCMPHeaderAttributeKey.PORT_NR, portNrInt);

				// scVersion
				String scVersion = message.getHeader(SCMPHeaderAttributeKey.SC_VERSION);
				SCMPMessage.SC_VERSION.isSupported(scVersion);

				// localDateTime
				Date localDateTime = ValidatorUtility.validateLocalDateTime(message
						.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME));
				request.setAttribute(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, localDateTime);

				// KeepAliveTimeout && KeepAliveInterval
				KeepAlive keepAlive = ValidatorUtility.validateKeepAlive(message
						.getHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT), message
						.getHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL));
				request.setAttribute(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT, keepAlive);
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
}
