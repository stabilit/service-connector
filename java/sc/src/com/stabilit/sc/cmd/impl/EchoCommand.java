package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.app.server.IHTTPServerConnection;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;

public class EchoCommand implements ICommand {

	@Override
	public String getKey() {
		return "echo";
	}

	@Override
	public ICommand newCommand() {
		return new EchoCommand();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
		SCMP scmp = request.getSCMP();
		// System.out.println("EchoCommand.run(): job = " + job.toString());
		try {
			ISession session = request.getSession(true);
			response.setSession(session);
			response.setSCMP(scmp);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}

}
