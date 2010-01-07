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
import com.stabilit.sc.job.impl.DemoJob;
import com.stabilit.sc.util.EventQueue;
import com.stabilit.sc.util.SubscribeQueue;

public class AsyncCallCommand implements ICommand {

	public AsyncCallCommand() {
		
	}
	
	public AsyncCallCommand(boolean demo) {
		if (demo == true) {
			Thread thread = new Thread(new AsyncEchoJobCreator());
			thread.start();
		}
	}

	@Override
	public String getKey() {
		return "asyncCall";
	}

	@Override
	public ICommand newCommand() {
		return new AsyncCallCommand();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
 		IJob job = request.getJob();
		try {
			ISession session = request.getSession(false);
			if (session == null) {
				throw new CommandException("no session");
			}
			IJob subscribeJob = SubscribeQueue.get(job);
			int nextIndex = (Integer)subscribeJob.getAttribute(ISubscribe.INDEX); 
			IJob nextJob = EventQueue.getInstance().get(nextIndex);
			IJobResult jobResult = new JobResult(nextJob);
			//System.out.println("returning job = " + nextJob.toString());
			response.setJobResult(jobResult);
			subscribeJob.setAttribute(ISubscribe.INDEX, nextIndex + 1); 
		} catch (CommandException e) {
			throw e;
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
	
	public static class AsyncEchoJobCreator implements Runnable {
	
		private int index = 0;
		@Override
		public void run() {
            while (true) {
            	try {
					Thread.sleep(1000);
	            	IJob demoJob = new DemoJob();
	            	demoJob.setAttribute("index", index++);
	            	EventQueue.getInstance().add(demoJob);
				} catch (InterruptedException e) {
				}            	
            }
		}
	}
}
