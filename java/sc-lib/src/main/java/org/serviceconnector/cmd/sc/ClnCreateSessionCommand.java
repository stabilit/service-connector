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
import org.serviceconnector.cmd.casc.ClnCommandCascCallback;
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
import org.serviceconnector.service.CascadedFileService;
import org.serviceconnector.service.CascadedSessionService;
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
	private final static Logger logger = Logger.getLogger(ClnCreateSessionCommand.class);

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
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// check service is present and enabled
		Service abstractService = this.getService(serviceName);
		if (abstractService.isEnabled() == false) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.SERVICE_DISABLED, "service="
					+ abstractService.getName() + " is disabled");
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}

		// enhance ipAddressList
		String ipAddressList = reqMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		ipAddressList = ipAddressList + request.getRemoteSocketAddress().getAddress();
		reqMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipAddressList);
		String sessionInfo = reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
		int eci = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.ECHO_INTERVAL);
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

		switch (abstractService.getType()) {
		case CASCADED_SESSION_SERVICE:
			CascadedSC cascadedSC = ((CascadedSessionService) abstractService).getCascadedSC();
			ClnCommandCascCallback callback = new ClnCommandCascCallback(request, response, responderCallback);
			cascadedSC.createSession(reqMessage, callback, oti);
			return;
		case CASCADED_FILE_SERVICE:
			cascadedSC = ((CascadedFileService) abstractService).getCascadedSC();
			callback = new ClnCommandCascCallback(request, response, responderCallback);
			cascadedSC.createSession(reqMessage, callback, oti);
			return;
		case SESSION_SERVICE:
			// code for type session service is below switch statement
			break;
		case FILE_SERVICE:
			FileService fileService = (FileService) abstractService;
			// create file session
			FileSession fileSession = new FileSession(sessionInfo, ipAddressList, fileService.getPath(), fileService
					.getUploadFileScriptName(), fileService.getGetFileListScriptName());
			fileSession.setService(fileService);
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
			responderCallback.responseCallback(request, response);
			return;
		}

		// create session
		Session session = new Session(sessionInfo, ipAddressList);
		session.setService(abstractService);
		session.setSessionTimeoutSeconds(eci * basicConf.getEchoIntervalMultiplier());
		reqMessage.setSessionId(session.getId());
		// no need to forward echo attributes
		reqMessage.removeHeader(SCMPHeaderAttributeKey.ECHO_INTERVAL);

		// tries allocating a server for this session
		ClnCreateSessionCommandCallback callback = null;

		int otiOnSCMillis = (int) (oti * basicConf.getOperationTimeoutMultiplier());
		int tries = (otiOnSCMillis / Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		// Following loop implements the wait mechanism in case of a busy connection pool
		int i = 0;
		do {
			callback = new ClnCreateSessionCommandCallback(request, response, responderCallback, session);
			try {
				((SessionService) abstractService).allocateServerAndCreateSession(reqMessage, callback, session, otiOnSCMillis
						- (i * Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS));
				// no exception has been thrown - get out of wait loop
				break;
			} catch (NoFreeServerException ex) {
				logger.debug("NoFreeServerException caught in wait mec of create session");
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					throw ex;
				}
			} catch (ConnectionPoolBusyException ex) {
				logger.debug("ConnectionPoolBusyException caught in wait mec of create session");
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					logger.warn(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + reqMessage.getServiceName()));
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION, "service="
							+ reqMessage.getServiceName());
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
		try {
			SCMPMessage message = request.getMessage();
			// msgSequenceNr mandatory
			String msgSequenceNr = message.getMessageSequenceNr();
			ValidatorUtility.validateLong(1, msgSequenceNr, SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR);
			// serviceName mandatory
			String serviceName = message.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
			ValidatorUtility.validateStringLengthTrim(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(1000, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// ipAddressList mandatory
			String ipAddressList = message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
			ValidatorUtility.validateIpAddressList(ipAddressList);
			// echoInterval mandatory
			String echoIntervalValue = message.getHeader(SCMPHeaderAttributeKey.ECHO_INTERVAL);
			ValidatorUtility.validateInt(10, echoIntervalValue, 3600, SCMPError.HV_WRONG_ECHO_INTERVAL);
			// sessionInfo optional
			String sessionInfo = message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
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
