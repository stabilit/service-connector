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
package com.stabilit.scm.sim.cmd.impl;

import java.util.Map;

import javax.xml.bind.ValidationException;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.log.listener.ExceptionPoint;
import com.stabilit.scm.common.log.listener.LoggerPoint;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.cmd.impl.CommandAdapter;
import com.stabilit.scm.sc.service.Session;
import com.stabilit.scm.sim.registry.SimulationSessionRegistry;

public class SrvCreateSessionCommand extends CommandAdapter {

	public SrvCreateSessionCommand() {
		this.commandValidator = new SrvCreateSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_CREATE_SESSION;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage message = request.getSCMP();
		SimulationSessionRegistry simSessReg = SimulationSessionRegistry
				.getCurrentInstance();

		String sessionId = message.getSessionId();
		Session session = simSessReg.getSession(sessionId);
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);

		if (session == null) {
			session = new Session();
			session.setAttribute("available", false);
			simSessReg.add(sessionId, (Session) session);
		} else if ((Boolean) session.getAttribute("available")) {
			session.setAttribute("available", false);
			scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, message
					.getServiceName());
		} else {
			scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, message
					.getServiceName());
			scmpReply.setHeader(SCMPHeaderAttributeKey.REJECT_SESSION, true);
			scmpReply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, 4334591);
			scmpReply
					.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT,
							"%RTXS-E-NOPARTICIPANT, Authorization error - unknown participant");
		}
		scmpReply.setMessageType(getKey().getName());
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class SrvCreateSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getSCMP();
			Map<String, String> scmpHeader = message.getHeader();
			try {
				// serviceName
				String serviceName = (String) scmpHeader
						.get(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessonId must be set!");
				}
				// ipAddressList
				String ipAddressList = (String) scmpHeader
						.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getName());
				ValidatorUtility.validateIpAddressList(ipAddressList);

				// sessionInfo
				String sessionInfo = (String) scmpHeader
						.get(SCMPHeaderAttributeKey.SESSION_INFO.getName());
				ValidatorUtility.validateString(0, sessionInfo, 256);
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				if (LoggerPoint.getInstance().isException()) {
					LoggerPoint.getInstance().fireException(this,"validation error: " + e.getMessage());
				}
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getName());
				throw validatorException;
			}
		}
	}
}