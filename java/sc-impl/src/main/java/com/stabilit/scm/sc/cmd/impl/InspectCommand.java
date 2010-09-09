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
package com.stabilit.scm.sc.cmd.impl;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.registry.Registry;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.registry.DisabledServiceRegistry;
import com.stabilit.scm.sc.registry.ServerRegistry;
import com.stabilit.scm.sc.registry.ServiceRegistry;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.service.Service;

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
			logger.debug("state requested for service : " + serviceName);
			if (serviceRegistry.containsKey(serviceName)) {
				scmpReply.setBody(Boolean.TRUE.toString());
				logger.debug("state true for service : " + serviceName);
			} else if (DisabledServiceRegistry.getCurrentInstance().containsKey(serviceName)) {
				scmpReply.setBody(Boolean.FALSE.toString());
				logger.debug("state false for service : " + serviceName);
			} else {
				scmpReply = new SCMPFault(SCMPError.NOT_FOUND, "serviceName :" + serviceName);
				logger.debug("not found for service : " + serviceName);
			}
			response.setSCMP(scmpReply);
			return;
		}

		if (bodyString.startsWith(Constants.SESSIONS)) {
			// state for service requested
			String serviceName = bodyString.substring(9);
			logger.debug("sessions requested for service : " + serviceName);
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
