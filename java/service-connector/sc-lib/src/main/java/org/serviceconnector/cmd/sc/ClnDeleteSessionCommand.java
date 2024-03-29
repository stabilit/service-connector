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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.cmd.casc.CommandCascCallback;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.server.FileServer;
import org.serviceconnector.server.IServer;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.CascadedFileService;
import org.serviceconnector.service.CascadedSessionService;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnDeleteSessionCommand. Responsible for validation and execution of delete session command. Deleting a session means: Free up backend server from session and delete
 * session entry in SC session registry.
 *
 * @author JTraber
 */
public class ClnDeleteSessionCommand extends CommandAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ClnDeleteSessionCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_DELETE_SESSION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
		String serviceName = reqMessage.getServiceName();
		// check service is present
		Service abstractService = this.getService(serviceName);
		String sessionId = reqMessage.getSessionId();

		switch (abstractService.getType()) {
			case CASCADED_SESSION_SERVICE:
				// clears message in cache if in loading state
				AppContext.getSCCache().clearLoading(sessionId);
				CascadedSC cascadedSC = ((CascadedSessionService) abstractService).getCascadedSC();
				CommandCascCallback callback = new CommandCascCallback(request, response, responderCallback);
				cascadedSC.deleteSession(reqMessage, callback, oti);
				return;
			case CASCADED_FILE_SERVICE:
				cascadedSC = ((CascadedFileService) abstractService).getCascadedSC();
				callback = new CommandCascCallback(request, response, responderCallback);
				cascadedSC.deleteSession(reqMessage, callback, oti);
				return;
			default:
				// code for other types of services is below
				break;
		}

		// lookup session and checks properness
		Session session = this.getSessionById(sessionId);
		synchronized (session) {
			session.setPendingRequest(true); // IMPORTANT - set true because of parallel echo call
			// delete entry from session registry
			this.sessionRegistry.removeSession(session);
		}

		IServer abstractServer = session.getServer();

		switch (abstractServer.getType()) {
			case STATEFUL_SERVER:
				// code for type session service is below switch statement
				break;
			case FILE_SERVER:
				((FileServer) abstractServer).removeSession(session);
				// reply to client - SCMP Version request
				SCMPMessage reply = new SCMPMessage(reqMessage.getSCMPVersion());
				reply.setIsReply(true);
				reply.setMessageType(getKey());
				response.setSCMP(reply);
				responderCallback.responseCallback(request, response);
				return;
			case CASCADED_SC:
			case UNDEFINED:
			default:
				throw new SCMPCommandException(SCMPError.SC_ERROR, "delete session not allowed for service " + abstractService.getName());
		}
		StatefulServer statefulServer = (StatefulServer) abstractServer;
		DeleteSessionCommandCallback callback;
		// free server from session
		statefulServer.removeSession(session);

		int otiOnSCMillis = (int) (oti * basicConf.getOperationTimeoutMultiplier());
		int tries = (otiOnSCMillis / Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		// Following loop implements the wait mechanism in case of a busy connection pool
		int i = 0;
		do {
			// reset msgType, might have been modified in below delete session try
			reqMessage.setMessageType(this.getKey());
			callback = new DeleteSessionCommandCallback(request, response, responderCallback, session, statefulServer);
			try {
				statefulServer.deleteSession(reqMessage, callback, otiOnSCMillis - (i * Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS));
				// no exception has been thrown - get out of wait loop
				break;
			} catch (ConnectionPoolBusyException ex) {
				LOGGER.debug("ConnectionPoolBusyException caught in wait mec of delete session, tries left=" + tries);
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					statefulServer.abortSession(session, "deleting session failed, connection pool to server busy");
					LOGGER.debug(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + reqMessage.getServiceName()));
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION, "service=" + reqMessage.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
			}
			// sleep for a while and then try again
			Thread.sleep(Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		} while (++i < tries);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();
		try {
			// msgSequenceNr mandatory
			String msgSequenceNr = message.getMessageSequenceNr();
			ValidatorUtility.validateLong(1, msgSequenceNr, SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR);
			// serviceName mandatory
			String serviceName = message.getServiceName();
			ValidatorUtility.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME, SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(Constants.MIN_OTI_VALUE_CLN, otiValue, Constants.MAX_OTI_VALUE, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, sessionId, Constants.MAX_STRING_LENGTH_256, SCMPError.HV_WRONG_SESSION_ID);
			// sessionInfo optional
			String sessionInfo = message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
			ValidatorUtility.validateStringLengthIgnoreNull(1, sessionInfo, Constants.MAX_STRING_LENGTH_256, SCMPError.HV_WRONG_SESSION_INFO);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			LOGGER.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}
}
