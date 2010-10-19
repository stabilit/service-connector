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
import org.serviceconnector.service.NoFreeSessionException;
import org.serviceconnector.service.Session;
import org.serviceconnector.service.StatefulServer;
import org.serviceconnector.service.SessionService;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnCreateSessionCommand. Responsible for validation and execution of creates session command. Command runs
 * successfully if backend server accepts clients request and allows creating a session. Session is saved in a session
 * registry of SC.
 * 
 * @author JTraber
 */
public class ClnCreateSessionCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnCreateSessionCommand.class);

	/**
	 * Instantiates a new ClnCreateSessionCommand.
	 */
	public ClnCreateSessionCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_CREATE_SESSION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// check service is present
		SessionService service = this.validateSessionService(serviceName);

		String ipAddressList = (String) reqMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		String sessionInfo = (String) reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
		// create session
		Session session = new Session(sessionInfo, ipAddressList);
		reqMessage.setSessionId(session.getId());
		// no need to forward echo attributes
		int eci = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.ECHO_INTERVAL);
		reqMessage.removeHeader(SCMPHeaderAttributeKey.ECHO_INTERVAL);

		// tries allocating a server for this session
		StatefulServer server = null;
		CommandCallback callback = null;
		try {
			int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

			int tries = (int) ((oti * Constants.OPERATION_TIMEOUT_MULTIPLIER) / Constants.WAIT_FOR_CONNECTION_INTERVAL_MILLIS);
			// Following loop implements the wait mechanism in case of a busy connection pool
			int i = 0;
			do {
				callback = new CommandCallback(true);
				try {
					server = service.allocateServerAndCreateSession(reqMessage, callback, session, oti
							- (i * Constants.WAIT_FOR_CONNECTION_INTERVAL_MILLIS));
					// no exception has been thrown - get out of wait loop
					break;
				} catch (NoFreeSessionException ex) {
					if (i >= (tries - 1)) {
						// only one loop outstanding - don't continue throw current exception
						throw ex;
					}
				} catch (ConnectionPoolBusyException ex) {
					if (i >= (tries - 1)) {
						// only one loop outstanding - don't continue throw current exception
						SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.SC_ERROR,
								"no free connection on server for service " + reqMessage.getServiceName());
						scmpCommandException.setMessageType(this.getKey());
						throw scmpCommandException;
					}
				} catch (Exception ex) {
					throw ex;
				}
				// sleep for a while and then try again
				Thread.sleep(Constants.WAIT_FOR_CONNECTION_INTERVAL_MILLIS);
			} while (++i < tries);

			SCMPMessage reply = callback.getMessageSync();

			if (reply.isFault() == false) {
				boolean rejectSessionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
				if (Boolean.FALSE.equals(rejectSessionFlag)) {
					// session has not been rejected, add server to session
					session.setServer(server);
					session.setEchoIntervalSeconds(eci * Constants.ECHO_INTERVAL_MULTIPLIER);
					// finally add session to the registry
					this.sessionRegistry.addSession(session.getId(), session);
				} else {
					// session has been rejected - remove session id from header
					reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
					// creation failed remove from server
					server.removeSession(session);
				}
			} else {
				// session has been rejected - remove session id from header
				reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
				// creation failed remove from server
				server.removeSession(session);
			}
			// forward server reply to client
			reply.setIsReply(true);
			reply.setMessageType(getKey());
			response.setSCMP(reply);
		} catch (Exception e) {
			if (server != null) {
				// creation failed remove from server
				server.removeSession(session);
			}
			throw e;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		try {
			SCMPMessage message = request.getMessage();
			// messageId
			String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID.getValue());
			if (messageId == null || messageId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_ID, "messageId must be set");
			}
			// serviceName
			String serviceName = (String) message.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME.getValue());
			if (serviceName == null || serviceName.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
			}
			// operation timeout
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT.getValue());
			ValidatorUtility.validateInt(10, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// ipAddressList
			String ipAddressList = (String) message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getValue());
			ValidatorUtility.validateIpAddressList(ipAddressList);
			// sessionInfo
			String sessionInfo = (String) message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO.getValue());
			if (sessionInfo != null) {
				ValidatorUtility.validateStringLength(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
			}
			// echoInterval
			String echoIntervalValue = message.getHeader(SCMPHeaderAttributeKey.ECHO_INTERVAL.getValue());
			ValidatorUtility.validateInt(1, echoIntervalValue, 3600, SCMPError.HV_WRONG_ECHO_INTERVAL);
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
