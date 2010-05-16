/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package com.stabilit.sc.cmd.factory.impl;

import com.stabilit.sc.cmd.impl.ClnCreateSessionCommand;
import com.stabilit.sc.cmd.impl.ClnDataCommand;
import com.stabilit.sc.cmd.impl.ClnDeleteSessionCommand;
import com.stabilit.sc.cmd.impl.ClnEchoCommand;
import com.stabilit.sc.cmd.impl.ClnSystemCommand;
import com.stabilit.sc.cmd.impl.ConnectCommand;
import com.stabilit.sc.cmd.impl.DeRegisterServiceCommand;
import com.stabilit.sc.cmd.impl.DisconnectCommand;
import com.stabilit.sc.cmd.impl.EchoSCCommand;
import com.stabilit.sc.cmd.impl.InspectCommand;
import com.stabilit.sc.cmd.impl.RegisterServiceCommand;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;

/**
 * A factory for creating ServiceConnectorCommand objects. 
 * Provides access to concrete instances of Service
 * Connector commands.
 * 
 * @author JTraber
 */
public class ServiceConnectorCommandFactory extends CommandFactory {

	/**
	 * Instantiates a new service connector command factory.
	 */
	public ServiceConnectorCommandFactory() {
		init(this);
	}

	/**
	 * Instantiates a new service connector command factory.
	 * 
	 * @param commandFactory
	 *            the command factory
	 */
	public ServiceConnectorCommandFactory(CommandFactory commandFactory) {
		init(commandFactory);
	}

	/**
	 * Initialize the command factory.
	 * 
	 * @param commandFactory
	 *            the command factory
	 *            TODO (TRN) list is not complete. Why?
	 */
	public void init(CommandFactory commandFactory) {
		ICommand connectCommand = new ConnectCommand();
		commandFactory.add(connectCommand.getRequestKeyName(), connectCommand);
		ICommand disconnectCommand = new DisconnectCommand();
		commandFactory.add(disconnectCommand.getRequestKeyName(), disconnectCommand);
		ICommand inspectCommand = new InspectCommand();
		commandFactory.add(inspectCommand.getRequestKeyName(), inspectCommand);
		ICommand echoSCCommand = new EchoSCCommand();
		commandFactory.add(echoSCCommand.getRequestKeyName(), echoSCCommand);
		ICommand clnCreateSessionCommand = new ClnCreateSessionCommand();
		commandFactory.add(clnCreateSessionCommand.getRequestKeyName(), clnCreateSessionCommand);
		ICommand clnDeleteSessionCommand = new ClnDeleteSessionCommand();
		commandFactory.add(clnDeleteSessionCommand.getRequestKeyName(), clnDeleteSessionCommand);
		ICommand registerServiceCommand = new RegisterServiceCommand();
		commandFactory.add(registerServiceCommand.getRequestKeyName(), registerServiceCommand);
		ICommand deRegisterServiceCommand = new DeRegisterServiceCommand();
		commandFactory.add(deRegisterServiceCommand.getRequestKeyName(), deRegisterServiceCommand);
		ICommand clnEchoCommand = new ClnEchoCommand();
		commandFactory.add(clnEchoCommand.getRequestKeyName(), clnEchoCommand);
		ICommand clnDataCommand = new ClnDataCommand();
		commandFactory.add(clnDataCommand.getRequestKeyName(), clnDataCommand);
		ICommand clnSystemCommand = new ClnSystemCommand();
		commandFactory.add(clnSystemCommand.getRequestKeyName(), clnSystemCommand);
	}
}
