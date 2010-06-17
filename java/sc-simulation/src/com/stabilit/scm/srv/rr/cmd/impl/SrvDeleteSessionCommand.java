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
import com.stabilit.scm.common.log.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.cmd.impl.CommandAdapter;
import com.stabilit.scm.sc.service.Session;
import com.stabilit.scm.srv.rr.registry.SessionServerSessionRegistry;

public class SrvDeleteSessionCommand extends CommandAdapter {

	public SrvDeleteSessionCommand() {
		this.commandValidator = new SrvDeleteSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_DELETE_SESSION;
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage message = request.getMessage();
		SessionServerSessionRegistry simSessReg = SessionServerSessionRegistry.getCurrentInstance();

		String sessionId = message.getSessionId();
		Session session = this.getSessionById(sessionId);
		simSessReg.removeSession(session);

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, message.getServiceName());
		scmpReply.setMessageType(getKey().getName());
		response.setSCMP(scmpReply);
	}

	public class SrvDeleteSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();

			try {
				// serviceName
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException("sessonId must be set!");
				}
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