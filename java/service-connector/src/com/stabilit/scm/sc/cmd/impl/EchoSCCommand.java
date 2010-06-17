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

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ECHO_SC;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage message = request.getMessage();

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		Object obj = message.getBody();
		scmpReply.setMessageType(getKey().getName());
		scmpReply.setSessionId(message.getSessionId());
		scmpReply.setBody(obj);
//		if (obj.toString().length() > 100) {
//			System.out.println("EchoSCCommand body = " + obj.toString().substring(0, 100));
//		} else {
//			System.out.println("EchoSCCommand body = " + obj.toString());
//		}
		response.setSCMP(scmpReply);
		return;
	}

	/**
	 * The Class EchoSCCommandValidator.
	 */
	public class EchoSCCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
		}
	}

}
