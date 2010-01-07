package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.ISubscribe;
import com.stabilit.sc.job.JobResult;
import com.stabilit.sc.util.SubscribeQueue;

public class SubscribeCommand implements ICommand {

	public SubscribeCommand() {
	}

	@Override
	public String getKey() {
		return "subscribe";
	}

	@Override
	public ICommand newCommand() {
		return new SubscribeCommand();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
 		IJob job = request.getJob();
		IJobResult jobResult = new JobResult(job);
		try {
			ISession session = request.getSession(true);
			response.setSession(session);
			String subscribeId = SubscribeQueue.subscribe(job);
			// set initial event queue read position
			response.setJobResult(jobResult);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}
