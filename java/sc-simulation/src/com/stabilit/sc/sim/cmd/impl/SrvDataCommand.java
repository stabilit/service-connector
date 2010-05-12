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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationException;

import com.stabilit.sc.cmd.impl.CommandAdapter;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.SCMPReply;
import com.stabilit.sc.scmp.internal.SCMPPart;
import com.stabilit.sc.sim.registry.SimulationSessionRegistry;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;
import com.stabilit.sc.util.MapBean;
import com.stabilit.sc.util.ValidatorUtility;

public class SrvDataCommand extends CommandAdapter {

	public SrvDataCommand() {
		this.commandValidator = new SrvDataCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_DATA;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SimulationSessionRegistry simSessReg = SimulationSessionRegistry
				.getCurrentInstance();
		SCMP scmpReply = new SCMPReply();
		SCMP scmp = request.getSCMP();
		String sessionId = scmp.getSessionId();
		MapBean<Object> mapBean = (MapBean<Object>) simSessReg.get(sessionId);

		if (mapBean == null) {
			if (LoggerListenerSupport.getInstance().isWarn()) {
				LoggerListenerSupport.getInstance().fireWarn(this, "command error: session not found");  
			}
			scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME,
					request.getAttribute(
							SCMPHeaderAttributeKey.SERVICE_NAME.getName())
							.toString());
			scmpReply.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE,
					SCMPErrorCode.SERVER_ERROR.getErrorCode());
			scmpReply.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT,
					SCMPErrorCode.SERVER_ERROR.getErrorText());
			scmpReply.setMessageType(getKey().getResponseName());
			response.setSCMP(scmpReply);
			return;
		}

		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setSessionId(sessionId);
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, scmp
				.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME).toString());
		scmpReply
				.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, "Session info");
		// scmpReply.setHeader(SCMPHeaderType.COMPRESSION, request.getAttribute(
		// SCMPHeaderType.COMPRESSION).toString());

		if ("large".equals(scmp.getBody()) || scmp.isPart()) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (i = 0; i < 10000; i++) {
				if (sb.length() > 60000) {
					break;
				}
				sb.append(i);
			}
			if (i >= 10000) {
				scmpReply.setBody(sb.toString());
				response.setSCMP(scmpReply);
			} else {
				scmpReply = new SCMPPart();
				scmpReply.setMessageType(getKey().getResponseName());
				scmpReply.setSessionId(sessionId);
				scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, scmp
						.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME)
						.toString());
				scmpReply.setHeader(SCMPHeaderAttributeKey.SESSION_INFO,
						"Session info");
				scmpReply.setBody(sb.toString());
				response.setSCMP(scmpReply);
			}
			return;
		}

		List<String> msg = (List<String>) mapBean.getAttribute("messageQueue");

		// init dummy message list
		if (msg == null) {
			msg = new ArrayList<String>();
			for (int i = 0; i < 100; i++) {
				msg.add("Message number " + i);
			}
			mapBean.setAttribute("messageQueueId", 0);
			mapBean.setAttribute("messageQueue", msg);
			scmpReply.setBody(msg.get(0));
			response.setSCMP(scmpReply);
			return;
		}
		int messageQueueId = (Integer) mapBean.getAttribute("messageQueueId");
		messageQueueId++;
		scmpReply.setBody(msg.get(messageQueueId));
		mapBean.setAttribute("messageQueueId", messageQueueId);
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class SrvDataCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMP scmp = request.getSCMP();

			if (scmp.isPart()) {
				return;
			}
			try {
				// sessionId
				String sessionId = scmp.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessionId must be set!");
				}
				if (!SimulationSessionRegistry.getCurrentInstance()
						.containsKey(sessionId)) {
					throw new ValidationException("sessoion does not exists!");
				}

				// serviceName
				String serviceName = (String) scmp.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				request.setAttribute(SCMPHeaderAttributeKey.SERVICE_NAME
						.getName(), serviceName);

				// bodyLength

				// compression
				Boolean compression = scmp
						.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION);
				if (compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION
						.getName(), compression);

				// messageInfo
				String messageInfo = (String) scmp.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
				ValidatorUtility.validateString(0, messageInfo, 256);
				request.setAttribute(SCMPHeaderAttributeKey.MSG_INFO.getName(),
						messageInfo);
			} catch (Throwable e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
