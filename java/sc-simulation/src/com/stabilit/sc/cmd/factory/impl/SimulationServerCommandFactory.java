package com.stabilit.sc.cmd.factory.impl;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.impl.AllocateSessionCommand;
import com.stabilit.sc.cmd.impl.DeAllocateSessionCommand;

public class SimulationServerCommandFactory extends CommandFactory {

	public SimulationServerCommandFactory() {
		init(this);
	}
	
	public SimulationServerCommandFactory(CommandFactory commandFactory) {
       init(commandFactory);
	}
	
	public void init(CommandFactory commandFactory) {
		ICommand allocateSessionCommand = new AllocateSessionCommand();
		commandFactory.add(allocateSessionCommand.getRequestKeyName(), allocateSessionCommand);
		ICommand deAllocateSessionCommand = new DeAllocateSessionCommand();
		commandFactory.add(deAllocateSessionCommand.getRequestKeyName(), deAllocateSessionCommand);
	}
}
