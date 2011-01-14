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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.FileServer;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;
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
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
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
			return;
		case CASCADED_SC:
			// TODO JOT cascaded service
		}

		StatefulServer statefulServer = (StatefulServer) abstractServer;
		ClnDeleteSessionCommandCallback callback;
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
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
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION,
							"service=" + reqMessage.getServiceName());
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

	/**
	 * The Class ClnDeleteSessionCommandCallback.
	 */
	public class ClnDeleteSessionCommandCallback implements ISCMPMessageCallback {

		/** The callback. */
		private IResponderCallback responderCallback;
		/** The request. */
		private IRequest request;
		/** The response. */
		private IResponse response;
		/** The session. */
		private Session session;
		/** The server. */
		private StatefulServer server;

		/**
		 * Instantiates a new cln delete session command callback.
		 * 
		 * @param request
		 *            the request
		 * @param response
		 *            the response
		 * @param callback
		 *            the callback
		 * @param session
		 *            the session
		 * @param server
		 *            the server
		 */
		public ClnDeleteSessionCommandCallback(IRequest request, IResponse response, IResponderCallback callback, Session session,
				StatefulServer server) {
			this.responderCallback = callback;
			this.request = request;
			this.response = response;
			this.session = session;
			this.server = server;
		}

		/** {@inheritDoc} */
		@Override
		public void receive(SCMPMessage reply) {
			// free server from session
			server.removeSession(session);
			String serviceName = reply.getServiceName();
			// forward server reply to client
			reply.setIsReply(true);
			reply.setServiceName(serviceName);
			reply.setMessageType(getKey());
			this.response.setSCMP(reply);
			this.responderCallback.responseCallback(request, response);
			if (reply.isFault()) {
				// delete session failed destroy server
				server.abortSessionsAndDestroy("deleting session failed");
			}
		}

		/** {@inheritDoc} */
		@Override
		public void receive(Exception ex) {
			SCMPMessage fault = null;
			if (ex instanceof IdleTimeoutException) {
				// operation timeout handling
				fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT_EXPIRED,
						"Operation timeout expired on SC cln delete session");
			} else if (ex instanceof IOException) {
				fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln delete session");
			} else {
				fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing cln delete session failed");
			}
			this.receive(fault);
		}
	}
}
