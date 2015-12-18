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
import org.serviceconnector.cmd.casc.CommandCascCallback;
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
import org.serviceconnector.service.FileSession;
import org.serviceconnector.service.Service;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class FileUploadCommand.
 */
public class FileUploadCommand extends CommandAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(FileUploadCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.FILE_UPLOAD;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		// check service is present
		Service abstractService = this.getService(serviceName);
		int oti = message.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

		switch (abstractService.getType()) {
		case CASCADED_FILE_SERVICE:
			CascadedSC cascadedSC = ((CascadedFileService) abstractService).getCascadedSC();
			CommandCascCallback callback = new CommandCascCallback(request, response, responderCallback);
			cascadedSC.serverUploadFile(message, callback, oti);
			return;
		default:
			// code for other types of services is below
			break;
		}

		FileSession session = (FileSession) this.getSessionById(message.getSessionId());
		// sets the time of last execution
		session.resetExecuteTime();
		// reset session timeout to OTI+ECI - during wait for server reply
		int otiOnSCMillis = (int) (oti * basicConf.getOperationTimeoutMultiplier());
		double otiOnSCSeconds = (otiOnSCMillis / Constants.SEC_TO_MILLISEC_FACTOR);
		this.sessionRegistry.resetSessionTimeout(session, (otiOnSCSeconds + session.getSessionTimeoutMillis()));
		SCMPMessage reply = null;
		try {
			String remoteFileName = message.getHeader(SCMPHeaderAttributeKey.REMOTE_FILE_NAME);
			FileServer fileServer = session.getFileServer();
			reply = fileServer.serverUploadFile(session, message, remoteFileName, oti);
		} catch (Exception e) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.FILE_UPLOAD_FAILED,
					"Error occured in file server on SC.");
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		reply.setIsReply(true);
		reply.setMessageType(getKey());
		response.setSCMP(reply);
		// reset session timeout to ECI
		this.sessionRegistry.resetSessionTimeout(session, session.getSessionTimeoutMillis());
		responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		try {
			SCMPMessage message = request.getMessage();
			// remoteFileName mandatory
			String remoteFileName = message.getHeader(SCMPHeaderAttributeKey.REMOTE_FILE_NAME);
			ValidatorUtility.validateStringLengthTrim(1, remoteFileName, Constants.MAX_STRING_LENGTH_256,
					SCMPError.HV_WRONG_REMOTE_FILE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(Constants.MIN_OTI_VALUE_CLN, otiValue, Constants.MAX_OTI_VALUE,
					SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// serviceName mandatory
			String serviceName = message.getServiceName();
			ValidatorUtility.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME,
					SCMPError.HV_WRONG_SERVICE_NAME);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, sessionId, Constants.MAX_STRING_LENGTH_256, SCMPError.HV_WRONG_SESSION_ID);
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