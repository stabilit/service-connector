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
package com.stabilit.scm.srv.ps.cmd.impl;

import javax.xml.bind.ValidationException;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SCMessageFault;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.srv.ISCPublishServerCallback;
import com.stabilit.scm.srv.SrvService;
import com.stabilit.scm.srv.rr.cmd.impl.SrvCommandAdapter;

public class SrvSubscribeCommand extends SrvCommandAdapter {

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
		// create scMessage
		ISCMessage scMessage = new SCMessage();
		scMessage.setData(scmpMessage.getBody());
		scMessage.setCompressed(scmpMessage.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(scmpMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(scmpMessage.getSessionId());

		// inform callback with scMessages
		ISCMessage scReply = ((ISCPublishServerCallback) srvService.getCallback()).subscribe(scMessage);
		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setServiceName(serviceName);
		reply.setSessionId(scmpMessage.getSessionId());
		reply.setMessageType(this.getKey());

		if (scReply.isFault()) {
			SCMessageFault scFault = (SCMessageFault) scReply;
			reply.setHeader(SCMPHeaderAttributeKey.REJECT_SESSION, true);
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
					throw new SCMPValidatorException("messageId must be set!");
				}
				// serviceName
				String serviceName = message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException("sessionId must be set!");
				}
				// mask
				String mask = (String) message.getHeader(SCMPHeaderAttributeKey.MASK);
				if (mask == null) {
					throw new SCMPValidatorException("mask must be set!");
				}
				if (mask.indexOf("%") != -1) {
					// percent sign in mask not allowed
					throw new SCMPValidatorException("percent sign found in mask - not allowed.");
				}
				ValidatorUtility.validateString(1, mask, 256);
				// ipAddressList
				String ipAddressList = (String) message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
				ValidatorUtility.validateIpAddressList(ipAddressList);
				// sessionInfo
				String sessionInfo = (String) message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
				ValidatorUtility.validateString(1, sessionInfo, 256);
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(SrvSubscribeCommand.this.getKey());
				throw ex;
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(SrvSubscribeCommand.this.getKey());
				throw validatorException;
			}
		}
	}
}