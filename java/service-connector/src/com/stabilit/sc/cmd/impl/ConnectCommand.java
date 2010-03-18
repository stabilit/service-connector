package com.stabilit.sc.cmd.impl;

import java.net.SocketAddress;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.MsgType;
import com.stabilit.sc.registry.ConnectionRegistry;

public class ConnectCommand implements ICommand {

	public ConnectCommand() {
	}

	@Override
	public MsgType getKey() {
		return MsgType.CONNECT;
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		SCMP scmp = request.getSCMP();
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		ConnectionRegistry connectionRegistry = ConnectionRegistry.getCurrentInstance();
		//TODO is socketAddress the right thing to save a a unique key?
		connectionRegistry.add(socketAddress, scmp);		
	}
	
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	@Override
	public String getKeyName() {
		return this.getKey().getName();
	}
}
