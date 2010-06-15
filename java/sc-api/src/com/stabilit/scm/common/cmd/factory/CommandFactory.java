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
package com.stabilit.scm.common.cmd.factory;

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.factory.Factory;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * A factory for creating Command objects.
 */
public class CommandFactory extends Factory {

	/** The command factory. */
	protected static CommandFactory commandFactory = null;

	/**
	 * Instantiates a new command factory.
	 */
	public CommandFactory() {
	}

	/**
	 * Gets the single instance of CommandFactory.
	 * 
	 * @return single instance of CommandFactory
	 */
	@Override
	public IFactoryable getInstance() {
		throw new UnsupportedOperationException();
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
	 * New command.
	 * 
	 * @param request
	 *            the request
	 * @return the command
	 * @throws Exception
	 *             the exception
	 */
	public ICommand newCommand(IRequest request) throws Exception {
		SCMPMsgType key = request.getKey();
		IFactoryable factoryInstance = this.newInstance(key.getName());
		return (ICommand) factoryInstance;
	}
}
