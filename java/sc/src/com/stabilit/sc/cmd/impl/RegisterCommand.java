package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.RegisterMessage;
import com.stabilit.sc.msg.impl.SubscribeMessage;
import com.stabilit.sc.util.SubscribeQueue;

public class RegisterCommand implements ICommand {

	public RegisterCommand() {
	}

	@Override
	public String getKey() {
		return "register";
	}

	@Override
	public ICommand newCommand() {
		return new RegisterCommand();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		SCMP scmp = request.getSCMP();

		ISession session = request.getSession(true);

		RegisterMessage registerMessage = (RegisterMessage) scmp.getBody();
		// TODO Sc getservice Register list .. and register new ServiceServer!
		try {
			//TODO registerMessage response generieren und zurücksenden!
			SCMP scmpRes = new SCMP(new RegisterMessage());

			response.setSession(session);
			response.setSCMP(scmpRes);

		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}
