package com.stabilit.sc.sim.cmd.impl;

import javax.xml.bind.ValidationException;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.registry.SessionRegistry;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.CommandException;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class DeAllocateSessionCommand extends CommandAdapter {

	public DeAllocateSessionCommand() {
		this.commandValidator = new DeAllocateSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_DEALLOCATE_SESSION;
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

		if (mapBean == null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.NOT_ALLOCATED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		sessionRegistry.remove("key");

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), scmp
				.getHeader(SCMPHeaderType.SERVICE_NAME.getName()));
		scmpReply.setMessageType(getKey().getResponseName());
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class DeAllocateSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response)
				throws SCMPValidatorException {
			SCMP scmp = request.getSCMP();

			try {
				// serviceName
				String serviceName = (String) scmp
						.getHeader(SCMPHeaderType.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				// sessionId
				String sessionId = scmp.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessonId must be set!");
				}
			} catch (Throwable e) {
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
