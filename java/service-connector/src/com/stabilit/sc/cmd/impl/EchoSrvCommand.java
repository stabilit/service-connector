package com.stabilit.sc.cmd.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderAttributeType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPPart;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.io.Session;
import com.stabilit.sc.common.registry.SessionRegistry;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class EchoSrvCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(EchoSrvCommand.class);

	public EchoSrvCommand() {
		this.commandValidator = new EchoSCCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ECHO_SRV;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		SCMP scmp = request.getSCMP();
		Map<String, String> header = scmp.getHeader();

		int maxNodes = scmp.getHeaderInt(SCMPHeaderAttributeType.MAX_NODES.getName());
		SCMP result = null;

		String ipList = header.get(SCMPHeaderAttributeType.IP_ADDRESS_LIST.getName());
		SocketAddress socketAddress = request.getSocketAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			scmp.setHeader(SCMPHeaderAttributeType.IP_ADDRESS_LIST.getName(), ipList);
		}

		if (maxNodes == 1) {
			if (scmp.isPart()) {
				result = new SCMPPart();
				String messageId = scmp.getHeader(SCMPHeaderAttributeType.SCMP_MESSAGE_ID.getName());
				result.setHeader(SCMPHeaderAttributeType.SCMP_MESSAGE_ID.getName(), messageId);
				String callLength = scmp.getHeader(SCMPHeaderAttributeType.SCMP_CALL_LENGTH.getName());
				result.setHeader(SCMPHeaderAttributeType.SCMP_CALL_LENGTH.getName(), callLength);
				String scmpOffset = scmp.getHeader(SCMPHeaderAttributeType.SCMP_OFFSET.getName());
				result.setHeader(SCMPHeaderAttributeType.SCMP_OFFSET.getName(), scmpOffset);
			} else {
				result = new SCMPReply();
			}

			result.setBody(scmp.getBody());
			result.setHeader("scmpCompositeSequence", scmp.getHeader("scmpCompositeSequence"));
			result.setSessionId(scmp.getSessionId());
			result.setHeader(SCMPHeaderAttributeType.IP_ADDRESS_LIST.getName(), ipList);
		} else {
			SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
			Session session = (Session) sessionRegistry.get(scmp.getSessionId());
			ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) session
					.getAttribute(ServiceRegistryItem.class.getName());

			if (serviceRegistryItem == null) {
				log.debug("command error: serviceRegistryItem not found");
				SCMPCommandException scmpCommandException = new SCMPCommandException(
						SCMPErrorCode.SERVER_ERROR);
				scmpCommandException.setMessageType(getKey().getResponseName());
				throw scmpCommandException;
			}
			if (scmp.getBody().toString().length() > 100) {
				System.out.println("EchoSrvCommand body = " + scmp.getBody().toString().substring(0, 100));
			} else {
				System.out.println("EchoSrvCommand body = " + scmp.getBody().toString());
			}
			header.remove(SCMPHeaderAttributeType.MAX_NODES.getName());
			--maxNodes;
			header.put(SCMPHeaderAttributeType.MAX_NODES.getName(), String.valueOf(maxNodes));
			result = serviceRegistryItem.echoSrv(scmp);
		}
		result.setMessageType(getKey().getResponseName());
		response.setSCMP(result);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class EchoSCCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
		}
	}
}
