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
package com.stabilit.sc.sim.cmd.impl;

import org.apache.log4j.Logger;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;

public class SrvSystemCommand extends CommandAdapter {

	private static Logger log = Logger.getLogger(SrvSystemCommand.class);

	public SrvSystemCommand() {
		this.commandValidator = new SrvSystemCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_SYSTEM;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		SCMP scmpReq = request.getSCMP();
		SCMP scmpReply = new SCMP();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setSessionId(scmpReq.getSessionId());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, scmpReq
				.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		scmpReply.setHeader("kill", "true");
		response.setSCMP(scmpReply);
		System.out.println("SrvSystemCommand.run()");
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class SrvSystemCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
		}
	}

}
