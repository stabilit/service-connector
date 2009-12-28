package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;

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
         System.out.println("SubscribeCommand.run()");
	}


}
