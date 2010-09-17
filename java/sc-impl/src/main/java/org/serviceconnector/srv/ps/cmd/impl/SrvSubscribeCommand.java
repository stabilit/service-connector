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
package org.serviceconnector.srv.ps.cmd.impl;

import org.apache.log4j.Logger;
import org.serviceconnector.common.cmd.ICommandValidator;
import org.serviceconnector.common.cmd.SCMPValidatorException;
import org.serviceconnector.common.scmp.HasFaultResponseException;
import org.serviceconnector.common.scmp.IRequest;
import org.serviceconnector.common.scmp.IResponse;
import org.serviceconnector.common.scmp.SCMPError;
import org.serviceconnector.common.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.scmp.SCMPMessageId;
import org.serviceconnector.common.scmp.SCMPMsgType;
import org.serviceconnector.common.service.ISCMessage;
import org.serviceconnector.service.SCMessage;
import org.serviceconnector.service.SCMessageFault;
import org.serviceconnector.srv.ISCPublishServerCallback;
import org.serviceconnector.srv.SrvService;
import org.serviceconnector.srv.rr.cmd.impl.SrvCommandAdapter;
import org.serviceconnector.util.ValidatorUtility;


public class SrvSubscribeCommand extends SrvCommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SrvSubscribeCommand.class);
	
	public SrvSubscribeCommand() {
		this.commandValidator = new SrvSubscribeCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_SUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		String serviceName = (String) request.getAttribute(SCMPHeaderAttributeKey.SERVICE_NAME);
		// look up srvService
		SrvService srvService = this.getSrvServiceByServiceName(serviceName);

		SCMPMessage scmpMessage = request.getMessage();
		String sessionId = scmpMessage.getSessionId();
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(scmpMessage.getBody());
		scMessage.setCompressed(scmpMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(scmpMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(sessionId);

		// inform callback with scMessages
		ISCMessage scReply = ((ISCPublishServerCallback) srvService.getCallback()).subscribe(scMessage);

		// create session in SCMPSessionCompositeRegistry
		this.sessionCompositeRegistry.addSession(sessionId);
		// handling messageId
		SCMPMessageId messageId = this.sessionCompositeRegistry.getSCMPMessageId(sessionId);

		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, messageId.getCurrentMessageID());
		reply.setServiceName(serviceName);
		reply.setSessionId(sessionId);
		reply.setMessageType(this.getKey());

		if (scReply.isFault()) {
			SCMessageFault scFault = (SCMessageFault) scReply;
			reply.setHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scFault.getAppErrorCode());
			reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scFault.getAppErrorText());
		}
		response.setSCMP(reply);
	}

	private class SrvSubscribeCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();

			try {
				// messageId
				String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID);
				if (messageId == null || messageId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_ID, "messageId must be set");
				}
				// serviceName
				String serviceName = message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
				}
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
				}
				// mask
				String mask = (String) message.getHeader(SCMPHeaderAttributeKey.MASK);
				ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
				if (mask.indexOf("%") != -1) {
					// percent sign in mask not allowed
					throw new SCMPValidatorException(SCMPError.HV_WRONG_MASK, "percent sign not allowed");
				}
				// ipAddressList
				String ipAddressList = (String) message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
				ValidatorUtility.validateIpAddressList(ipAddressList);
				// sessionInfo
				String sessionInfo = (String) message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
				ValidatorUtility.validateStringLength(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(SrvSubscribeCommand.this.getKey());
				throw ex;
			} catch (Throwable th) {
				logger.error("validate", th);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(SrvSubscribeCommand.this.getKey());
				throw validatorException;
			}
		}
	}
}