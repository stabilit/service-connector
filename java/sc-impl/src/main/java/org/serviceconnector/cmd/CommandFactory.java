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
package org.serviceconnector.cmd;

import org.apache.log4j.Logger;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * A factory for creating Command objects.
 */
public abstract class CommandFactory {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(CommandFactory.class);
	/** The app context. */
	protected AppContext appContext;

	/**
	 * Instantiates a new command factory.
	 */
	public CommandFactory() {
	}

	/**
	 * Initialize factory.
	 * 
	 * @param commandFactory
	 *            the command factory
	 */
	public abstract void initCommands(AppContext appContext);

	/**
	 * Adds the command.
	 * 
	 * @param messageType
	 *            the message type
	 * @param factoryInstance
	 *            the factory instance
	 */
	public void addCommand(SCMPMsgType messageType, ICommand command) {
		this.appContext.getCommands().put(messageType.getValue(), command);
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
