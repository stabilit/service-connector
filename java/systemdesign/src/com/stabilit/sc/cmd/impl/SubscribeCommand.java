package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.SubscribeMessage;
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
 		SCMP scmp = request.getSCMP();
 		if (SubscribeMessage.ID.equals(scmp.getMessageId()) == false) {
 			throw new CommandException("not supported message id");
 		}
 		SubscribeMessage subscribeMessage = (SubscribeMessage)scmp.getBody();
 		SubscribeMessage result = new SubscribeMessage();
		try {
			ISession session = request.getSession(true);
			response.setSession(session);
			String subscribeId = SubscribeQueue.subscribe(subscribeMessage);
			// set initial event queue read position
			scmp.setSubsribeId(subscribeId);
			response.setSCMP(scmp);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}
