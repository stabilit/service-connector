package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPMsgType;

public class EchoCommand extends CommandAdapter  {

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ECHO;
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
