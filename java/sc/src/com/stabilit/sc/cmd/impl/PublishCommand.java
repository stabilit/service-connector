package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.SC;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.PublishMessage;

public class PublishCommand extends Command {

	@Override
	public String getKey() {
		return "publish";
	}

	@Override
	public ICommand newCommand() {
		return new PublishCommand();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		SCMP scmp = request.getSCMP();

		try {
			PublishMessage publishMsg = (PublishMessage) scmp.getBody();
			
			SC.getInstance().getSubPubQueue().putNewMsg((String)publishMsg.getAttribute("msg"));		
			
			ISession session = request.getSession(true);
			response.setSession(session);
			response.setSCMP(scmp);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}