package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.Message;
import com.stabilit.sc.msg.impl.UnSubscribeMessage;
import com.stabilit.sc.util.SubscribeQueue;

public class UnSubscribeCommand extends Command {

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
		System.out.println("UnSubscribeCommand.run()");
		SCMP scmp = request.getSCMP();
		String subscribeId = scmp.getSubscribeId();
		String messageId = scmp.getMessageId();
		if (UnSubscribeMessage.ID.equals(messageId) == false) {
			throw new CommandException("invalid unsubscribe command");
		}
 		UnSubscribeMessage msg = (UnSubscribeMessage) scmp.getBody();
		SCMP scmpResult = new SCMP();
		scmpResult.setSubsribeId(subscribeId);
		IMessage result = new Message(msg.getKey());
		try {
			ISession session = request.getSession(false);
			SubscribeQueue.unsubscribe(subscribeId);
			scmpResult.setBody(result);
			response.setSCMP(scmpResult);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}
