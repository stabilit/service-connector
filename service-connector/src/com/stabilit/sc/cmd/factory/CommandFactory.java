package com.stabilit.sc.cmd.factory;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.msg.MsgType;

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
		
	public ICommand newCommand(IRequest request) {
		MsgType key = request.getKey();
		IFactoryable factoryInstance = this.newInstance(key.getName()); 
		return (ICommand)factoryInstance;
	}

}
