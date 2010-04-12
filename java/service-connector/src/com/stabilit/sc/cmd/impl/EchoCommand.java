package com.stabilit.sc.cmd.impl;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPPart;
import com.stabilit.sc.common.io.Session;
import com.stabilit.sc.common.registry.SessionRegistry;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class EchoCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(EchoCommand.class);

	public EchoCommand() {
		this.commandValidator = new EchoCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ECHO;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		SCMP scmp = request.getSCMP();
		Boolean transitive = scmp.getHeaderBoolean(SCMPHeaderType.TRANSITIVE.getName());

		if (!transitive) {
			log.info("echo runs not transitive");
			SCMP scmpReply = null;
			if (scmp.isPart()) {
				String messageId = scmp.getHeader(SCMPHeaderType.SCMP_MESSAGE_ID.getName());
				String sequenceNr = scmp.getHeader(SCMPHeaderType.SEQUENCE_NR.getName());
				String offset = scmp.getHeader(SCMPHeaderType.SCMP_OFFSET.getName());
				scmpReply = new SCMPPart();
				scmpReply.setHeader(SCMPHeaderType.SCMP_MESSAGE_ID.getName(), messageId);
				scmpReply.setHeader(SCMPHeaderType.SEQUENCE_NR.getName(), sequenceNr);
				scmpReply.setHeader(SCMPHeaderType.SCMP_OFFSET.getName(), offset);
			} else {
				scmpReply = new SCMP();
			}
			Object obj = scmp.getBody();
			scmpReply.setMessageType(getKey().getResponseName());
			scmpReply.setSessionId(scmp.getSessionId());
			scmpReply.setBody(obj);
			if (obj.toString().length() > 100) {
				System.out.println("EchoCommand not transitive body = " + obj.toString().substring(0, 100));
			} else {
				System.out.println("EchoCommand not transitive body = " + obj.toString());
			}
			response.setSCMP(scmpReply);
			return;
		}
		log.info("echo runs transitive");

		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		Session session = (Session) sessionRegistry.get(scmp.getSessionId());
		ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) session
				.getAttribute(ServiceRegistryItem.class.getName());

		if (serviceRegistryItem == null) {
			log.debug("command error: serviceRegistryItem not found");
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPErrorCode.SERVER_ERROR);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		if (scmp.getBody().toString().length() > 100) {
			System.out
					.println("EchoCommand TRANSITIVE body = " + scmp.getBody().toString().substring(0, 100));
		} else {
			System.out
					.println("EchoCommand TRANSITIVE body = " + scmp.getBody().toString());
		}
		SCMP result = serviceRegistryItem.echo(scmp);
		result.setMessageType(getKey().getResponseName());
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
