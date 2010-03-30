package com.stabilit.sc.srv.cmd.factory;

import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.srv.cmd.ICommand;

public class CommandFactory extends Factory {

	protected static CommandFactory commandFactory = null;

	public CommandFactory() {
	}

	public static CommandFactory getCurrentCommandFactory() {
		return commandFactory;
	}

	public static void setCurrentCommandFactory(CommandFactory commandFactory) {
		CommandFactory.commandFactory = commandFactory;
	}

	public void init(CommandFactory commandFactory) {

	}

	public ICommand newCommand(IRequest request) {
		SCMPMsgType key = request.getKey();
		IFactoryable factoryInstance = this.newInstance(key.getRequestName());
		return (ICommand) factoryInstance;
	}

}
