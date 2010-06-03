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
package com.stabilit.scm.cmd.impl;

import com.stabilit.scm.cln.msg.impl.InspectMessage;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.registry.ClientRegistry;
import com.stabilit.scm.registry.ServiceRegistry;
import com.stabilit.scm.registry.SessionRegistry;
import com.stabilit.scm.scmp.IRequest;
import com.stabilit.scm.scmp.IResponse;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMsgType;
import com.stabilit.scm.srv.cmd.ICommandValidator;
import com.stabilit.scm.srv.cmd.SCMPValidatorException;
import com.stabilit.scm.util.DateTimeUtility;

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
		ClientRegistry clientRegistry = ClientRegistry.getCurrentInstance();
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, DateTimeUtility.getCurrentTimeZoneMillis());
		InspectMessage inspectMsg = new InspectMessage();

		// dump internal registries
		inspectMsg.setAttribute("clientRegistry", clientRegistry);
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
