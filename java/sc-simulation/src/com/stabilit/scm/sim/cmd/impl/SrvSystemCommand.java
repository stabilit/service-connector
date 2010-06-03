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

import com.stabilit.sc.cmd.impl.CommandAdapter;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.srv.cmd.ICommandValidator;

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
				ServiceRegistry.getCurrentInstance().remove(name);
			}
		}
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getResponseName());
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
