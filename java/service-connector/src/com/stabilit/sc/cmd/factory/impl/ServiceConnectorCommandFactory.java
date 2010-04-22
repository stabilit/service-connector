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

import com.stabilit.sc.cmd.impl.ClnDataCommand;
import com.stabilit.sc.cmd.impl.ConnectCommand;
import com.stabilit.sc.cmd.impl.CreateSessionCommand;
import com.stabilit.sc.cmd.impl.DeRegisterServiceCommand;
import com.stabilit.sc.cmd.impl.DeleteSessionCommand;
import com.stabilit.sc.cmd.impl.DisconnectCommand;
import com.stabilit.sc.cmd.impl.EchoSCCommand;
import com.stabilit.sc.cmd.impl.EchoSrvCommand;
import com.stabilit.sc.cmd.impl.InspectCommand;
import com.stabilit.sc.cmd.impl.RegisterServiceCommand;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;

public class ServiceConnectorCommandFactory extends CommandFactory {

	public ServiceConnectorCommandFactory() {
		init(this);
	}

	public ServiceConnectorCommandFactory(CommandFactory commandFactory) {
		init(commandFactory);
	}

	public void init(CommandFactory commandFactory) {
		ICommand echoSrvCommand = new EchoSrvCommand();
		commandFactory.add(echoSrvCommand.getRequestKeyName(), echoSrvCommand);
		ICommand echoSCCommand = new EchoSCCommand();
		commandFactory.add(echoSCCommand.getRequestKeyName(), echoSCCommand);
		ICommand inspectCommand = new InspectCommand();
		commandFactory.add(inspectCommand.getRequestKeyName(), inspectCommand);
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
		ICommand clnDataCommand = new ClnDataCommand();
		commandFactory.add(clnDataCommand.getRequestKeyName(), clnDataCommand);
	}
}
