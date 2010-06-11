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

import javax.xml.bind.ValidationException;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.log.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.cmd.impl.CommandAdapter;

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
		SCMPMessage message = request.getSCMP();
		response.setSCMP(message);
		return;
		
//		SCMPMessage scmpReply = new SCMPMessage();
//		scmpReply.setIsReply(true);
//			
//		String sessionId = message.getSessionId();
//
//		SimulationSessionRegistry simSessReg = SimulationSessionRegistry
//		.getCurrentInstance();
//		MapBean<Object> mapBean = (MapBean<Object>) simSessReg.get(sessionId);
//
//		if (mapBean == null) {
//			if (LoggerPoint.getInstance().isWarn()) {
//				LoggerPoint.getInstance().fireWarn(this, "command error: session not found");  
//			}
//			scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME,
//					request.getAttribute(
//							SCMPHeaderAttributeKey.SERVICE_NAME.getName())
//							.toString());
//			scmpReply.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE,
//					SCMPError.SERVER_ERROR.getErrorCode());
//			scmpReply.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT,
//					SCMPError.SERVER_ERROR.getErrorText());
//			scmpReply.setMessageType(getKey().getResponseName());
//			response.setSCMP(scmpReply);
//			return;
//		}
//
//		scmpReply.setMessageType(getKey().getResponseName());
//		scmpReply.setSessionId(sessionId);
//		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, message
//				.getServiceName().toString());
//		scmpReply
//				.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, "Session info");
//		// scmpReply.setHeader(SCMPHeaderType.COMPRESSION, request.getAttribute(
//		// SCMPHeaderType.COMPRESSION).toString());
//
//		if (message.getBody().toString().startsWith("large") || message.isPart()) {
//			StringBuilder sb = new StringBuilder();
//			int i = 0;
//			sb.append("large:");
//			for (i = 0; i < 10000; i++) {
//				if (sb.length() > 60000) {
//					break;
//				}
//				sb.append(i);
//			}
//			if (i >= 10000) {
//				scmpReply.setBody(sb.toString());
//				response.setSCMP(scmpReply);
//			} else {
//				scmpReply = new SCMPPart();
//				scmpReply.setMessageType(getKey().getResponseName());
//				scmpReply.setSessionId(sessionId);
//				scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, message
//						.getServiceName()
//						.toString());
//				scmpReply.setHeader(SCMPHeaderAttributeKey.SESSION_INFO,
//						"Session info");
//				scmpReply.setBody(sb.toString());
//				response.setSCMP(scmpReply);
//			}
//			return;
//		}
//
//		List<String> msg = (List<String>) mapBean.getAttribute("messageQueue");
//
//		// init dummy message list
//		if (msg == null) {
//			msg = new ArrayList<String>();
//			for (int i = 0; i < 1000000; i++) {
//				msg.add("Message number " + i);
//			}
//			mapBean.setAttribute("messageQueueId", 0);
//			mapBean.setAttribute("messageQueue", msg);
//			scmpReply.setBody(msg.get(0));
//			response.setSCMP(scmpReply);
//			return;
//		}
//		int messageQueueId = (Integer) mapBean.getAttribute("messageQueueId");
//		messageQueueId++;
//		scmpReply.setBody(msg.get(messageQueueId));
//		mapBean.setAttribute("messageQueueId", messageQueueId);
//		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class SrvDataCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getSCMP();

			if (message.isPart()) {
				return;
			}
			try {
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessionId must be set!");
				}

				// serviceName
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				request.setAttribute(SCMPHeaderAttributeKey.SERVICE_NAME
						.getName(), serviceName);

				// bodyLength

				// compression
				Boolean compression = message
						.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION);
				if (compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION
						.getName(), compression);

				// messageInfo
				String messageInfo = (String) message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
				ValidatorUtility.validateString(0, messageInfo, 256);
				request.setAttribute(SCMPHeaderAttributeKey.MSG_INFO.getName(),
						messageInfo);
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
