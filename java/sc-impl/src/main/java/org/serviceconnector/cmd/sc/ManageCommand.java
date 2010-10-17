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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.ServiceState;
import org.serviceconnector.util.ValidatorUtility;


/**
 * The Class ManageCommand. Responsible for validation and execution of manage command. Manage command is used to
 * enable/disable services.
 * 
 * @author JTraber
 */
public class ManageCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ManageCommand.class);

	/** The Constant MANAGE_REGEX_STRING. */
	private static final String MANAGE_REGEX_STRING = "(" + Constants.ENABLE + "|" + Constants.DISABLE + ")=(.*)";
	/** The Constant MANAGE_PATTER. */
	private static final Pattern MANAGE_PATTERN = Pattern.compile(MANAGE_REGEX_STRING, Pattern.CASE_INSENSITIVE);

	/**
	 * Instantiates a new manage command.
	 */
	public ManageCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.MANAGE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage reqMsg = request.getMessage();
		String bodyString = (String) reqMsg.getBody();
		String ipAddress = (String) reqMsg.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		
		// set up response
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey());
		InetAddress localHost = InetAddress.getLocalHost();
		scmpReply.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, localHost.getHostAddress());

		if ((ipAddress.equals(localHost.getHostAddress())) && (bodyString.equalsIgnoreCase(Constants.KILL))) {
			// kill request is allowed from localhost only!
			logger.warn("Kill request received, SC exiting ...");
			System.exit(0);
		}

		Matcher m = MANAGE_PATTERN.matcher(bodyString);
		if (!m.matches()) {
			logger.error("wrong body syntax:" + bodyString); 		// body has bad syntax
			scmpReply = new SCMPFault(SCMPError.NOT_FOUND, "wrong body syntax");
			response.setSCMP(scmpReply);
			return;
		}

		String stateString = m.group(1);
		String serviceName = m.group(2);

		if (this.serviceRegistry.containsKey(serviceName)) {
			// service exists
			if (stateString.equalsIgnoreCase(Constants.ENABLE)) {
				// enable service
				logger.info("enable service:" + serviceName);
				this.serviceRegistry.getService(serviceName).setState(ServiceState.ENABLED);
			} else {
				// disable service
				logger.info("disable service:" + serviceName);
				this.serviceRegistry.getService(serviceName).setState(ServiceState.DISABLED);
			}
		} else {
			logger.debug("service:" + serviceName + " not found");
			scmpReply = new SCMPFault(SCMPError.NOT_FOUND, "service:" + serviceName + " not found");
		}
		response.setSCMP(scmpReply);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		try {
			SCMPMessage message = request.getMessage();
			// ipAddressList
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
}
