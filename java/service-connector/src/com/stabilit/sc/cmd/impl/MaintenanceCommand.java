package com.stabilit.sc.cmd.impl;

import java.net.SocketAddress;
import java.util.Date;
import java.util.Map;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.SCMPCommandException;
import com.stabilit.sc.cmd.SCMPValidatorException;
import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.KeepAlive;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.io.SCMPReply;
import com.stabilit.sc.msg.impl.ConnectMessage;
import com.stabilit.sc.msg.impl.MaintenanceMessage;
import com.stabilit.sc.registry.ConnectionRegistry;
import com.stabilit.sc.util.MapBean;
import com.stabilit.sc.util.ValidatorUtility;

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

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(SCMPMsgType.RES_MAINTENANCE.getResponseName());
		scmpReply.setLocalDateTime();
		MaintenanceMessage mainMsg = new MaintenanceMessage();
		
		mainMsg.setAttribute("connectionRegistry", connectionRegistry);
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
