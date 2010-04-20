package com.stabilit.sc.sim.cmd.impl;

import javax.xml.bind.ValidationException;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.sim.registry.SimulationSessionRegistry;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class DeAllocateSessionCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(DeAllocateSessionCommand.class);

	public DeAllocateSessionCommand() {
		this.commandValidator = new DeAllocateSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.DEALLOCATE_SESSION;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		SCMP scmp = request.getSCMP();
		SimulationSessionRegistry simSessReg = SimulationSessionRegistry.getCurrentInstance();

		String sessionId = scmp.getSessionId();
		MapBean<Object> mapBean = (MapBean<Object>) simSessReg.get(sessionId);

		if (mapBean == null) {
			log.debug("command error: session is no allocated");
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPErrorCode.NOT_ALLOCATED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		simSessReg.remove(sessionId);

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, scmp
				.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		scmpReply.setMessageType(getKey().getResponseName());
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class DeAllocateSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws Exception {
			SCMP scmp = request.getSCMP();

			try {
				// serviceName
				String serviceName = (String) scmp.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				// sessionId
				String sessionId = scmp.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessonId must be set!");
				}
			} catch (Throwable e) {
				log.debug("validation error: " + e.getMessage());
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}