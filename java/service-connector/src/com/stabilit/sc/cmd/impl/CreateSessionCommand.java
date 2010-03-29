package com.stabilit.sc.cmd.impl;

import java.util.Map;

import javax.xml.bind.ValidationException;

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
import com.stabilit.sc.io.Session;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.util.MapBean;
import com.stabilit.sc.util.ValidatorUtility;

public class CreateSessionCommand extends CommandAdapter {

	public CreateSessionCommand() {
		this.commandValidator = new CreateSessionCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_CREATE_SESSION;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {

		try {
			// get free service
			SCMP scmp = request.getSCMP();
			String serviceName = scmp.getHeader(SCMPHeaderType.SERVICE_NAME
					.getName());
			ServiceRegistry serviceRegistry = ServiceRegistry
					.getCurrentInstance();

			MapBean<?> mapBean = serviceRegistry.get(serviceName);

			if (mapBean == null) {
				SCMPCommandException scmpCommandException = new SCMPCommandException(
						SCMPErrorCode.UNKNOWN_SERVICE);
				scmpCommandException.setMessageType(getKey().getResponseName());
				throw scmpCommandException;
			}
			SessionRegistry sessionRegistry = SessionRegistry
					.getCurrentInstance();
			
			// create session
			Session session = new Session();
			scmp.setSessionId(session.getId());
			// try to allocate session
			ServiceRegistryItem serviceRegistryItem = serviceRegistry
					.allocate(serviceName, scmp.getHeader());
			
			// finally save session
			session.setAttribute(ServiceRegistryItem.class.getName(),
					serviceRegistryItem);
			sessionRegistry.put(session.getId(), session);
			
			// reply
			SCMPReply scmpReply = new SCMPReply();
			scmpReply.setMessageType(getKey().getResponseName());
			scmpReply.setSessionId(session.getId());
			scmpReply.setHeader(SCMPHeaderType.SERVICE_NAME.getName(),
					serviceName);
			response.setSCMP(scmpReply);
		} catch (Throwable e) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.SERVER_ERROR);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class CreateSessionCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response)
				throws SCMPValidatorException {
			Map<String, String> scmpHeader = request.getSCMP().getHeader();

			try {
				// serviceName
				String serviceName = (String) scmpHeader
						.get(SCMPHeaderType.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
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
