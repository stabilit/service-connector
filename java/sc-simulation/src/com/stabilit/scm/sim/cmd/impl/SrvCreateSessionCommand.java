/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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

import com.stabilit.scm.cmd.impl.CommandAdapter;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.listener.ExceptionPoint;
import com.stabilit.scm.listener.LoggerPoint;
import com.stabilit.scm.scmp.IRequest;
import com.stabilit.scm.scmp.IResponse;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMsgType;
import com.stabilit.scm.scmp.Session;
import com.stabilit.scm.sim.registry.SimulationSessionRegistry;
import com.stabilit.scm.srv.cmd.ICommandValidator;
import com.stabilit.scm.srv.cmd.SCMPValidatorException;
import com.stabilit.scm.util.MapBean;
import com.stabilit.scm.util.ValidatorUtility;

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
		SCMPMessage message = request.getMessage();
		SimulationSessionRegistry simSessReg = SimulationSessionRegistry
				.getCurrentInstance();

		String sessionId = message.getSessionId();
		MapBean<Object> mapBean = (MapBean<Object>) simSessReg.get(sessionId);
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);

		if (mapBean == null) {
			Session session = new Session();
			session.setAttribute("available", false);
			simSessReg.add(sessionId, (Session) session);
		} else if ((Boolean) mapBean.getAttribute("available")) {
			mapBean.setAttribute("available", false);
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
		scmpReply.setMessageType(getKey().getResponseName());
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class SrvCreateSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();
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
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}