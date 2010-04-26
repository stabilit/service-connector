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
package com.stabilit.sc.srv.cmd.factory;

import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.scmp.IRequest;
import com.stabilit.sc.common.scmp.SCMPMsgType;
import com.stabilit.sc.srv.cmd.ICommand;

public class CommandFactory extends Factory {

	protected static CommandFactory commandFactory = null;

	public CommandFactory() {
	}

	public static CommandFactory getCurrentCommandFactory() {
		return commandFactory;
	}

	public static void setCurrentCommandFactory(CommandFactory commandFactory) {
		CommandFactory.commandFactory = commandFactory;
	}

	public void init(CommandFactory commandFactory) {

	}

	public ICommand newCommand(IRequest request) throws Exception {
		SCMPMsgType key = request.getKey();
		IFactoryable factoryInstance = this.newInstance(key.getRequestName());
		return (ICommand) factoryInstance;
	}

}
