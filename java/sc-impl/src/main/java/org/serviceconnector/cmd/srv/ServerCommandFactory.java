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
package org.serviceconnector.cmd.srv;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.CommandFactory;
import org.serviceconnector.cmd.ICommand;


/**
 * A factory for creating UnitServerCommand objects. Unifies all commands used by publish and session server.
 * 
 * @author JTraber
 */
public class ServerCommandFactory extends CommandFactory {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServerCommandFactory.class);
	
	/**
	 * Instantiates a new session server command factory.
	 */
	public ServerCommandFactory() {
		init(this);
	}

	/**
	 * Instantiates a new session server command factory.
	 * 
	 * @param commandFactory
	 *            the command factory
	 */
	public ServerCommandFactory(CommandFactory commandFactory) {
		init(commandFactory);
	}

	/**
	 * Initialize factory.
	 * 
	 * @param commandFactory
	 *            the command factory
	 */
	public void init(CommandFactory commandFactory) {
		ICommand srvCreateSessionCommand = new SrvCreateSessionCommand();
		commandFactory.addCommand(srvCreateSessionCommand.getKey(), srvCreateSessionCommand);
		ICommand srvDeleteSessionCommand = new SrvDeleteSessionCommand();
		commandFactory.addCommand(srvDeleteSessionCommand.getKey(), srvDeleteSessionCommand);
		ICommand srvExecuteCommand = new SrvExecuteCommand();
		commandFactory.addCommand(srvExecuteCommand.getKey(), srvExecuteCommand);
		ICommand srvAbortSessionCommand = new SrvAbortSessionCommand();
		commandFactory.addCommand(srvAbortSessionCommand.getKey(), srvAbortSessionCommand);
		
		ICommand srvSubscribeCommand = new SrvSubscribeCommand();
		commandFactory.addCommand(srvSubscribeCommand.getKey(), srvSubscribeCommand);
		ICommand srvUnsubscribeCommand = new SrvUnsubscribeCommand();
		commandFactory.addCommand(srvUnsubscribeCommand.getKey(), srvUnsubscribeCommand);
		ICommand srvChangeSubscriptionCommand = new SrvChangeSubscriptionCommand();
		commandFactory.addCommand(srvChangeSubscriptionCommand.getKey(), srvChangeSubscriptionCommand);
	}
}
