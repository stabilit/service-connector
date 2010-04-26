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
package com.stabilit.sc.srv.net.server.nio;

import com.stabilit.sc.common.scmp.IRequest;
import com.stabilit.sc.common.scmp.IResponse;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPPart;
import com.stabilit.sc.common.scmp.internal.SCMPComposite;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;

/**
 * @author JTraber
 * 
 */
public class NioCommandRequest {
	protected IRequest request;
	protected IResponse response;
	protected ICommand command;

	public NioCommandRequest(IRequest request, IResponse response) {
		this.request = request;
		this.response = response;
		this.command = null;
	}

	public void readRequest() throws Exception {
		this.request.read();
	}

	public ICommand readCommand() throws Exception {
		this.request.read();
		this.command = CommandFactory.getCurrentCommandFactory().newCommand(this.request);
		if (this.command == null) {
			return null;
		}
		SCMP scmp = this.request.getSCMP();
		if (scmp == null) {
			return null;
		}
		if (scmp.isPart() || this.command instanceof IPassThrough) {
			return this.command;
		}
		
		SCMPComposite scmpComposite = null;
		while (scmp.isPart()) {
			if (scmpComposite == null) {
				scmpComposite = new SCMPComposite(scmp, (SCMPPart) scmp);
			}
			String messageId = scmp.getHeader(SCMPHeaderAttributeKey.PART_ID);
			SCMPPart scmpReply = new SCMPPart();
			scmpReply.setIsReply(true);
			scmpReply.setHeader(SCMPHeaderAttributeKey.PART_ID, messageId);
			scmpReply.setMessageType(scmp.getMessageType());
			response.setSCMP(scmpReply);
			response.write();
			request.read();
			scmp = request.getSCMP();
			if (scmp != null) {
				scmpComposite.add(scmp);
			}
		}
		this.request.setSCMP(scmpComposite);
		return this.command;
	}
}