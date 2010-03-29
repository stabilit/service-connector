package com.stabilit.sc.cmd.impl;

import java.util.Map;

import javax.xml.bind.ValidationException;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.SCMPValidatorException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.io.SCMPReply;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.util.MapBean;
import com.stabilit.sc.util.ValidatorUtility;

public class AllocateSessionCommand extends CommandAdapter {

	public AllocateSessionCommand() {
		this.commandValidator = new AllocateSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_ALLOCATE_SESSION;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {

		SCMP scmp = request.getSCMP();
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();

		MapBean<Object> mapBean = (MapBean<Object>) sessionRegistry.get(scmp
				.getSessionId());
		SCMPReply scmpReply = new SCMPReply();

		if (mapBean == null) {
			MapBean<Object> newMapBean = new MapBean<Object>();
			newMapBean.setAttribute("available", false);
			sessionRegistry.put(scmp.getSessionId(), newMapBean);
		} else if ((Boolean) mapBean.getAttribute("available")) {
			mapBean.setAttribute("available", false);
			scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), scmp
					.getHeader(SCMPHeaderType.SERVICE_NAME.getName()));
		} else {
			scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), scmp
					.getHeader(SCMPHeaderType.SERVICE_NAME.getName()));
			scmpReply.setHeader(SCMPHeaderType.REJECT_SESSION.getName(), true);
			scmpReply.setHeader(SCMPHeaderType.APP_ERROR_CODE.getName(),
					4334591);
			scmpReply
					.setHeader(SCMPHeaderType.APP_ERROR_TEXT.getName(),
							"%RTXS-E-NOPARTICIPANT, Authorization error - unknown participant");
		}
		scmpReply.setMessageType(getKey().getResponseName());
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class AllocateSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response)
				throws SCMPValidatorException {
			SCMP scmp = request.getSCMP();
			Map<String, String> scmpHeader = scmp.getHeader();
			try {
				// serviceName
				String serviceName = (String) scmpHeader
						.get(SCMPHeaderType.SERVICE_NAME.getName());
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
						.get(SCMPHeaderType.IP_ADDRESS_LIST.getName());
				ValidatorUtility.validateIpAddressList(ipAddressList);

				// sessionInfo
				String sessionInfo = (String) scmpHeader
						.get(SCMPHeaderType.SESSION_INFO.getName());
				ValidatorUtility.validateString(0, sessionInfo, 256);
			} catch (Throwable e) {
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
