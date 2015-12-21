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
package org.serviceconnector.cmd.srv;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SrvService;
import org.serviceconnector.api.srv.SrvServiceRegistry;
import org.serviceconnector.api.srv.SrvSessionService;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SrvAbortSessionCommand. Responsible for validation and execution of abort session command. Aborts an active session on
 * server.
 * 
 * @author JTraber
 */
public class SrvAbortSessionCommand extends SrvCommandAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SrvAbortSessionCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_ABORT_SESSION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws SCMPCommandException {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// look up srvService
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		IResponder responder = AppContext.getResponderRegistry().getCurrentResponder();
		int listenerPort = responder.getListenerConfig().getPort();
		SrvService srvService = srvServiceRegistry.getSrvService(serviceName + "_" + listenerPort);
		String sessionId = reqMessage.getSessionId();

		if (srvService == null) {
			// service not available anymore set up reply - SCMP Version request
			SCMPMessage reply = new SCMPMessage(reqMessage.getSCMPVersion());
			reply.setServiceName(serviceName);
			reply.setSessionId(reqMessage.getSessionId());
			reply.setMessageType(this.getKey());
			response.setSCMP(reply);
			// delete session in SCMPSessionCompositeRegistry
			SrvCommandAdapter.sessionCompositeRegistry.removeSession(sessionId);
			responderCallback.responseCallback(request, response);
			return;
		}

		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(sessionId);
		scMessage.setServiceName(reqMessage.getServiceName());

		int oti = Integer.parseInt(reqMessage.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT));

		// inform callback with scMessagesF
		((SrvSessionService) srvService).getCallback().abortSession(scMessage, oti);

		// set up reply - SCMP version request
		SCMPMessage reply = new SCMPMessage(reqMessage.getSCMPVersion());
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		reply.setMessageType(this.getKey());
		response.setSCMP(reply);
		// delete session in SCMPSessionCompositeRegistry
		SrvCommandAdapter.sessionCompositeRegistry.removeSession(sessionId);
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
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, sessionId, Constants.MAX_STRING_LENGTH_256, SCMPError.HV_WRONG_SESSION_ID);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(Constants.MIN_OTI_VALUE_SRV, otiValue, Constants.MAX_OTI_VALUE,
					SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sc error code mandatory
			String sec = message.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE);
			if (sec == null || sec.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SC_ERROR_CODE, "sc error code must be set");
			}
			// sc error text mandatory
			String set = message.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT);
			if (set == null || set.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SC_ERROR_TEXT, "sc error text must be set");
			}
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
