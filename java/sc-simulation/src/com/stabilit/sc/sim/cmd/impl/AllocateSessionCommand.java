package com.stabilit.sc.sim.cmd.impl;

import java.util.Map;

import javax.xml.bind.ValidationException;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.io.Session;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.common.util.ValidatorUtility;
import com.stabilit.sc.sim.registry.SimulationSessionRegistry;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class AllocateSessionCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(AllocateSessionCommand.class);

	public AllocateSessionCommand() {
		this.commandValidator = new AllocateSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ALLOCATE_SESSION;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		SCMP scmp = request.getSCMP();
		SimulationSessionRegistry simSessReg = SimulationSessionRegistry.getCurrentInstance();

		String sessionId = scmp.getSessionId();
		MapBean<Object> mapBean = (MapBean<Object>) simSessReg.get(sessionId);
		SCMPReply scmpReply = new SCMPReply();

		if (mapBean == null) {
			Session session = new Session();
			session.setAttribute("available", false);
			simSessReg.add(sessionId, (Session) session);
		} else if ((Boolean) mapBean.getAttribute("available")) {
			mapBean.setAttribute("available", false);
			scmpReply.setHeader(SCMPHeaderAttributeType.SERVICE_NAME.getName(), scmp
					.getHeader(SCMPHeaderAttributeType.SERVICE_NAME.getName()));
		} else {
			scmpReply.setHeader(SCMPHeaderAttributeType.SERVICE_NAME.getName(), scmp
					.getHeader(SCMPHeaderAttributeType.SERVICE_NAME.getName()));
			scmpReply.setHeader(SCMPHeaderAttributeType.REJECT_SESSION.getName(), true);
			scmpReply.setHeader(SCMPHeaderAttributeType.APP_ERROR_CODE.getName(), 4334591);
			scmpReply.setHeader(SCMPHeaderAttributeType.APP_ERROR_TEXT.getName(),
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
		public void validate(IRequest request, IResponse response) throws Exception {
			SCMP scmp = request.getSCMP();
			Map<String, String> scmpHeader = scmp.getHeader();
			try {
				// serviceName
				String serviceName = (String) scmpHeader.get(SCMPHeaderAttributeType.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				// sessionId
				String sessionId = scmp.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessonId must be set!");
				}
				// ipAddressList
				String ipAddressList = (String) scmpHeader.get(SCMPHeaderAttributeType.IP_ADDRESS_LIST.getName());
				ValidatorUtility.validateIpAddressList(ipAddressList);

				// sessionInfo
				String sessionInfo = (String) scmpHeader.get(SCMPHeaderAttributeType.SESSION_INFO.getName());
				ValidatorUtility.validateString(0, sessionInfo, 256);
			} catch (Throwable e) {
				log.debug("validation error: " + e.getMessage());
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
