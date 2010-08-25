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
package com.stabilit.scm.srv.ps.cmd.factory.impl;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.srv.cmd.impl.SrvAbortSessionCommand;
import com.stabilit.scm.srv.ps.cmd.impl.SrvChangeSubscriptionCommand;
import com.stabilit.scm.srv.ps.cmd.impl.SrvSubscribeCommand;
import com.stabilit.scm.srv.ps.cmd.impl.SrvUnsubscribeCommand;

/**
 * A factory for creating PublishServerCommand objects. Unifies commands used by publish services.
 * 
 * @author JTraber
 */
public class PublishServerCommandFactory extends CommandFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishServerCommandFactory.class);
	
	/**
	 * Instantiates a new publish server command factory.
	 */
	public PublishServerCommandFactory() {
		init(this);
	}

	/**
	 * Instantiates a new publish server command factory.
	 * 
	 * @param commandFactory
	 *            the command factory
	 */
	public PublishServerCommandFactory(CommandFactory commandFactory) {
		init(commandFactory);
	}

	/**
	 * Initialize factory.
	 * 
	 * @param commandFactory
	 *            the command factory
	 */
	public void init(CommandFactory commandFactory) {
		ICommand srvSubscribeCommand = new SrvSubscribeCommand();
		commandFactory.addCommand(srvSubscribeCommand.getKey(), srvSubscribeCommand);
		ICommand srvUnsubscribeCommand = new SrvUnsubscribeCommand();
		commandFactory.addCommand(srvUnsubscribeCommand.getKey(), srvUnsubscribeCommand);
		ICommand srvChangeSubscriptionCommand = new SrvChangeSubscriptionCommand();
		commandFactory.addCommand(srvChangeSubscriptionCommand.getKey(), srvChangeSubscriptionCommand);
		ICommand srvAbortSessionCommand = new SrvAbortSessionCommand();
		commandFactory.addCommand(srvAbortSessionCommand.getKey(), srvAbortSessionCommand);
	}
}
