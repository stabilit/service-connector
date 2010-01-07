package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.JobResult;

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
		IJob job = request.getJob();
		IJobResult jobResult = new JobResult(job);
		// System.out.println("EchoCommand.run(): job = " + job.toString());
		try {
			ISession session = request.getSession(true);
			response.setSession(session);
			response.setJobResult(jobResult);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}

}
