package com.stabilit.sc.cmd.factory.impl;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.impl.DataCommand;
import com.stabilit.sc.cmd.impl.EchoCommand;
import com.stabilit.sc.cmd.impl.RegisterServiceCommand;

public class ServiceConnectorCommandFactory extends CommandFactory {

	public ServiceConnectorCommandFactory() {
		ICommand echoCommand = new EchoCommand();
		add(echoCommand.getKeyName(), echoCommand);
		ICommand registerCmd = new RegisterServiceCommand();
		add(registerCmd.getKeyName(), registerCmd);
		ICommand getDataCmd = new DataCommand();
		add(getDataCmd.getKeyName(), getDataCmd);
	}
}
