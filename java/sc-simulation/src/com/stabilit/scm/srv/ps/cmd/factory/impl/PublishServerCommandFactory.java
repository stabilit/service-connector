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

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.srv.rr.cmd.impl.SrvChangeSubscriptionCommand;
import com.stabilit.scm.srv.rr.cmd.impl.SrvEchoCommand;
import com.stabilit.scm.srv.rr.cmd.impl.SrvSubscribeCommand;
import com.stabilit.scm.srv.rr.cmd.impl.SrvSystemCommand;
import com.stabilit.scm.srv.rr.cmd.impl.SrvUnsubscribeCommand;

public class PublishServerCommandFactory extends CommandFactory {

	public PublishServerCommandFactory() {
		init(this);
	}

	public PublishServerCommandFactory(CommandFactory commandFactory) {
		init(commandFactory);
	}

	public void init(CommandFactory commandFactory) {
		ICommand srvEchoCommand = new SrvEchoCommand();
		commandFactory.addCommand(srvEchoCommand.getKey(), srvEchoCommand);
		ICommand srvSystemCommand = new SrvSystemCommand();
		commandFactory.addCommand(srvSystemCommand.getKey(), srvSystemCommand);
		// publish subscribe commands
		ICommand srvSubscribeCommand = new SrvSubscribeCommand();
		commandFactory.addCommand(srvSubscribeCommand.getKey(), srvSubscribeCommand);
		ICommand srvUnsubscribeCommand = new SrvUnsubscribeCommand();
		commandFactory.addCommand(srvUnsubscribeCommand.getKey(), srvUnsubscribeCommand);
		ICommand srvChangeSubscriptionCommand = new SrvChangeSubscriptionCommand();
		commandFactory.addCommand(srvChangeSubscriptionCommand.getKey(), srvChangeSubscriptionCommand);
	}
}
