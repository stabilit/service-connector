package com.stabilit.sc.cmd.impl;

import org.apache.log4j.Logger;

import com.stabilit.sc.cln.msg.impl.MaintenanceMessage;
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

public class MaintenanceCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(MaintenanceCommand.class);
	
	public MaintenanceCommand() {
		this.commandValidator = new MaintenanceCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_MAINTENANCE;
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
		MaintenanceMessage mainMsg = new MaintenanceMessage();
		
		mainMsg.setAttribute("connectionRegistry", connectionRegistry);
		mainMsg.setAttribute("serviceRegistry", serviceRegistry);
		mainMsg.setAttribute("sessionRegistry", sessionRegistry);
		scmpReply.setBody(mainMsg);
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class MaintenanceCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
		}
	}

}
