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
import org.serviceconnector.server.FileServer;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.FileService;
import org.serviceconnector.service.FileSession;
import org.serviceconnector.service.NoFreeServerException;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.Session;
import org.serviceconnector.service.SessionService;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnCreateSessionCommand. Responsible for validation and execution of creates session command. Command runs successfully
 * if backend server accepts clients request and allows creating a session. Session is saved in a session registry of SC.
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
		Service abstractService = this.validateService(serviceName);

		String ipAddressList = (String) reqMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		String sessionInfo = (String) reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
		int eci = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.ECHO_INTERVAL);

		switch (abstractService.getType()) {
		case SESSION_SERVICE:
			// code for type session service is below switch statement
			break;
		case FILE_SERVICE:
			FileService fileService = (FileService) abstractService;
			// create file session
			FileSession fileSession = new FileSession(sessionInfo, ipAddressList, fileService.getPath(), fileService
					.getUploadFileScriptName(), fileService.getGetFileListScriptName());
			FileServer fileServer = fileService.allocateFileServerAndCreateSession(fileSession);
			// add server to session
			fileSession.setServer(fileServer);
			fileSession.setSessionTimeoutSeconds(eci * basicConf.getEchoIntervalMultiplier());
			// finally add file session to the registry
			this.sessionRegistry.addSession(fileSession.getId(), fileSession);
			// reply to client
			SCMPMessage reply = new SCMPMessage();
			reply.setIsReply(true);
			reply.setMessageType(getKey());
			reply.setSessionId(fileSession.getId());
			response.setSCMP(reply);
			return;
		case CASCADED_SERVICE:
			// TODO JOT cascaded service
			break;
		default:
			throw new SCMPCommandException(SCMPError.SC_ERROR, "create session command not allowed for service " + serviceName);
		}

		// create session
		Session session = new Session(sessionInfo, ipAddressList);
		reqMessage.setSessionId(session.getId());
		// no need to forward echo attributes
		reqMessage.removeHeader(SCMPHeaderAttributeKey.ECHO_INTERVAL);

		// tries allocating a server for this session
		StatefulServer server = null;
		CommandCallback callback = null;
		try {
			int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

			int tries = (int) ((oti * basicConf.getOperationTimeoutMultiplier()) / Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
			// Following loop implements the wait mechanism in case of a busy connection pool
			int i = 0;
			int otiOnServerMillis = 0;
			do {
				callback = new CommandCallback(true);
				try {
					otiOnServerMillis = oti - (i * Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
					server = ((SessionService) abstractService).allocateServerAndCreateSession(reqMessage, callback, session,
							otiOnServerMillis);
					// no exception has been thrown - get out of wait loop
					break;
				} catch (NoFreeServerException ex) {
					if (i >= (tries - 1)) {
						// only one loop outstanding - don't continue throw current exception
						throw ex;
					}
				} catch (ConnectionPoolBusyException ex) {
					if (i >= (tries - 1)) {
						// only one loop outstanding - don't continue throw current exception
						SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION,
								"no free connection on server for service " + reqMessage.getServiceName());
						scmpCommandException.setMessageType(this.getKey());
						throw scmpCommandException;
					}
				} catch (Exception ex) {
					throw ex;
				}
				// sleep for a while and then try again
				Thread.sleep(Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
			} while (++i < tries);

			SCMPMessage reply = callback.getMessageSync(otiOnServerMillis);

			if (reply.isFault()) {
				// response is an error - remove session id from header
				reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
				// remove session from server
				server.removeSession(session);
			} else {
				boolean rejectSessionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
				if (rejectSessionFlag) {
					// session has been rejected by the server - remove session id from header
					reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
					// remove session from server
					server.removeSession(session);
				} else {
					// session has not accepted, add server to session
					session.setServer(server);
					session.setSessionTimeoutSeconds(eci * basicConf.getEchoIntervalMultiplier());
					// finally add session to the registry
					this.sessionRegistry.addSession(session.getId(), session);
				}
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
			// msgSequenceNr
			String msgSequenceNr = message.getMessageSequenceNr();
			if (msgSequenceNr == null || msgSequenceNr.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR, "msgSequenceNr must be set");
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
			ValidatorUtility.validateStringLengthIgnoreNull(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
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
