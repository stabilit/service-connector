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
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.registry.Registry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.ServiceState;
import org.serviceconnector.service.ServiceType;
import org.serviceconnector.service.StatefulService;
import org.serviceconnector.util.ValidatorUtility;

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
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.INSPECT;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage reqMsg = request.getMessage();
		String bodyString = (String) reqMsg.getBody();

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		InetAddress localHost = InetAddress.getLocalHost();
		scmpReply.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());
		scmpReply.setMessageType(getKey());

		if (bodyString == null) {
			String inspectString = "serviceRegistry&" + this.getRegistryInspectString(this.serviceRegistry);
			inspectString += "sessionRegistry&" + this.getRegistryInspectString(this.sessionRegistry);
			inspectString += "serverRegistry&" + this.getRegistryInspectString(this.serverRegistry);

			// dump internal registries
			scmpReply.setBody(inspectString);
			response.setSCMP(scmpReply);
			return;
		}

		if (bodyString.startsWith(Constants.STATE)) {
			// state for service requested
			String serviceName = bodyString.substring(6);
			logger.debug("state request for service:" + serviceName);

			if (this.serviceRegistry.containsKey(serviceName)) {
				if (this.serviceRegistry.getService(serviceName).getState() == ServiceState.ENABLED) {
					scmpReply.setBody(ServiceState.ENABLED.toString());
					logger.debug("service:" + serviceName + "is enabled");
				} else if (this.serviceRegistry.getService(serviceName).getState() == ServiceState.DISABLED) {
					scmpReply.setBody(ServiceState.DISABLED.toString());
					logger.debug("service:" + serviceName + "is disabled");
				} else {
					scmpReply.setBody("?");
					logger.debug("service:" + serviceName + "is state unknown");
				}
			} else {
				logger.debug("service:" + serviceName + " not found");
				scmpReply = new SCMPMessageFault(SCMPError.NOT_FOUND, "service=" + serviceName + " not found");
			}
			response.setSCMP(scmpReply);
			return;
		}

		if (bodyString.startsWith(Constants.SESSIONS)) {
			// state for service requested
			String serviceName = bodyString.substring(9);
			logger.debug("sessions request for service:" + serviceName);
			Service service = this.getService(serviceName);
			if (service.getType() != ServiceType.PUBLISH_SERVICE && service.getType() != ServiceType.SESSION_SERVICE) {
				// no service known with incoming serviceName
				SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND, "service=" + serviceName
						+ " is not known service");
				scmpCommandException.setMessageType(getKey());
				throw scmpCommandException;
			}
			StatefulService statefulService = (StatefulService) service;
			scmpReply.setBody(statefulService.getCountAvailableSessions() + "/" + statefulService.getCountAllocatedSessions());
			response.setSCMP(scmpReply);
			return;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		try {
			SCMPMessage message = request.getMessage();
			// ipAddressList mandatory
			String ipAddressList = (String) message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getValue());
			ValidatorUtility.validateIpAddressList(ipAddressList);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			logger.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
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
}