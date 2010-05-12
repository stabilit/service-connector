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
package com.stabilit.sc.sim.cmd.impl;

import java.util.Map;

import javax.xml.bind.ValidationException;

import com.stabilit.sc.cmd.impl.CommandAdapter;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.SCMPReply;
import com.stabilit.sc.scmp.Session;
import com.stabilit.sc.sim.registry.SimulationSessionRegistry;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;
import com.stabilit.sc.util.MapBean;
import com.stabilit.sc.util.ValidatorUtility;

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
		SCMP scmp = request.getSCMP();
		SimulationSessionRegistry simSessReg = SimulationSessionRegistry
				.getCurrentInstance();

		String sessionId = scmp.getSessionId();
		MapBean<Object> mapBean = (MapBean<Object>) simSessReg.get(sessionId);
		SCMPReply scmpReply = new SCMPReply();

		if (mapBean == null) {
			Session session = new Session();
			session.setAttribute("available", false);
			simSessReg.add(sessionId, (Session) session);
		} else if ((Boolean) mapBean.getAttribute("available")) {
			mapBean.setAttribute("available", false);
			scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, scmp
					.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		} else {
			scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, scmp
					.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
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
			SCMP scmp = request.getSCMP();
			Map<String, String> scmpHeader = scmp.getHeader();
			try {
				// serviceName
				String serviceName = (String) scmpHeader
						.get(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				// sessionId
				String sessionId = scmp.getSessionId();
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
				ExceptionListenerSupport.getInstance().fireException(this, e);
				if (LoggerListenerSupport.getInstance().isException()) {
					LoggerListenerSupport.getInstance().fireException(this,"validation error: " + e.getMessage());
				}
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}