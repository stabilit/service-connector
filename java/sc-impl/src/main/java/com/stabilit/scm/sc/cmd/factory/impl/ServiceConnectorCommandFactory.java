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

import org.apache.log4j.Logger;

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.sc.cmd.impl.AttachCommand;
import com.stabilit.scm.sc.cmd.impl.ClnChangeSubscriptionCommand;
import com.stabilit.scm.sc.cmd.impl.ClnCreateSessionCommand;
import com.stabilit.scm.sc.cmd.impl.ClnDeleteSessionCommand;
import com.stabilit.scm.sc.cmd.impl.EchoCommand;
import com.stabilit.scm.sc.cmd.impl.ClnExecuteCommand;
import com.stabilit.scm.sc.cmd.impl.ClnSubscribeCommand;
import com.stabilit.scm.sc.cmd.impl.ClnUnsubscribeCommand;
import com.stabilit.scm.sc.cmd.impl.DeRegisterServiceCommand;
import com.stabilit.scm.sc.cmd.impl.DetachCommand;
import com.stabilit.scm.sc.cmd.impl.InspectCommand;
import com.stabilit.scm.sc.cmd.impl.ManageCommand;
import com.stabilit.scm.sc.cmd.impl.PublishCommand;
import com.stabilit.scm.sc.cmd.impl.ReceivePublicationCommand;
import com.stabilit.scm.sc.cmd.impl.RegisterServiceCommand;

/**
 * A factory for creating ServiceConnectorCommand objects. Provides access to concrete instances of Service Connector
 * commands.
 * 
 * @author JTraber
 */
public class ServiceConnectorCommandFactory extends CommandFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServiceConnectorCommandFactory.class);
	
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
	 */
	public void init(CommandFactory commandFactory) {
		ICommand attachCommand = new AttachCommand();
		commandFactory.addCommand(attachCommand.getKey(), attachCommand);
		ICommand detachCommand = new DetachCommand();
		commandFactory.addCommand(detachCommand.getKey(), detachCommand);
		ICommand inspectCommand = new InspectCommand();
		commandFactory.addCommand(inspectCommand.getKey(), inspectCommand);
		ICommand manageCommand = new ManageCommand();
		commandFactory.addCommand(manageCommand.getKey(), manageCommand);
		ICommand clnCreateSessionCommand = new ClnCreateSessionCommand();
		commandFactory.addCommand(clnCreateSessionCommand.getKey(), clnCreateSessionCommand);
		ICommand clnDeleteSessionCommand = new ClnDeleteSessionCommand();
		commandFactory.addCommand(clnDeleteSessionCommand.getKey(), clnDeleteSessionCommand);
		ICommand registerServiceCommand = new RegisterServiceCommand();
		commandFactory.addCommand(registerServiceCommand.getKey(), registerServiceCommand);
		ICommand deRegisterServiceCommand = new DeRegisterServiceCommand();
		commandFactory.addCommand(deRegisterServiceCommand.getKey(), deRegisterServiceCommand);
		ICommand clnEchoCommand = new EchoCommand();
		commandFactory.addCommand(clnEchoCommand.getKey(), clnEchoCommand);
		ICommand clnExecuteCommand = new ClnExecuteCommand();
		commandFactory.addCommand(clnExecuteCommand.getKey(), clnExecuteCommand);
		// publish subscribe commands
		ICommand clnSubscribeCommand = new ClnSubscribeCommand();
		commandFactory.addCommand(clnSubscribeCommand.getKey(), clnSubscribeCommand);
		ICommand clnUnsubscribeCommand = new ClnUnsubscribeCommand();
		commandFactory.addCommand(clnUnsubscribeCommand.getKey(), clnUnsubscribeCommand);
		ICommand clnChangeSubscriptionCommand = new ClnChangeSubscriptionCommand();
		commandFactory.addCommand(clnChangeSubscriptionCommand.getKey(), clnChangeSubscriptionCommand);
		ICommand receivePublicationCommand = new ReceivePublicationCommand();
		commandFactory.addCommand(receivePublicationCommand.getKey(), receivePublicationCommand);
		ICommand publishCommand = new PublishCommand();
		commandFactory.addCommand(publishCommand.getKey(), publishCommand);
	}
}
