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
package org.serviceconnector.test.sc;

import org.serviceconnector.cmd.CommandFactory;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.cmd.sc.AttachCommand;
import org.serviceconnector.cmd.sc.ClnChangeSubscriptionCommand;
import org.serviceconnector.cmd.sc.ClnCreateSessionCommand;
import org.serviceconnector.cmd.sc.ClnDeleteSessionCommand;
import org.serviceconnector.cmd.sc.ClnExecuteCommand;
import org.serviceconnector.cmd.sc.ClnSubscribeCommand;
import org.serviceconnector.cmd.sc.ClnUnsubscribeCommand;
import org.serviceconnector.cmd.sc.DeRegisterServerCommand;
import org.serviceconnector.cmd.sc.DetachCommand;
import org.serviceconnector.cmd.sc.EchoCommand;
import org.serviceconnector.cmd.sc.InspectCommand;
import org.serviceconnector.cmd.sc.ManageCommand;
import org.serviceconnector.cmd.sc.PublishCommand;
import org.serviceconnector.cmd.sc.ReceivePublicationCommand;
import org.serviceconnector.cmd.sc.RegisterServerCommand;
import org.serviceconnector.cmd.srv.SrvAbortSessionCommand;
import org.serviceconnector.cmd.srv.SrvChangeSubscriptionCommand;
import org.serviceconnector.cmd.srv.SrvCreateSessionCommand;
import org.serviceconnector.cmd.srv.SrvDeleteSessionCommand;
import org.serviceconnector.cmd.srv.SrvExecuteCommand;
import org.serviceconnector.cmd.srv.SrvSubscribeCommand;
import org.serviceconnector.cmd.srv.SrvUnsubscribeCommand;
import org.serviceconnector.ctx.AppContext;

/**
 * A factory for creating command objects. Unifies all commands used by publish, session server and service connector.
 * 
 * @author JTraber
 */
public class TestServerCommandFactory extends CommandFactory {

	/** @{inheritDoc **/
	@Override
	public void initCommands(AppContext appContext) {
		this.appContext = appContext;
		ICommand srvCreateSessionCommand = new SrvCreateSessionCommand();
		this.addCommand(srvCreateSessionCommand.getKey(), srvCreateSessionCommand);
		ICommand srvDeleteSessionCommand = new SrvDeleteSessionCommand();
		this.addCommand(srvDeleteSessionCommand.getKey(), srvDeleteSessionCommand);
		ICommand srvExecuteCommand = new SrvExecuteCommand();
		this.addCommand(srvExecuteCommand.getKey(), srvExecuteCommand);
		ICommand srvAbortSessionCommand = new SrvAbortSessionCommand();
		this.addCommand(srvAbortSessionCommand.getKey(), srvAbortSessionCommand);

		ICommand srvSubscribeCommand = new SrvSubscribeCommand();
		this.addCommand(srvSubscribeCommand.getKey(), srvSubscribeCommand);
		ICommand srvUnsubscribeCommand = new SrvUnsubscribeCommand();
		this.addCommand(srvUnsubscribeCommand.getKey(), srvUnsubscribeCommand);
		ICommand srvChangeSubscriptionCommand = new SrvChangeSubscriptionCommand();
		this.addCommand(srvChangeSubscriptionCommand.getKey(), srvChangeSubscriptionCommand);

		ICommand attachCommand = new AttachCommand();
		this.addCommand(attachCommand.getKey(), attachCommand);
		ICommand detachCommand = new DetachCommand();
		this.addCommand(detachCommand.getKey(), detachCommand);
		ICommand inspectCommand = new InspectCommand();
		this.addCommand(inspectCommand.getKey(), inspectCommand);
		ICommand manageCommand = new ManageCommand();
		this.addCommand(manageCommand.getKey(), manageCommand);
		ICommand clnCreateSessionCommand = new ClnCreateSessionCommand();
		this.addCommand(clnCreateSessionCommand.getKey(), clnCreateSessionCommand);
		ICommand clnDeleteSessionCommand = new ClnDeleteSessionCommand();
		this.addCommand(clnDeleteSessionCommand.getKey(), clnDeleteSessionCommand);
		ICommand registerServerCommand = new RegisterServerCommand();
		this.addCommand(registerServerCommand.getKey(), registerServerCommand);
		ICommand deRegisterServerCommand = new DeRegisterServerCommand();
		this.addCommand(deRegisterServerCommand.getKey(), deRegisterServerCommand);
		ICommand clnEchoCommand = new EchoCommand();
		this.addCommand(clnEchoCommand.getKey(), clnEchoCommand);
		ICommand clnExecuteCommand = new ClnExecuteCommand();
		this.addCommand(clnExecuteCommand.getKey(), clnExecuteCommand);
		// publish subscribe commands
		ICommand clnSubscribeCommand = new ClnSubscribeCommand();
		this.addCommand(clnSubscribeCommand.getKey(), clnSubscribeCommand);
		ICommand clnUnsubscribeCommand = new ClnUnsubscribeCommand();
		this.addCommand(clnUnsubscribeCommand.getKey(), clnUnsubscribeCommand);
		ICommand clnChangeSubscriptionCommand = new ClnChangeSubscriptionCommand();
		this.addCommand(clnChangeSubscriptionCommand.getKey(), clnChangeSubscriptionCommand);
		ICommand receivePublicationCommand = new ReceivePublicationCommand();
		this.addCommand(receivePublicationCommand.getKey(), receivePublicationCommand);
		ICommand publishCommand = new PublishCommand();
		this.addCommand(publishCommand.getKey(), publishCommand);
	}
}
