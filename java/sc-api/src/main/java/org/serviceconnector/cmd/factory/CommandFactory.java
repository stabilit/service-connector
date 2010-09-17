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
package org.serviceconnector.cmd.factory;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.factory.Factory;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.SCMPMsgType;


/**
 * A factory for creating Command objects.
 */
public abstract class CommandFactory extends Factory {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(CommandFactory.class);
	
	/** The command factory. */
	protected static CommandFactory commandFactory = null;

	/**
	 * Instantiates a new command factory.
	 */
	public CommandFactory() {
	}

	/**
	 * Gets the current command factory.
	 * 
	 * @return the current command factory
	 */
	public static CommandFactory getCurrentCommandFactory() {
		return commandFactory;
	}

	/**
	 * Sets the current command factory.
	 * 
	 * @param commandFactory
	 *            the new current command factory
	 */
	public static void setCurrentCommandFactory(CommandFactory commandFactory) {
		CommandFactory.commandFactory = commandFactory;
	}

	/**
	 * Adds the command.
	 * 
	 * @param messageType
	 *            the message type
	 * @param factoryInstance
	 *            the factory instance
	 */
	public void addCommand(SCMPMsgType messageType, IFactoryable factoryInstance) {
		super.add(messageType.getValue(), factoryInstance);
	}

	/**
	 * Get command.
	 * 
	 * @param request
	 *            the request
	 * @return the command
	 * @throws Exception
	 *             the exception
	 */
	public ICommand getCommand(IRequest request) throws Exception {
		SCMPMsgType key = request.getKey();
		return this.getCommand(key);
	}

	/**
	 * Get command.
	 * 
	 * @param key
	 *            the key
	 * @return the command
	 * @throws Exception
	 *             the exception
	 */
	public ICommand getCommand(SCMPMsgType key) {
		IFactoryable factoryInstance = this.newInstance(key.getValue());
		return (ICommand) factoryInstance;
	}
}
