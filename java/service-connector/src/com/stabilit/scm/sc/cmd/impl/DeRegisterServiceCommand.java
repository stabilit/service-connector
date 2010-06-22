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

import java.net.SocketAddress;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.registry.ServerRegistry;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Service;

/**
 * The Class DeRegisterServiceCommand. Responsible for validation and execution of deregister command. Used to
 * deregister backend server from SC. Backend server will be removed from server registry of SC.
 * 
 * @author JTraber
 */
public class DeRegisterServiceCommand extends CommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a new DeRegisterServiceCommand.
	 */
	public DeRegisterServiceCommand() {
		this.commandValidator = new DeRegisterServiceCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.DEREGISTER_SERVICE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		SocketAddress socketAddress = request.getRemoteSocketAddress();
		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
		Server server = serverRegistry.getServer(serviceName + "_" + socketAddress);

		// validate server is registered - otherwise deregister not possible
		this.validateServer(server);

		// release all resources used by server, disconnects requesters
		server.destroy();
		serverRegistry.removeServer(server);

		// validate service not null - otherwise deregister not possible
		Service service = this.validateService(serviceName);
		// remove server in service
		service.removeServer(server);

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
	}

	/**
	 * Validate server. Checks if server is registered. If not an exception will be thrown.
	 * 
	 * @param server
	 *            the server
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	private void validateServer(Server server) throws SCMPCommandException {
		if (server == null) {
			// server not registered - deregister not possible
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: server not registered");
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_REGISTERED);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
	}

	/**
	 * The Class DeRegisterServiceCommandValidator.
	 */
	public class DeRegisterServiceCommandValidator implements ICommandValidator {

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
