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
package org.serviceconnector.sc.cmd;

import org.apache.log4j.Logger;
import org.serviceconnector.common.cmd.ICommandValidator;
import org.serviceconnector.common.cmd.IPassThroughPartMsg;
import org.serviceconnector.common.cmd.SCMPValidatorException;
import org.serviceconnector.common.conf.Constants;
import org.serviceconnector.common.scmp.HasFaultResponseException;
import org.serviceconnector.common.scmp.IRequest;
import org.serviceconnector.common.scmp.IResponse;
import org.serviceconnector.common.scmp.ISCMPSynchronousCallback;
import org.serviceconnector.common.scmp.SCMPError;
import org.serviceconnector.common.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.scmp.SCMPMsgType;
import org.serviceconnector.sc.service.Server;
import org.serviceconnector.sc.service.Session;
import org.serviceconnector.util.ValidatorUtility;


/**
 * The Class ClnDeleteSessionCommand. Responsible for validation and execution of delete session command. Deleting a
 * session means: Free up backend server from session and delete session entry in SC session registry.
 * 
 * @author JTraber
 */
public class ClnDeleteSessionCommand extends CommandAdapter implements IPassThroughPartMsg {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnDeleteSessionCommand.class);

	/**
	 * Instantiates a new ClnDeleteSessionCommand.
	 */
	public ClnDeleteSessionCommand() {
		this.commandValidator = new ClnDeleteSessionCommandValidator();
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

		Server server = session.getServer();
		SCMPMessage reply = null;
		ISCMPSynchronousCallback callback = new CommandCallback(true);
		server.deleteSession(message, callback,
				((Integer) request.getAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT)));
		reply = callback.getMessageSync();

		if (reply.isFault()) {
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
			server.serverAbortSession(message, callback, Constants.OPERATION_TIMEOUT_MILLIS_SHORT);
			server.destroy();
		}
		// free server from session
		server.removeSession(session);
		// forward server reply to client
		reply.setIsReply(true);
		reply.setMessageType(getKey());
		response.setSCMP(reply);
	}

	/**
	 * The Class ClnDeleteSessionCommandValidator.
	 */
	private class ClnDeleteSessionCommandValidator implements ICommandValidator {

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
				int oti = ValidatorUtility.validateInt(1, otiValue, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
				request.setAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, oti);
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
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
}
