package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;

public class GetDataCommand implements ICommand {

	@Override
	public String getKey() {
		return "getData";
	}

	@Override
	public ICommand newCommand() {
		return new GetDataCommand();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
		SCMP scmp = request.getSCMP();
		
		
		//TODO 
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
