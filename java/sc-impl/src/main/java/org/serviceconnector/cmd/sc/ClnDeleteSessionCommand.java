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

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.Session;
import org.serviceconnector.service.SessionServer;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnDeleteSessionCommand. Responsible for validation and execution of delete session command. Deleting a
 * session means: Free up backend server from session and delete session entry in SC session registry.
 * 
 * @author JTraber
 */
public class ClnDeleteSessionCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnDeleteSessionCommand.class);

	/**
	 * Instantiates a new ClnDeleteSessionCommand.
	 */
	public ClnDeleteSessionCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_DELETE_SESSION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		// lookup session and checks properness
		Session session = this.getSessionById(sessionId);
		// delete entry from session registry
		this.sessionRegistry.removeSession(session);

		SessionServer server = session.getServer();
		CommandCallback callback;
		int oti = message.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
		int tries = (int) ((oti * Constants.OPERATION_TIMEOUT_MULTIPLIER) / Constants.WAIT_FOR_CONNECTION_INTERVAL_MILLIS);
		// Following loop implements the wait mechanism in case of a busy connection pool
		int i = 0;
		do {
			callback = new CommandCallback(true);
			try {
				server.deleteSession(message, callback, oti - (i * Constants.WAIT_FOR_CONNECTION_INTERVAL_MILLIS));
				// no exception has been thrown - get out of wait loop
				break;
			} catch (ConnectionPoolBusyException ex) {
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					this.cleanUpServer(server, message);
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.SC_ERROR,
							"no free connection on server for service " + message.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
			} catch (Exception ex) {
				this.cleanUpServer(server, message);
				throw ex;
			}
			// sleep for a while and then try again
			Thread.sleep(Constants.WAIT_FOR_CONNECTION_INTERVAL_MILLIS);
		} while (++i < tries);

		SCMPMessage reply = callback.getMessageSync();

		if (reply.isFault()) {
			this.cleanUpServer(server, message);
		}
		// free server from session
		server.removeSession(session);
		// forward server reply to client
		reply.setIsReply(true);
		reply.setMessageType(getKey());
		response.setSCMP(reply);
	}

	private void cleanUpServer(SessionServer server, SCMPMessage message) {
		/**
		 * error in deleting session process<br>
		 * 1. deregister server from service<br>
		 * 3. SRV_ABORT_SESSION (SAS) to server<br>
		 * 4. destroy server<br>
		 **/
		server.getService().removeServer(server);
		// set up server abort session message - don't forward messageId & include error stuff
		message.removeHeader(SCMPHeaderAttributeKey.MESSAGE_ID);
		message.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
		message.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, SCMPError.SESSION_ABORT.getErrorText()
				+ " [delete session failed]");
		// no need to get the reply so just put in a random command callback
		server.serverAbortSession(message, new CommandCallback(false), Constants.OPERATION_TIMEOUT_MILLIS_SHORT);
		server.destroy();
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();
		try {
			// messageId
			String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID);
			if (messageId == null || messageId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_ID, "messageId must be set");
			}
			// serviceName
			String serviceName = (String) message.getServiceName();
			if (serviceName == null || serviceName.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
			}
			// operation timeout
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT.getValue());
			ValidatorUtility.validateInt(10, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId
			String sessionId = message.getSessionId();
			if (sessionId == null || sessionId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
			}
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			logger.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}
}
