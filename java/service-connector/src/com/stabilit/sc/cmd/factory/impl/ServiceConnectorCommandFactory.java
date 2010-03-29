package com.stabilit.sc.cmd.factory.impl;

import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.impl.ConnectCommand;
import com.stabilit.sc.cmd.impl.CreateSessionCommand;
import com.stabilit.sc.cmd.impl.DeRegisterServiceCommand;
import com.stabilit.sc.cmd.impl.DeleteSessionCommand;
import com.stabilit.sc.cmd.impl.DisconnectCommand;
import com.stabilit.sc.cmd.impl.EchoCommand;
import com.stabilit.sc.cmd.impl.MaintenanceCommand;
import com.stabilit.sc.cmd.impl.RegisterServiceCommand;

public class ServiceConnectorCommandFactory extends CommandFactory {

	public ServiceConnectorCommandFactory() {
		init(this);
	}
	public ServiceConnectorCommandFactory(CommandFactory commandFactory) {
		init(commandFactory);
	}
	public void init(CommandFactory commandFactory) {
		ICommand echoCommand = new EchoCommand();
		commandFactory.add(echoCommand.getRequestKeyName(), echoCommand);
		ICommand maintenanceCommand = new MaintenanceCommand();
		commandFactory.add(maintenanceCommand.getRequestKeyName(), maintenanceCommand);
		ICommand connectCommand = new ConnectCommand();
		commandFactory.add(connectCommand.getRequestKeyName(), connectCommand);
		ICommand disconnectCommand = new DisconnectCommand();
		commandFactory.add(disconnectCommand.getRequestKeyName(), disconnectCommand);
		ICommand createSessionCommand = new CreateSessionCommand();
		commandFactory.add(createSessionCommand.getRequestKeyName(), createSessionCommand);
		ICommand deleteSessionCommand = new DeleteSessionCommand();
		commandFactory.add(deleteSessionCommand.getRequestKeyName(), deleteSessionCommand);
		ICommand registerServiceCommand = new RegisterServiceCommand();
		commandFactory.add(registerServiceCommand.getRequestKeyName(), registerServiceCommand);
		ICommand deRegisterServiceCommand = new DeRegisterServiceCommand();
		commandFactory.add(deRegisterServiceCommand.getRequestKeyName(), deRegisterServiceCommand);
	}
}
