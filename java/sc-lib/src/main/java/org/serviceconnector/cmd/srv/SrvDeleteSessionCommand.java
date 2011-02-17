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
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.SrvSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SrvDeleteSessionCommand. Responsible for validation and execution of server delete session command. Allows deleting
 * session on backend server.
 * 
 * @author JTraber
 */
public class SrvDeleteSessionCommand extends SrvCommandAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(SrvDeleteSessionCommand.class);

	/**
	 * Instantiates a new SrvDeleteSessionCommand.
	 */
	public SrvDeleteSessionCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_DELETE_SESSION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// look up srvService
		SrvSessionService srvService = this.getSrvSessionServiceByServiceName(serviceName);
		String sessionId = reqMessage.getSessionId();
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(sessionId);
		scMessage.setServiceName(reqMessage.getServiceName());
		scMessage.setSessionInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		try {
			// inform callback with scMessages
			srvService.getCallback().deleteSession(scMessage,
					Integer.parseInt(reqMessage.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT)));
			// handling msgSequenceNr
			SCMPMessageSequenceNr msgSequenceNr = SrvCommandAdapter.sessionCompositeRegistry.getSCMPMsgSequenceNr(sessionId);
			msgSequenceNr.incrementAndGetMsgSequenceNr();
			// set up reply
			SCMPMessage reply = new SCMPMessage();
			reply.setServiceName(serviceName);
			reply.setSessionId(reqMessage.getSessionId());
			reply.setMessageType(this.getKey());
			reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
			response.setSCMP(reply);
		} finally {
			// delete session in SCMPSessionCompositeRegistry
			SrvCommandAdapter.sessionCompositeRegistry.removeSession(sessionId);
			responderCallback.responseCallback(request, response);
		}
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
			ValidatorUtility.validateStringLengthTrim(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(100, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, sessionId, 256, SCMPError.HV_WRONG_SESSION_ID);
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
