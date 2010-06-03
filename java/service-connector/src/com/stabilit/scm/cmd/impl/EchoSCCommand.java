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

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

/**
 * The Class EchoSCCommand. Responsible for validation and execution of echoSC command. Simply sends back incoming
 * content.
 */
public class EchoSCCommand extends CommandAdapter {

	/**
	 * Instantiates a new EchoSCCommand.
	 */
	public EchoSCCommand() {
		this.commandValidator = new EchoSCCommandValidator();
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ECHO_SC;
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
		SCMPMessage message = request.getMessage();

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		Object obj = message.getBody();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setSessionId(message.getSessionId());
		scmpReply.setBody(obj);
		if (obj.toString().length() > 100) {
			System.out.println("EchoSCCommand body = " + obj.toString().substring(0, 100));
		} else {
			System.out.println("EchoSCCommand body = " + obj.toString());
		}
		response.setSCMP(scmpReply);
		return;
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
	 * The Class EchoSCCommandValidator.
	 */
	public class EchoSCCommandValidator implements ICommandValidator {

		/**
		 * Validate request, nothing to validate in case of echoSC.
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
