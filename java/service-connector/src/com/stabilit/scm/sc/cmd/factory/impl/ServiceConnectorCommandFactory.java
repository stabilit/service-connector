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
package com.stabilit.scm.sc.cmd.factory.impl;

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.sc.cmd.impl.AttachCommand;
import com.stabilit.scm.sc.cmd.impl.ClnCreateSessionCommand;
import com.stabilit.scm.sc.cmd.impl.ClnDataCommand;
import com.stabilit.scm.sc.cmd.impl.ClnDeleteSessionCommand;
import com.stabilit.scm.sc.cmd.impl.ClnEchoCommand;
import com.stabilit.scm.sc.cmd.impl.ClnSystemCommand;
import com.stabilit.scm.sc.cmd.impl.DeRegisterServiceCommand;
import com.stabilit.scm.sc.cmd.impl.DetachCommand;
import com.stabilit.scm.sc.cmd.impl.EchoSCCommand;
import com.stabilit.scm.sc.cmd.impl.InspectCommand;
import com.stabilit.scm.sc.cmd.impl.RegisterServiceCommand;

/**
 * A factory for creating ServiceConnectorCommand objects. Provides access to concrete instances of Service Connector
 * commands.
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
	 *            the command factory TODO (TRN) (Open JOT)list is not complete. Why?
	 */
	public void init(CommandFactory commandFactory) {
		ICommand attachCommand = new AttachCommand();
		commandFactory.addCommand(attachCommand.getKey(), attachCommand);
		ICommand detachCommand = new DetachCommand();
		commandFactory.addCommand(detachCommand.getKey(), detachCommand);
		ICommand inspectCommand = new InspectCommand();
		commandFactory.addCommand(inspectCommand.getKey(), inspectCommand);
		ICommand echoSCCommand = new EchoSCCommand();
		commandFactory.addCommand(echoSCCommand.getKey(), echoSCCommand);
		ICommand clnCreateSessionCommand = new ClnCreateSessionCommand();
		commandFactory.addCommand(clnCreateSessionCommand.getKey(), clnCreateSessionCommand);
		ICommand clnDeleteSessionCommand = new ClnDeleteSessionCommand();
		commandFactory.addCommand(clnDeleteSessionCommand.getKey(), clnDeleteSessionCommand);
		ICommand registerServiceCommand = new RegisterServiceCommand();
		commandFactory.addCommand(registerServiceCommand.getKey(), registerServiceCommand);
		ICommand deRegisterServiceCommand = new DeRegisterServiceCommand();
		commandFactory.addCommand(deRegisterServiceCommand.getKey(), deRegisterServiceCommand);
		ICommand clnEchoCommand = new ClnEchoCommand();
		commandFactory.addCommand(clnEchoCommand.getKey(), clnEchoCommand);
		ICommand clnDataCommand = new ClnDataCommand();
		commandFactory.addCommand(clnDataCommand.getKey(), clnDataCommand);
		ICommand clnSystemCommand = new ClnSystemCommand();
		commandFactory.addCommand(clnSystemCommand.getKey(), clnSystemCommand);
	}
}
