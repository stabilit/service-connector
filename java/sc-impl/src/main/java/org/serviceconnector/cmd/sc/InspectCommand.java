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

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.ICommandValidator;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.registry.Registry;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.sc.service.Service;
import org.serviceconnector.sc.service.ServiceState;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;


/**
 * The Class InspectCommand. Responsible for validation and execution of inspect command. Inspect command is used for
 * testing/maintaining reasons. Returns dumps of internal stuff to requester.
 * 
 * @author JTraber
 */
public class InspectCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(InspectCommand.class);

	/**
	 * Instantiates a new InspectCommand.
	 */
	public InspectCommand() {
		this.commandValidator = new InspectCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.INSPECT;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();

		SCMPMessage reqMsg = request.getMessage();
		String bodyString = (String) reqMsg.getBody();
		
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		InetAddress localHost = InetAddress.getLocalHost();
		scmpReply.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		scmpReply.setMessageType(getKey());

		if (bodyString == null) {
			String inspectString = "serviceRegistry&" + this.getRegistryInspectString(serviceRegistry);
			inspectString += "sessionRegistry&" + this.getRegistryInspectString(sessionRegistry);
			inspectString += "serverRegistry&" + this.getRegistryInspectString(serverRegistry);

			// dump internal registries
			scmpReply.setBody(inspectString);
			response.setSCMP(scmpReply);
			return;
		}

		if (bodyString.startsWith(Constants.STATE)) {
			// state for service requested
			String serviceName = bodyString.substring(6);
			logger.debug("state request for service:" + serviceName);

			if (serviceRegistry.containsKey(serviceName)) {
				if (serviceRegistry.getService(serviceName).getState() == ServiceState.ENABLED) {
					scmpReply.setBody(ServiceState.ENABLED.toString());
					logger.debug("service:" + serviceName + "is enabled");
				} else if (serviceRegistry.getService(serviceName).getState() == ServiceState.DISABLED) {
					scmpReply.setBody(ServiceState.DISABLED.toString());
					logger.debug("service:" + serviceName + "is disabled");
				} else {
					scmpReply.setBody("?");
					logger.debug("service:" + serviceName + "is state unknown");
				}
			} else {
				logger.debug("service:" + serviceName+" not found");
				scmpReply = new SCMPFault(SCMPError.NOT_FOUND, "service:" + serviceName+" not found");
			}
			response.setSCMP(scmpReply);
			return;
		}

		if (bodyString.startsWith(Constants.SESSIONS)) {
			// state for service requested
			String serviceName = bodyString.substring(9);
			logger.debug("sessions request for service:" + serviceName);
			if (serviceRegistry.containsKey(serviceName)) {
				Service service = serviceRegistry.getService(serviceName);
				scmpReply.setBody(service.getCountAvailableSessions() + "/" + service.getCountAllocatedSessions());
			}
			response.setSCMP(scmpReply);
			return;
		}
	}

	/**
	 * Gets the registry inspect string.
	 * 
	 * @param registry
	 *            the registry
	 * @return the registry inspect string
	 */
	private String getRegistryInspectString(Registry<?, ?> registry) {
		String string = registry.toString();
		if (registry.getSize() == 0) {
			string += "@";
		}
		return string;
	}

	/**
	 * The Class InspectCommandValidator.
	 */
	private class InspectCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
			// no validation necessary in case of inspect command
		}
	}
}
