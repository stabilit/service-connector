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
package org.serviceconnector.cmd.sc;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.ICommandValidator;
import org.serviceconnector.cmd.IPassThroughPartMsg;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.Server;
import org.serviceconnector.service.Session;

/**
 * The Class DeRegisterServerCommand. Responsible for validation and execution of deregister command. Used to
 * deregisters server from SC service. Server will be removed from server registry.
 * 
 * @author JTraber
 */
public class DeRegisterServerCommand extends CommandAdapter implements IPassThroughPartMsg {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DeRegisterServerCommand.class);

	private static final String ABORT_SESSION_ERROR_STRING = SCMPError.SESSION_ABORT.getErrorText()
			+ "[deregister server]";

	/**
	 * Instantiates a new DeRegisterServerCommand.
	 */
	public DeRegisterServerCommand() {
		this.commandValidator = new DeRegisterServerCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.DEREGISTER_SERVER;
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
		ISCMPCallback callback = new DeRegisterServerCommmandCallback();
		// set up abort message
		SCMPMessage abortMsg = new SCMPMessage();
		abortMsg.setServiceName(serviceName);
		abortMsg.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
		abortMsg.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, DeRegisterServerCommand.ABORT_SESSION_ERROR_STRING);

		// aborts session on server - carefully don't modify list in loop ConcurrentModificationException
		for (Session session : serverSessions) {
			this.sessionRegistry.removeSession(session);
			abortMsg.setSessionId(session.getId());
			server.serverAbortSession(abortMsg, callback, Constants.OPERATION_TIMEOUT_MILLIS_SHORT);
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
	 * The Class DeRegisterServerCommandValidator.
	 */
	private class DeRegisterServerCommandValidator implements ICommandValidator {

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
			} catch (Throwable ex) {
				logger.error("validate", ex);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}

	/**
	 * The Class DeRegisterServerCommmandCallback. It's used as callback for abort sessions. Callback can be ignored.
	 */
	private class DeRegisterServerCommmandCallback implements ISCMPCallback {

		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// nothing to do
		}

		@Override
		public void callback(Exception th) {
			// nothing to do
		}
	}
}