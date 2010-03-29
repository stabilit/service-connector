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
		ICommand echoCommand = new EchoCommand();
		add(echoCommand.getRequestKeyName(), echoCommand);
		ICommand maintenanceCommand = new MaintenanceCommand();
		add(maintenanceCommand.getRequestKeyName(), maintenanceCommand);
		ICommand connectCommand = new ConnectCommand();
		add(connectCommand.getRequestKeyName(), connectCommand);
		ICommand disconnectCommand = new DisconnectCommand();
		add(disconnectCommand.getRequestKeyName(), disconnectCommand);
		ICommand createSessionCommand = new CreateSessionCommand();
		add(createSessionCommand.getRequestKeyName(), createSessionCommand);
		ICommand deleteSessionCommand = new DeleteSessionCommand();
		add(deleteSessionCommand.getRequestKeyName(), deleteSessionCommand);
		ICommand registerServiceCommand = new RegisterServiceCommand();
		add(registerServiceCommand.getRequestKeyName(), registerServiceCommand);
		ICommand deRegisterServiceCommand = new DeRegisterServiceCommand();
		add(deRegisterServiceCommand.getRequestKeyName(), deRegisterServiceCommand);
	}
}
