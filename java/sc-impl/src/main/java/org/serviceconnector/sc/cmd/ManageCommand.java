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
package org.serviceconnector.sc.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.ICommandValidator;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.sc.registry.DisabledServiceRegistry;
import org.serviceconnector.sc.registry.ServiceRegistry;
import org.serviceconnector.sc.service.Service;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;


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
	private static final Pattern MANAGE_PATTER = Pattern.compile(MANAGE_REGEX_STRING, Pattern.CASE_INSENSITIVE);

	/**
	 * Instantiates a new manage command.
	 */
	public ManageCommand() {
		this.commandValidator = new ManageCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.MANAGE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		DisabledServiceRegistry disabledServiceRegistry = DisabledServiceRegistry.getCurrentInstance();

		// TODO not found error message JOT

		// set up response
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey());
		response.setSCMP(scmpReply);

		SCMPMessage reqMsg = request.getMessage();
		String bodyString = (String) reqMsg.getBody();

		if (bodyString.equalsIgnoreCase(Constants.KILL)) {
			logger.info("SC exiting ...");
			// kill sc requested
			System.exit(0);
		}

		Matcher m = MANAGE_PATTER.matcher(bodyString);
		if (!m.matches()) {
			// given string has bad format
			return;
		}

		String stateString = m.group(1);
		String serviceName = m.group(2);

		if (stateString.equalsIgnoreCase(Constants.ENABLE)) {
			// enable service
			if (disabledServiceRegistry.containsKey(serviceName)) {
				logger.info("enable service: " + serviceName);
				Service service = disabledServiceRegistry.removeService(serviceName);
				serviceRegistry.addService(serviceName, service);
			}
			return;
		}
		if (serviceRegistry.containsKey(serviceName)) {
			// disable service
			logger.info("disable service: " + serviceName);
			Service service = serviceRegistry.removeService(serviceName);
			disabledServiceRegistry.addService(serviceName, service);
		}
	}

	/**
	 * The Class ManageCommandValidator.
	 */
	private class ManageCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
			// no validation necessary in case of manage command
		}
	}
}
