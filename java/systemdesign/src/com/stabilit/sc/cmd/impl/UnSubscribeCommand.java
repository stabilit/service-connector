package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.JobResult;
import com.stabilit.sc.util.SubscribeQueue;

public class UnSubscribeCommand implements ICommand {

	public UnSubscribeCommand() {
	}

	@Override
	public String getKey() {
		return "unsubscribe";
	}

	@Override
	public ICommand newCommand() {
		return new UnSubscribeCommand();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
 		IJob job = request.getJob();
		IJobResult jobResult = new JobResult(job);
		try {
			ISession session = request.getSession(false);
			SubscribeQueue.unsubscribe(job);			
			response.setJobResult(jobResult);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}
