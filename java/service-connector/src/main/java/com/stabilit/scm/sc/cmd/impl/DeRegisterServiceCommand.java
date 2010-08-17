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
import java.util.List;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class DeRegisterServiceCommand. Responsible for validation and execution of deregister command. Used to
 * deregister backend server from SC service. Backend server will be removed from server registry of SC.
 * 
 * @author JTraber
 */
public class DeRegisterServiceCommand extends CommandAdapter implements IPassThroughPartMsg {

	private static final String ABORT_SESSION_ERROR_STRING = SCMPError.SESSION_ABORT.getErrorText()
			+ "[deregister service]";

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
		InetSocketAddress socketAddress = request.getRemoteSocketAddress();

		String serverKey = serviceName + "_" + socketAddress.getHostName() + "/" + socketAddress.getPort();
		// looks up server & validate server is registered
		Server server = this.getServerByName(serverKey);
		// deregister server from service
		server.getService().removeServer(server);

		List<Session> serverSessions = server.getSessions();
		ISCMPCallback callback = new DeRegisterServiceCommmandCallback();
		// set up abort message
		SCMPMessage abortMsg = new SCMPMessage();
		abortMsg.setServiceName(serviceName);
		abortMsg.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
		abortMsg.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, DeRegisterServiceCommand.ABORT_SESSION_ERROR_STRING);

		// aborts session on server
		for (Session session : serverSessions) {
			this.sessionRegistry.removeSession(session);
			server.removeSession(session);
			abortMsg.setSessionId(session.getId());
			server.serverAbortSession(abortMsg, callback);
		}
		// release all resources used by server, disconnects requester
		server.destroy();
		this.serverRegistry.removeServer(serverKey);

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
	}

	/**
	 * Validate server. Checks properness of allocated server. If server null no free server available.
	 * 
	 * @param key
	 *            the key
	 * @return the server by name
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	public Server getServerByName(String key) throws SCMPCommandException {
		Server server = this.serverRegistry.getServer(key);

		if (server == null) {
			// no available server for this service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND,
					"server not registered, key " + key);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return server;
	}

	/**
	 * The Class DeRegisterServiceCommandValidator.
	 */
	private class DeRegisterServiceCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();

			try {
				// serviceName
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
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

	/**
	 * The Class DeRegisterServiceCommmandCallback. It's used as callback for abort sessions. Callback can be ignored.
	 */
	private class DeRegisterServiceCommmandCallback implements ISCMPCallback {

		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// nothing to do
		}

		@Override
		public void callback(Throwable th) {
			// nothing to do
		}
	}
}