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
package com.stabilit.scm.srv.rr.cmd.impl;

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
import com.stabilit.scm.srv.ISCSessionServerCallback;
import com.stabilit.scm.srv.SrvService;

public class SrvDataCommand extends SrvCommandAdapter {

	public SrvDataCommand() {
		this.commandValidator = new SrvDataCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_DATA;
	}

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
		ISCMessage scReply = ((ISCSessionServerCallback) srvService.getCallback()).execute(scMessage);
		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setServiceName(serviceName);
		reply.setSessionId(scmpMessage.getSessionId());
		reply.setMessageType(this.getKey().getValue());
		String msgInfo = scReply.getMessageInfo();
		if (msgInfo != null) {
			reply.setHeader(SCMPHeaderAttributeKey.MSG_INFO, msgInfo);
		}
		reply.setBody(scReply.getData());
		response.setSCMP(reply);
	}

	public class SrvDataCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();

			if (message.isPart()) {
				return;
			}
			try {
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException("sessonId must be set!");
				}

				// serviceName
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
				request.setAttribute(SCMPHeaderAttributeKey.SERVICE_NAME.getValue(), serviceName);

				// bodyLength

				// compression
				Boolean compression = message.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION);
				if (compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION.getValue(), compression);
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(getKey());
				throw ex;
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}
}
