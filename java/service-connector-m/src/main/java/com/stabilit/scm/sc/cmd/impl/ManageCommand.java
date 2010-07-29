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

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.registry.DisabledServiceRegistry;
import com.stabilit.scm.sc.registry.ServiceRegistry;
import com.stabilit.scm.sc.service.Service;

/**
 * The Class ManageCommand. Responsible for validation and execution of manage command. Manage command is used to
 * enable/disable services.
 * 
 * @author JTraber
 */
public class ManageCommand extends CommandAdapter {

	/**
	 * Instantiates a new InspectCommand.
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

		// set up response
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey());
		response.setSCMP(scmpReply);

		SCMPMessage reqMsg = request.getMessage();
		String serviceName = (String) reqMsg.getBody();

		if (disabledServiceRegistry.containsKey(serviceName)) {
			Service service = disabledServiceRegistry.removeService(serviceName);
			serviceRegistry.addService(serviceName, service);
			return;
		}

		if (serviceRegistry.containsKey(serviceName)) {
			// TODO verify with jan .. how to disable service
			Service service = serviceRegistry.removeService(serviceName);
			disabledServiceRegistry.addService(serviceName, service);
		}
	}

	/**
	 * The Class InspectCommandValidator.
	 */
	private class ManageCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
			// no validation necessary in case of manage command
		}
	}
}
