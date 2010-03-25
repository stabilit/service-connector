package com.stabilit.sc.cmd.impl;

import java.net.SocketAddress;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.SCMPCommandException;
import com.stabilit.sc.cmd.SCMPValidatorException;
import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.io.SCMPReply;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.util.MapBean;

public class RegisterServiceCommand extends CommandAdapter {

	public RegisterServiceCommand() {
		this.commandValidator = new RegisterServiceCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_REGISTER_SERVICE;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		request.setAttribute(SocketAddress.class.getName(), socketAddress);
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		
		SCMP scmp = request.getSCMP();
		String serviceName = scmp.getHeader(SCMPHeaderType.SERVICE_NAME.getName());
		MapBean<?> mapBean = serviceRegistry.get(serviceName);

		if (mapBean != null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.ALREADY_REGISTERED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		ServiceRegistryItem serviceRegistryItem = new ServiceRegistryItem(scmp);
		serviceRegistry.add(serviceName, serviceRegistryItem);
		
		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setLocalDateTime();
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class RegisterServiceCommandValidator implements ICommandValidator {

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
