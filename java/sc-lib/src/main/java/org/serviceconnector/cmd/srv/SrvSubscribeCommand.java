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
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.SrvPublishService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.ValidatorUtility;

public class SrvSubscribeCommand extends SrvCommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SrvSubscribeCommand.class);

	public SrvSubscribeCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_SUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// look up srvService
		SrvPublishService srvService = this.getSrvPublishServiceByServiceName(serviceName);

		String sessionId = reqMessage.getSessionId();
		// create scMessage
		SCSubscribeMessage scMessage = new SCSubscribeMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		scMessage.setSessionId(sessionId);
		scMessage.setMask(reqMessage.getHeader(SCMPHeaderAttributeKey.MASK));
		scMessage.setServiceName(reqMessage.getServiceName());

		// inform callback with scMessages
		SCMessage scReply = srvService.getCallback().subscribe(scMessage,
				Integer.parseInt(reqMessage.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT)));

		// set up reply
		SCMPMessage reply = new SCMPMessage();

		if (scReply != null) {
			reply.setBody(scReply.getData());
			if (scReply.isCompressed()) {
				reply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
			}
			if (scReply.getAppErrorCode() != Constants.EMPTY_APP_ERROR_CODE) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scReply.getAppErrorCode());
			}
			if (scReply.getAppErrorText() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scReply.getAppErrorText());
			}
			if (scReply.isReject()) {
				// subscription rejected
				reply.setHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			} else {
				// create session in SCMPSessionCompositeRegistry
				SrvCommandAdapter.sessionCompositeRegistry.addSession(sessionId);
				// handling msgSequenceNr
				SCMPMessageSequenceNr msgSequenceNr = SrvCommandAdapter.sessionCompositeRegistry.getSCMPMsgSequenceNr(sessionId);
				reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
			}
		}
		reply.setServiceName(serviceName);
		reply.setSessionId(sessionId);
		reply.setMessageType(this.getKey());
		response.setSCMP(reply);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();

		try {
			// msgSequenceNr mandatory
			String msgSequenceNr = message.getMessageSequenceNr();
			if (msgSequenceNr == null || msgSequenceNr.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR, "msgSequenceNr must be set");
			}
			// serviceName mandatory
			String serviceName = message.getServiceName();
			if (serviceName == null || serviceName.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
			}
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT.getValue());
			ValidatorUtility.validateInt(10, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId mandatory
			String sessionId = message.getSessionId();
			if (sessionId == null || sessionId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
			}
			// mask mandatory
			String mask = message.getHeader(SCMPHeaderAttributeKey.MASK);
			ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
			// ipAddressList mandatory
			String ipAddressList = message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
			ValidatorUtility.validateIpAddressList(ipAddressList);
			// sessionInfo optional
			String sessionInfo = message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
			ValidatorUtility.validateStringLengthIgnoreNull(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(SrvSubscribeCommand.this.getKey());
			throw ex;
		} catch (Throwable th) {
			logger.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(SrvSubscribeCommand.this.getKey());
			throw validatorException;
		}
	}
}
