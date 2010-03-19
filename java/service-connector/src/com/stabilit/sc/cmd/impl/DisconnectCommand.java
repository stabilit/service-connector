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
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.io.SCMPReply;
import com.stabilit.sc.registry.ConnectionRegistry;
import com.stabilit.sc.util.MapBean;

public class DisconnectCommand extends CommandAdapter {

	public DisconnectCommand() {
		this.commandValidator = new DisconnectCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_DISCONNECT;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		ConnectionRegistry connectionRegistry = ConnectionRegistry.getCurrentInstance();
		// TODO is socketAddress the right thing to save as a unique key?
		MapBean<?> mapBean = connectionRegistry.get(socketAddress);		
		if(mapBean == null) {
			throw new SCMPCommandException(SCMPErrorCode.NOT_CONNECTED);			
		}
		connectionRegistry.remove(socketAddress);
		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(SCMPMsgType.REQ_DISCONNECT.getResponseName());
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class DisconnectCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
		}
	}
}
