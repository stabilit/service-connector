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
package org.serviceconnector.cmd.sc;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.FlyweightCommandFactory;
import org.serviceconnector.cmd.ICommand;

/**
 * A factory for creating ServiceConnectorCommand objects. Provides access to concrete instances of Service Connector commands.
 * 
 * @author JTraber
 */
public class ServiceConnectorCommandFactory extends FlyweightCommandFactory {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(ServiceConnectorCommandFactory.class);

	/**
	 * Instantiates a new service connector command factory.
	 */
	public ServiceConnectorCommandFactory() {
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
		ICommand cscCreateSessionCommand = new CscCreateSessionCommand();
		this.addCommand(cscCreateSessionCommand.getKey(), cscCreateSessionCommand);
		ICommand cscDeleteSessionCommand = new CscDeleteSessionCommand();
		this.addCommand(cscDeleteSessionCommand.getKey(), cscDeleteSessionCommand);
		ICommand registerServerCommand = new RegisterServerCommand();
		this.addCommand(registerServerCommand.getKey(), registerServerCommand);
		ICommand checkRegistrationCommand = new CheckRegistrationCommand();
		this.addCommand(checkRegistrationCommand.getKey(), checkRegistrationCommand);
		ICommand deRegisterServerCommand = new DeRegisterServerCommand();
		this.addCommand(deRegisterServerCommand.getKey(), deRegisterServerCommand);
		ICommand clnEchoCommand = new EchoCommand();
		this.addCommand(clnEchoCommand.getKey(), clnEchoCommand);
		ICommand clnExecuteCommand = new ClnExecuteCommand();
		this.addCommand(clnExecuteCommand.getKey(), clnExecuteCommand);
		ICommand cscExecuteCommand = new CscExecuteCommand();
		this.addCommand(cscExecuteCommand.getKey(), cscExecuteCommand);
		// publish subscribe commands
		ICommand clnSubscribeCommand = new ClnSubscribeCommand();
		this.addCommand(clnSubscribeCommand.getKey(), clnSubscribeCommand);
		ICommand clnUnsubscribeCommand = new ClnUnsubscribeCommand();
		this.addCommand(clnUnsubscribeCommand.getKey(), clnUnsubscribeCommand);
		ICommand clnChangeSubscriptionCommand = new ClnChangeSubscriptionCommand();
		this.addCommand(clnChangeSubscriptionCommand.getKey(), clnChangeSubscriptionCommand);
		ICommand cscSubscribeCommand = new CscSubscribeCommand();
		this.addCommand(cscSubscribeCommand.getKey(), cscSubscribeCommand);
		ICommand cscUnsubscribeCommand = new CscUnsubscribeCommand();
		this.addCommand(cscUnsubscribeCommand.getKey(), cscUnsubscribeCommand);
		ICommand cscChangeSubscriptionCommand = new CscChangeSubscriptionCommand();
		this.addCommand(cscChangeSubscriptionCommand.getKey(), cscChangeSubscriptionCommand);
		ICommand cscAbortSubscriptionCommand = new CscAbortSubscriptionCommand();
		this.addCommand(cscAbortSubscriptionCommand.getKey(), cscAbortSubscriptionCommand);
		ICommand receivePublicationCommand = new ReceivePublicationCommand();
		this.addCommand(receivePublicationCommand.getKey(), receivePublicationCommand);
		ICommand publishCommand = new PublishCommand();
		this.addCommand(publishCommand.getKey(), publishCommand);
		// file commands
		ICommand fileUploadCommand = new FileUploadCommand();
		this.addCommand(fileUploadCommand.getKey(), fileUploadCommand);
		ICommand fileDownloadCommand = new FileDownloadCommand();
		this.addCommand(fileDownloadCommand.getKey(), fileDownloadCommand);
		ICommand fileListCommand = new FileListCommand();
		this.addCommand(fileListCommand.getKey(), fileListCommand);
	}
}