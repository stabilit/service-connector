package com.stabilit.sc.cmd.impl;

import java.util.Map;

import javax.xml.bind.ValidationException;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.registry.SessionRegistry;
import com.stabilit.sc.common.util.ValidatorUtility;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.CommandException;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class ClnDataCommand extends CommandAdapter {

	public ClnDataCommand() {
		this.commandValidator = new ClnDataCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_CLN_DATA;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		
		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class ClnDataCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
			SCMP scmp = request.getSCMP();

			try {
				Map<String, String> scmpHeader = scmp.getHeader();

				// sessionId
				String sessionId = scmp.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessionId must be set!");
				}
				if (SessionRegistry.getCurrentInstance().containsKey(sessionId)) {
					throw new ValidationException("sessoion does not exists!");
				}

				// serviceName
				String serviceName = (String) scmpHeader.get(SCMPHeaderType.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}

				// bodyLength

				// sequenceNr

				// compression
				String compression = (String) scmpHeader.get(SCMPHeaderType.COMPRESSION.getName());
				compression = ValidatorUtility.validateBoolean(compression, true);
				request.setAttribute(SCMPHeaderType.COMPRESSION.getName(), compression);

				// messageInfo
				String messageInfo = (String) scmpHeader.get(SCMPHeaderType.MESSAGE_INFO.getName());
				ValidatorUtility.validateString(0, messageInfo, 256);
			} catch (Throwable e) {
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
