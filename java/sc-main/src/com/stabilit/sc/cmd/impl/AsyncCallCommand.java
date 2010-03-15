package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.SCKernel;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.AsyncCallMessage;

public class AsyncCallCommand extends Command {

	public AsyncCallCommand() {
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
	public void run(IRequest request, IResponse response) throws CommandException {
		SCMP scmp = request.getSCMP();
		try {
			ISession session = request.getSession(false);
			if (session == null) {
				throw new CommandException("no session");
			}
			String subscribeId = scmp.getSubscribeId();
			String msg = SCKernel.getInstance().getSubPubQueue().getNextMsg(subscribeId);

			SCMP result;
			if (msg == null) {
				result = new SCMP();
			} else {
				result = new SCMP(msg);
			}

			result.setMessageId(AsyncCallMessage.ID);
			AsyncCallMessage asyncM = new AsyncCallMessage();
			result.setSubsribeId(subscribeId);
			result.setBody(asyncM);
			response.setSCMP(result);
		} catch (CommandException e) {
			throw e;
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}
