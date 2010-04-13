package com.stabilit.sc.sim.cmd.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.apache.log4j.Logger;

import com.stabilit.sc.cln.service.MessageID;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPPart;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.common.util.ValidatorUtility;
import com.stabilit.sc.sim.registry.SimulationSessionRegistry;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class SrvDataCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(SrvDataCommand.class);

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
		log.debug("Run command " + this.getKey());
		SimulationSessionRegistry simSessReg = SimulationSessionRegistry.getCurrentInstance();

		String sessionId = request.getSessionId();
		MapBean<Object> mapBean = (MapBean<Object>) simSessReg.get(sessionId);
		SCMP scmpReply = new SCMPReply();
		SCMP scmp = request.getSCMP();

		if (mapBean == null) {
			log.debug("command error: session not found");
			scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), request.getAttribute(
					SCMPHeaderType.SERVICE_NAME.getName()).toString());
			scmpReply.setHeader(SCMPHeaderType.SC_ERROR_CODE.getName(), SCMPErrorCode.SERVER_ERROR
					.getErrorCode());
			scmpReply.setHeader(SCMPHeaderType.SC_ERROR_TEXT.getName(), SCMPErrorCode.SERVER_ERROR
					.getErrorText());
			scmpReply.setHeader(SCMPHeaderType.SEQUENCE_NR.getName(), scmp
					.getHeader(SCMPHeaderType.SEQUENCE_NR.getName()));
			scmpReply.setMessageType(getKey().getResponseName());
			response.setSCMP(scmpReply);
			return;
		}

		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setSessionId(sessionId);
		scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), scmp.getHeader(
				SCMPHeaderType.SERVICE_NAME.getName()).toString());
		scmpReply.setHeader(SCMPHeaderType.SESSION_INFO.getName(), "Session info");
		scmpReply.setHeader(SCMPHeaderType.SEQUENCE_NR.getName(), scmp.getHeader(SCMPHeaderType.SEQUENCE_NR
				.getName()));
		// scmpReply.setHeader(SCMPHeaderType.COMPRESSION.getName(), request.getAttribute(
		// SCMPHeaderType.COMPRESSION.getName()).toString());

		int scmpOffsetInt = 0;
		String scmpOffset = null;
		if (scmp.isPart()) {
			scmpOffset = scmp.getHeader(SCMPHeaderType.SCMP_OFFSET.getName());
			scmpOffsetInt = Integer.parseInt(scmpOffset);
		}

		if ("large".equals(scmp.getBody()) || scmp.isPart()) {
			String messageID = MessageID.getNextAsString();
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (i = scmpOffsetInt; i < 1000000; i++) {
				if (sb.length() > 60000) {
					break;
				}
				sb.append(i);
			}
			if (i >= 1000000) {
				scmpReply.setBody(sb.toString());
				response.setSCMP(scmpReply);
			} else {
				scmpReply = new SCMPPart();
				scmpReply.setMessageType(getKey().getResponseName());
				((SCMPPart)scmpReply).setMessageId(messageID);
				scmpReply.setSessionId(sessionId);
				scmpReply.setHeader(SCMPHeaderType.SEQUENCE_NR.getName(), scmp
						.getHeader(SCMPHeaderType.SEQUENCE_NR.getName()));
				scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), scmp.getHeader(
						SCMPHeaderType.SERVICE_NAME.getName()).toString());
				scmpReply.setHeader(SCMPHeaderType.SESSION_INFO.getName(), "Session info");
				if (scmp.isPart()) {
					scmpReply.setHeader(SCMPHeaderType.SCMP_MESSAGE_ID.getName(), scmp.getHeader(
							SCMPHeaderType.SCMP_MESSAGE_ID.getName()).toString());
				}
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
		public void validate(IRequest request, IResponse response) throws Exception {
			SCMP scmp = request.getSCMP();

			if (scmp.isPart()) {
				return;
			}
			try {
				Map<String, String> scmpHeader = scmp.getHeader();

				// sessionId
				String sessionId = scmp.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessionId must be set!");
				}
				if (!SimulationSessionRegistry.getCurrentInstance().containsKey(sessionId)) {
					throw new ValidationException("sessoion does not exists!");
				}

				// serviceName
				String serviceName = (String) scmpHeader.get(SCMPHeaderType.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				request.setAttribute(SCMPHeaderType.SERVICE_NAME.getName(), serviceName);

				// bodyLength

				// sequenceNr

				// compression
				Boolean compression = scmp.getHeaderBoolean(SCMPHeaderType.COMPRESSION.getName());
				if (compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderType.COMPRESSION.getName(), compression);

				// messageInfo
				String messageInfo = (String) scmpHeader.get(SCMPHeaderType.MESSAGE_INFO.getName());
				ValidatorUtility.validateString(0, messageInfo, 256);
				request.setAttribute(SCMPHeaderType.MESSAGE_INFO.getName(), messageInfo);
			} catch (Throwable e) {
				log.debug("validation error: " + e.getMessage());
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
