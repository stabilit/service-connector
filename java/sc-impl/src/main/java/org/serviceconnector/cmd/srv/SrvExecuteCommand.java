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
import org.serviceconnector.api.SCMessageFault;
import org.serviceconnector.api.srv.SrvSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageId;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.ValidatorUtility;


/**
 * The Class SrvExecuteCommand. Responsible for validation and execution of server execute command.
 * 
 * @author JTraber
 */
public class SrvExecuteCommand extends SrvCommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SrvExecuteCommand.class);

	/**
	 * Instantiates a new SrvExecuteCommand.
	 */
	public SrvExecuteCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_EXECUTE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		// look up srvService
		SrvSessionService srvService = this.getSrvSessionServiceByServiceName(serviceName);

		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setOperationTimeout(Integer.parseInt(reqMessage.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT)));
		scMessage.setSessionId(reqMessage.getSessionId());

		// inform callback with scMessages
		SCMessage scReply = srvService.getCallback().execute(scMessage);

		// handling messageId
		SCMPMessageId messageId = SrvCommandAdapter.sessionCompositeRegistry.getSCMPMessageId(reqMessage.getSessionId());
		messageId.incrementMsgSequenceNr();
		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, messageId.getCurrentMessageID());
		reply.setMessageType(this.getKey());
		if (scReply.isCompressed()) {
			reply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
		}
		String msgInfo = scReply.getMessageInfo();
		if (msgInfo != null) {
			reply.setHeader(SCMPHeaderAttributeKey.MSG_INFO, msgInfo);
		}
		reply.setBody(scReply.getData());

		if (scReply.isFault()) {
			SCMessageFault scFault = (SCMessageFault) scReply;
			reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scFault.getAppErrorCode());
			reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scFault.getAppErrorText());
		}
		response.setSCMP(reply);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();

		try {
			// messageId
			String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID.getValue());
			if (messageId == null || messageId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_ID, "messageId must be set");
			}
			// sessionId
			String sessionId = message.getSessionId();
			if (sessionId == null || sessionId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
			}
			// serviceName
			String serviceName = (String) message.getServiceName();
			if (serviceName == null || serviceName.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
			}
			// message info
			String messageInfo = (String) message.getHeader(SCMPHeaderAttributeKey.MSG_INFO.getValue());
			if (messageInfo != null) {
				ValidatorUtility.validateStringLength(1, messageInfo, 256, SCMPError.HV_WRONG_MESSAGE_INFO);
			}
			// compression
			message.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
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
