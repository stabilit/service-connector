package com.stabilit.sc.cmd.impl;

import java.net.SocketAddress;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.common.util.ValidatorUtility;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.CommandException;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class RegisterServiceCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(RegisterServiceCommand.class);

	public RegisterServiceCommand() {
		this.commandValidator = new RegisterServiceCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REGISTER_SERVICE;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		request.setAttribute(SocketAddress.class.getName(), socketAddress);
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();

		SCMP scmp = request.getSCMP();
		String serviceName = scmp.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
		MapBean<?> mapBean = serviceRegistry.get(serviceName);

		if (mapBean != null) {
			log.debug("command error: service already registered");
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.ALREADY_REGISTERED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}

		ServiceRegistryItem serviceRegistryItem = new ServiceRegistryItem(scmp, socketAddress);
		serviceRegistry.add(serviceName, serviceRegistryItem);

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME.getName(), serviceName);
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class RegisterServiceCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws Exception {
			SCMP scmp = request.getSCMP();
			Map<String, String> scmpHeader = scmp.getHeader();

			try {
				// serviceName
				String serviceName = (String) scmpHeader.get(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("ServiceName must be set!");
				}

				// maxSessions
				String maxSessions = (String) scmpHeader.get(SCMPHeaderAttributeKey.MAX_SESSIONS.getName());
				maxSessions = ValidatorUtility.validateInt(0, maxSessions);
				request.setAttribute(SCMPHeaderAttributeKey.MAX_SESSIONS.getName(), maxSessions);
				// compression
				Boolean multiThreaded = scmp.getHeaderBoolean(SCMPHeaderAttributeKey.MULTI_THREADED.getName());
				if (multiThreaded == null) {
					multiThreaded = false;
				}
				request.setAttribute(SCMPHeaderAttributeKey.MULTI_THREADED.getName(), multiThreaded);
				// portNr
				String portNr = (String) scmpHeader.get(SCMPHeaderAttributeKey.PORT_NR.getName());
				portNr = ValidatorUtility.validateInt(1, portNr, 99999);
				request.setAttribute(SCMPHeaderAttributeKey.PORT_NR.getName(), portNr);

			} catch (Throwable e) {
				log.debug("validation error: " + e.getMessage());
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}
