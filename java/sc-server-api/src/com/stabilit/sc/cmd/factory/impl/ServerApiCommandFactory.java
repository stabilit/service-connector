package com.stabilit.sc.cmd.factory.impl;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.impl.DataCommand;
import com.stabilit.sc.cmd.impl.EchoCommand;

public class ServerApiCommandFactory extends CommandFactory {

	public ServerApiCommandFactory() {
		ICommand echoCommand = new EchoCommand();
		add(echoCommand.getKeyName(), echoCommand);
		//TODO problem anfrage / antwort command
//		ICommand registerCmd = new RegisterServiceCommand();
//		add(registerCmd.getKeyName(), registerCmd);
		ICommand getDataCmd = new DataCommand();
		add(getDataCmd.getKeyName(), getDataCmd);
	}
}
