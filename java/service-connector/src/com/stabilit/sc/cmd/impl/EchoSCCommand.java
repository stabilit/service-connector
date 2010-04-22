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

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;
import com.stabilit.sc.srv.cmd.SCOnly;

public class EchoSCCommand extends CommandAdapter implements SCOnly {

	private static Logger log = Logger.getLogger(EchoSCCommand.class);

	public EchoSCCommand() {
		this.commandValidator = new EchoSCCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ECHO_SC;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		SCMP scmp = request.getSCMP();

		SCMP scmpReply = new SCMPReply();
		Object obj = scmp.getBody();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setSessionId(scmp.getSessionId());
		scmpReply.setBody(obj);
		if (obj.toString().length() > 100) {
			System.out.println("EchoSCCommand body = " + obj.toString().substring(0, 100));
		} else {
			System.out.println("EchoSCCommand body = " + obj.toString());
		}
		response.setSCMP(scmpReply);
		return;
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class EchoSCCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
		}
	}

}
