package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.message.IMessage;
import com.stabilit.sc.message.IMessageResult;
import com.stabilit.sc.message.ISubscribe;
import com.stabilit.sc.message.MessageResult;
import com.stabilit.sc.message.impl.DemoMessage;
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
 		IMessage job = request.getJob();
		try {
			ISession session = request.getSession(false);
			if (session == null) {
				throw new CommandException("no session");
			}
			IMessage subscribeJob = SubscribeQueue.get(job);
			int nextIndex = (Integer)subscribeJob.getAttribute(ISubscribe.INDEX); 
			IMessage nextJob = EventQueue.getInstance().get(nextIndex);
			IMessageResult MessageResult = new MessageResult(nextJob);
			//System.out.println("returning job = " + nextJob.toString());
			response.setJobResult(MessageResult);
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
	            	IMessage demoJob = new DemoMessage();
	            	demoJob.setAttribute("index", index++);
	            	EventQueue.getInstance().add(demoJob);
				} catch (InterruptedException e) {
				}            	
            }
		}
	}
}
