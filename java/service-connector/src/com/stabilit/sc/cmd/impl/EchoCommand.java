package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class EchoCommand extends CommandAdapter {

	public EchoCommand() {
		this.commandValidator = new EchoCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_ECHO;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {

		SCMP scmp = request.getSCMP();
		Boolean transitive = scmp.getHeaderBoolean(SCMPHeaderType.TRANSITIVE.getName());

		if (!transitive) {
			Object obj = scmp.getBody();
			SCMP scmpReply = new SCMP();
			scmpReply.setMessageType(getKey().getResponseName());
			scmpReply.setSessionId(scmp.getSessionId());
			scmpReply.setBody(obj);
			System.out.println("EchoCommand not transitive body = " + obj.toString());
			response.setSCMP(scmpReply);
			return;
		}

		// get free service
		String serviceName = scmp.getHeader(SCMPHeaderType.SERVICE_NAME.getName());
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();

		ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) serviceRegistry.get(serviceName);

		if (serviceRegistryItem == null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.UNKNOWN_SERVICE);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		System.out.println("EchoCommand TRANSITIVE body = " + scmp.getBody().toString());
		SCMP result = serviceRegistryItem.echo(scmp);
		response.setSCMP(result);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class EchoCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
		}
	}

}
