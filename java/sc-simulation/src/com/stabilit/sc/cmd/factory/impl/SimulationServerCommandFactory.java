package com.stabilit.sc.cmd.factory.impl;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.impl.DataCommand;
import com.stabilit.sc.cmd.impl.EchoCommand;

public class SimulationServerCommandFactory extends CommandFactory {

	public SimulationServerCommandFactory() {
		ICommand echoCommand = new EchoCommand();
		add(echoCommand.getRequestKeyName(), echoCommand);
		//TODO problem anfrage / antwort command
//		ICommand registerCmd = new RegisterServiceCommand();
//		add(registerCmd.getKeyName(), registerCmd);
		ICommand getDataCmd = new DataCommand();
		add(getDataCmd.getRequestKeyName(), getDataCmd);
	}
}
