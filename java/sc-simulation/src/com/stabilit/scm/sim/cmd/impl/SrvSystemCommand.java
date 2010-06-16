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
package com.stabilit.scm.sim.cmd.impl;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.cmd.impl.CommandAdapter;
import com.stabilit.scm.sc.registry.ServiceRegistry;

public class SrvSystemCommand extends CommandAdapter {

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
		SCMPMessage scmpReq = request.getMessage();
		if(scmpReq.getBodyLength() > 0) {
			String[] serviceNames = ((String) scmpReq.getBody()).split(":");
			for (String name : serviceNames) {
				ServiceRegistry.getCurrentInstance().removeService(name);
			}
		}
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getName());
		scmpReply.setSessionId(scmpReq.getSessionId());
		scmpReply.setHeader("kill", "true");
		response.setSCMP(scmpReply);
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
