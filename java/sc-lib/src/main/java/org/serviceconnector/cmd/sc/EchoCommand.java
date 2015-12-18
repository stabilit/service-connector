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
import org.serviceconnector.service.CascadedSessionService;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class EchoCommand. Responsible for validation and execution of echo command. Used to refresh session on SC.
 * 
 * @author JTraber
 */
public class EchoCommand extends CommandAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(EchoCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ECHO;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		// check service is present
		Service abstractService = this.getService(serviceName);

		// enhance ipAddressList
		String ipAddressList = message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		ipAddressList = ipAddressList + Constants.SLASH + request.getRemoteSocketAddress().getAddress().getHostAddress();
		message.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipAddressList);

		switch (abstractService.getType()) {
		case CASCADED_SESSION_SERVICE:
			int oti = message.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			CascadedSC cascadedSC = ((CascadedSessionService) abstractService).getCascadedSC();
			CommandCascCallback callback = new CommandCascCallback(request, response, responderCallback);
			cascadedSC.echo(message, callback, oti);
			return;
		default:
			// code for other types of services is below
			break;
		}
		String sessionId = message.getSessionId();
		Session session = this.getSessionById(sessionId);
		message.setIsReply(true);
		response.setSCMP(message);

		synchronized (session) {
			// needs to be in sync because of setting pending request & reseting timer
			if (session.hasPendingRequest() == false) {
				// reset session timeout to ECI - only if no pending request on session
				this.sessionRegistry.resetSessionTimeout(session, session.getSessionTimeoutMillis());
			}
		}
		responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();
		try {
			// serviceName mandatory
			String serviceName = message.getServiceName();
			ValidatorUtility.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME,
					SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(Constants.MIN_OTI_VALUE_CLN, otiValue, Constants.MAX_OTI_VALUE,
					SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, sessionId, Constants.MAX_STRING_LENGTH_256, SCMPError.HV_WRONG_SESSION_ID);
		} catch (HasFaultResponseException ex) {
			LOGGER.warn("validation error " + ex.toString());
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