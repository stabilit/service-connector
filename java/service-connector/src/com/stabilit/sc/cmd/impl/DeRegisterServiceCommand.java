package com.stabilit.sc.cmd.impl;

import java.util.Map;

import javax.xml.bind.ValidationException;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.CommandException;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class DeRegisterServiceCommand extends CommandAdapter {

	public DeRegisterServiceCommand() {
		this.commandValidator = new UnRegisterServiceCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_DEREGISTER_SERVICE;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		SCMP scmp = request.getSCMP();
		String serviceName = scmp.getHeader(SCMPHeaderType.SERVICE_NAME
				.getName());
		MapBean<?> mapBean = serviceRegistry.get(serviceName);

		if (mapBean == null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.NOT_REGISTERED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		serviceRegistry.remove(serviceName);
		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), serviceName);
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class UnRegisterServiceCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response)
				throws SCMPValidatorException {
			Map<String, String> scmpHeader = request.getSCMP().getHeader();

			try {
				// serviceName
				String serviceName = (String) scmpHeader
						.get(SCMPHeaderType.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("ServiceName must be set!");
				}
			} catch (Throwable e) {
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
