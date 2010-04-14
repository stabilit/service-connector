package com.stabilit.sc.cmd.impl;

import org.apache.log4j.Logger;

import com.stabilit.sc.cln.msg.impl.InspectMessage;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.registry.SessionRegistry;
import com.stabilit.sc.registry.ConnectionRegistry;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.CommandException;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class InspectCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(InspectCommand.class);
	
	public InspectCommand() {
		this.commandValidator = new InspectCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.INSPECT;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		log.debug("Run command " + this.getKey());
		ConnectionRegistry connectionRegistry = ConnectionRegistry.getCurrentInstance();
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setLocalDateTime();
		InspectMessage inspectMsg = new InspectMessage();
		
		inspectMsg.setAttribute("connectionRegistry", connectionRegistry);
		inspectMsg.setAttribute("serviceRegistry", serviceRegistry);
		inspectMsg.setAttribute("sessionRegistry", sessionRegistry);
		scmpReply.setBody(inspectMsg);
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class InspectCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
		}
	}
}
