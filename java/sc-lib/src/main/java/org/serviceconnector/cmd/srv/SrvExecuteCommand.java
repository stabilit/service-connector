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
import org.serviceconnector.api.srv.SrvSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SrvExecuteCommand. Responsible for validation and execution of server execute command.
 * 
 * @author JTraber
 */
public class SrvExecuteCommand extends SrvCommandAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SrvExecuteCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_EXECUTE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// look up srvService
		SrvSessionService srvService = this.getSrvSessionServiceByServiceName(serviceName);

		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setCacheId(reqMessage.getCacheId());
		scMessage.setCachePartNr(reqMessage.getHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER));
		scMessage.setServiceName(reqMessage.getServiceName());
		scMessage.setSessionId(reqMessage.getSessionId());

		// inform callback with scMessages
		SCMessage scReply = srvService.getCallback().execute(scMessage,
				Integer.parseInt(reqMessage.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT)));

		// handling msgSequenceNr
		SCMPMessageSequenceNr msgSequenceNr = SrvCommandAdapter.sessionCompositeRegistry.getSCMPMsgSequenceNr(reqMessage
				.getSessionId());
		msgSequenceNr.incrementAndGetMsgSequenceNr();
		// set up reply - SCMP Version request
		SCMPMessage reply = new SCMPMessage(reqMessage.getSCMPVersion());
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
		reply.setMessageType(this.getKey());
		if (scReply != null) {
			reply.setBody(scReply.getData());
			if (scReply.getCacheExpirationDateTime() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME,
						DateTimeUtility.getDateTimeAsString(scReply.getCacheExpirationDateTime()));
			}
			reply.setCacheId(scReply.getCacheId());
			if (scReply.getMessageInfo() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.MSG_INFO, scReply.getMessageInfo());
			}
			if (scReply.isCompressed()) {
				reply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
			}
			if (scReply.getAppErrorCode() != Constants.EMPTY_APP_ERROR_CODE) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scReply.getAppErrorCode());
			}
			if (scReply.getAppErrorText() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scReply.getAppErrorText());
			}
			if (scReply.getCachingMethod() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.CACHING_METHOD, scReply.getCachingMethod().getValue());
			}
			reply.setPartSize(scReply.getPartSize());
		}
		response.setSCMP(reply);
		responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();

		try {
			// msgSequenceNr mandatory
			String msgSequenceNr = message.getMessageSequenceNr();
			ValidatorUtility.validateLong(1, msgSequenceNr, SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			ValidatorUtility.validateStringLengthTrim(1, sessionId, Constants.MAX_STRING_LENGTH_256, SCMPError.HV_WRONG_SESSION_ID);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(Constants.MIN_OTI_VALUE_SRV, otiValue, Constants.MAX_OTI_VALUE,
					SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// serviceName mandatory
			String serviceName = message.getServiceName();
			ValidatorUtility.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME,
					SCMPError.HV_WRONG_SERVICE_NAME);
			// message info optional
			String messageInfo = message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
			ValidatorUtility.validateStringLengthIgnoreNull(1, messageInfo, Constants.MAX_STRING_LENGTH_256,
					SCMPError.HV_WRONG_MESSAGE_INFO);
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
