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
package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cln.msg.impl.InspectMessage;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.registry.ConnectionRegistry;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.SCMPReply;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

/**
 * The Class InspectCommand. Responsible for validation and execution of inspect command. Inspect command is used
 * for testing/maintaining reasons. Returns dumps of internal stuff to requester.
 * 
 * @author JTraber
 */
public class InspectCommand extends CommandAdapter {

	/**
	 * Instantiates a new InspectCommand.
	 */
	public InspectCommand() {
		this.commandValidator = new InspectCommandValidator();
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.INSPECT;
	}

	/**
	 * Gets the command validator.
	 * 
	 * @return the command validator
	 */
	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	/**
	 * Run command.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		ConnectionRegistry connectionRegistry = ConnectionRegistry.getCurrentInstance();
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setLocalDateTime();
		InspectMessage inspectMsg = new InspectMessage();

		// dump internal registries
		inspectMsg.setAttribute("connectionRegistry", connectionRegistry);
		inspectMsg.setAttribute("serviceRegistry", serviceRegistry);
		inspectMsg.setAttribute("sessionRegistry", sessionRegistry);
		scmpReply.setBody(inspectMsg);
		response.setSCMP(scmpReply);
	}

	/**
	 * New instance.
	 * 
	 * @return the factoryable
	 */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/**
	 * The Class InspectCommandValidator.
	 */
	public class InspectCommandValidator implements ICommandValidator {

		/**
		 * Validate request, nothing to validate in case of inspect.
		 * 
		 * @param request
		 *            the request
		 * @throws SCMPValidatorException
		 *             the SCMP validator exception
		 */
		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
		}
	}
}
