package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.SCMPValidatorException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.io.SCMPReply;
import com.stabilit.sc.msg.impl.MaintenanceMessage;
import com.stabilit.sc.registry.ConnectionRegistry;
import com.stabilit.sc.registry.ServiceRegistry;

public class MaintenanceCommand extends CommandAdapter {

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
		ConnectionRegistry connectionRegistry = ConnectionRegistry.getCurrentInstance();
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setLocalDateTime();
		MaintenanceMessage mainMsg = new MaintenanceMessage();
		
		mainMsg.setAttribute("connectionRegistry", connectionRegistry);
		mainMsg.setAttribute("serviceRegistry", serviceRegistry);
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
