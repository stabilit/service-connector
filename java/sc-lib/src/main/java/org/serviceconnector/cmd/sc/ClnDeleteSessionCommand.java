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
import org.serviceconnector.cmd.proxy.ClnDeleteSessionCommandProxyCallback;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.server.FileServer;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.CascadedSessionService;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnDeleteSessionCommand. Responsible for validation and execution of delete session command. Deleting a session means:
 * Free up backend server from session and delete session entry in SC session registry.
 * 
 * @author JTraber
 */
public class ClnDeleteSessionCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnDeleteSessionCommand.class);

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
		Service abstractService = this.validateService(serviceName);

		switch (abstractService.getType()) {
		case CASCADED_SESSION_SERVICE:
			CascadedSC cascadedSC = ((CascadedSessionService) abstractService).getCascadedSC();
			ClnDeleteSessionCommandProxyCallback callback = new ClnDeleteSessionCommandProxyCallback(request, response,
					responderCallback);
			cascadedSC.deleteSession(reqMessage, callback, oti);
			return;
		}

		String sessionId = reqMessage.getSessionId();
		// lookup session and checks properness
		Session session = this.getSessionById(sessionId);
		// delete entry from session registry
		this.sessionRegistry.removeSession(session);

		Server abstractServer = session.getServer();

		switch (abstractServer.getType()) {
		case STATEFUL_SERVER:
			// code for type session service is below switch statement
			break;
		case FILE_SERVER:
			this.sessionRegistry.removeSession(session);
			((FileServer) abstractServer).removeSession(session);
			// reply to client
			SCMPMessage reply = new SCMPMessage();
			reply.setIsReply(true);
			reply.setMessageType(getKey());
			response.setSCMP(reply);
			responderCallback.responseCallback(request, response);
			return;
		case CASCADED_SC:
		case UNDEFINED:
		default:
			throw new SCMPCommandException(SCMPError.SC_ERROR, "delete session not allowed for service "
					+ abstractServer.getServiceName());
		}

		StatefulServer statefulServer = (StatefulServer) abstractServer;
		ClnDeleteSessionCommandCallback callback;
		int tries = (int) ((oti * basicConf.getOperationTimeoutMultiplier()) / Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
		// Following loop implements the wait mechanism in case of a busy connection pool
		int i = 0;
		int otiOnServerMillis = 0;
		do {
			callback = new ClnDeleteSessionCommandCallback(request, response, responderCallback, session, statefulServer);
			try {
				otiOnServerMillis = oti - (i * Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
				statefulServer.deleteSession(reqMessage, callback, otiOnServerMillis);
				// no exception has been thrown - get out of wait loop
				break;
			} catch (ConnectionPoolBusyException ex) {
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					statefulServer.abortSessionsAndDestroy("deleting session failed, connection pool to server busy");
					logger.warn(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + reqMessage.getServiceName()));
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION, "service="
							+ reqMessage.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
			}
			// sleep for a while and then try again
			Thread.sleep(Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
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
			String serviceName = (String) message.getServiceName();
			ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(1000, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLength(1, sessionId, 256, SCMPError.HV_WRONG_SESSION_ID);
			// sessionInfo optional
			String sessionInfo = (String) message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
			ValidatorUtility.validateStringLengthIgnoreNull(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
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
