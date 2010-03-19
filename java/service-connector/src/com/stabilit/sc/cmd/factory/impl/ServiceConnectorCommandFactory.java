package com.stabilit.sc.cmd.factory.impl;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.impl.ConnectCommand;
import com.stabilit.sc.cmd.impl.DataCommand;
import com.stabilit.sc.cmd.impl.DisconnectCommand;
import com.stabilit.sc.cmd.impl.EchoCommand;
import com.stabilit.sc.cmd.impl.RegisterServiceCommand;

public class ServiceConnectorCommandFactory extends CommandFactory {

	public ServiceConnectorCommandFactory() {
		ICommand echoCommand = new EchoCommand();
		add(echoCommand.getRequestKeyName(), echoCommand);
		ICommand connectCommand = new ConnectCommand();
		add(connectCommand.getRequestKeyName(), connectCommand);
		ICommand disconnectCommand = new DisconnectCommand();
		add(disconnectCommand.getRequestKeyName(), disconnectCommand);
		ICommand registerCmd = new RegisterServiceCommand();
		add(registerCmd.getRequestKeyName(), registerCmd);
		ICommand getDataCmd = new DataCommand();
		add(getDataCmd.getRequestKeyName(), getDataCmd);
	}
}
