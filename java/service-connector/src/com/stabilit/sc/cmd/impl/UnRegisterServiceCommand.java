package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.SCMPCommandException;
import com.stabilit.sc.cmd.SCMPValidatorException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.io.SCMPReply;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.util.MapBean;

public class UnRegisterServiceCommand extends CommandAdapter {

	public UnRegisterServiceCommand() {
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
	public void run(IRequest request, IResponse response) throws CommandException {
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		SCMP scmp = request.getSCMP();
		String serviceName = scmp.getHeader(SCMPHeaderType.SERVICE_NAME.getName());
		MapBean<?> mapBean = serviceRegistry.get(serviceName);
		
		if (mapBean == null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPErrorCode.NOT_REGISTERED);
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
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
			SCMP scmp = request.getSCMP();

			try {

				
			} catch (Throwable e) {
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
