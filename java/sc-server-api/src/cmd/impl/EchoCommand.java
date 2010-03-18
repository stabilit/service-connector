package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.MsgType;

public class EchoCommand implements ICommand {

	@Override
	public MsgType getKey() {
		return MsgType.ECHO;
	}
	
	@Override
	public String getKeyName() {
		return this.getKey().getName();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
		SCMP scmp = request.getSCMP();
		try {
			ISession session = request.getSession(true);
			response.setSession(session);
			response.setSCMP(scmp);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}
}
