package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.job.impl.DemoMessage;
import com.stabilit.sc.msg.IMessage;
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
 		SCMP scmp= request.getSCMP();
		try {
			ISession session = request.getSession(false);
			if (session == null) {
				throw new CommandException("no session");
			}
			String subscribeId = scmp.getSubscribeId();
			IMessage subscribeMessage = SubscribeQueue.get(subscribeId);
			Integer nextIndex = (Integer)subscribeMessage.getAttribute(SCMP.INDEX); 
			IMessage nextJob = EventQueue.getInstance().get(nextIndex);
			SCMP result = new SCMP(nextJob);
			result.setSubsribeId(subscribeId);
			//System.out.println("returning job = " + nextJob.toString());
			response.setSCMP(result);
			subscribeMessage.setAttribute(SCMP.INDEX, nextIndex + 1); 
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
	            	IMessage demoMsg = new DemoMessage();
	            	demoMsg.setAttribute("index", index++);
	            	EventQueue.getInstance().add(demoMsg);
				} catch (InterruptedException e) {
				}            	
            }
		}
	}
}
