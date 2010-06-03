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
package com.stabilit.scm.cmd.factory.impl;

import com.stabilit.scm.cmd.impl.AttachCommand;
import com.stabilit.scm.cmd.impl.ClnCreateSessionCommand;
import com.stabilit.scm.cmd.impl.ClnDataCommand;
import com.stabilit.scm.cmd.impl.ClnDeleteSessionCommand;
import com.stabilit.scm.cmd.impl.ClnEchoCommand;
import com.stabilit.scm.cmd.impl.ClnSystemCommand;
import com.stabilit.scm.cmd.impl.DeRegisterServiceCommand;
import com.stabilit.scm.cmd.impl.DetachCommand;
import com.stabilit.scm.cmd.impl.EchoSCCommand;
import com.stabilit.scm.cmd.impl.InspectCommand;
import com.stabilit.scm.cmd.impl.RegisterServiceCommand;
import com.stabilit.scm.srv.cmd.ICommand;
import com.stabilit.scm.srv.cmd.factory.CommandFactory;

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
	 *            TODO (TRN) (Open JOT)list is not complete. Why?
	 */
	public void init(CommandFactory commandFactory) {
		ICommand attachCommand = new AttachCommand();
		commandFactory.add(attachCommand.getRequestKeyName(), attachCommand);
		ICommand detachCommand = new DetachCommand();
		commandFactory.add(detachCommand.getRequestKeyName(), detachCommand);
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
