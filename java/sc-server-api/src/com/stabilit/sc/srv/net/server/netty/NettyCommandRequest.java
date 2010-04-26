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
package com.stabilit.sc.srv.net.server.netty;

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
public class NettyCommandRequest {

	private boolean complete;
	private SCMPComposite scmpComposite;

	/**
	 * @param request
	 * @param response
	 */
	public NettyCommandRequest() {
		complete = true;
	}

	public ICommand readCommand(IRequest request, IResponse response) throws Exception {
		request.read();
		ICommand command = CommandFactory.getCurrentCommandFactory().newCommand(request);
		if (command == null) {
			return null;
		}

		SCMP scmp = request.getSCMP();
		if (scmp == null) {
			return null;
		}

		// request not for SC, forward to server
		if (command instanceof IPassThrough) {
			complete = true;
			return command;
		}

		if (scmpComposite == null) {
			// request not chunked
			if (scmp.isPart() == false) {
				return command;
			}
			scmpComposite = new SCMPComposite(scmp, (SCMPPart) scmp);
		} else {
			scmpComposite.add(scmp);
		}

		// request is part of a chunked message
		if (scmp.isPart()) {
			complete = false;
			String messageId = scmp.getHeader(SCMPHeaderAttributeKey.PART_ID);
			SCMPPart scmpReply = new SCMPPart();
			scmpReply.setIsReply(true);
			scmpReply.setHeader(SCMPHeaderAttributeKey.PART_ID, messageId);
			scmpReply.setMessageType(scmp.getMessageType());
			response.setSCMP(scmpReply);
		} else { // last request of a chunked message
			complete = true;
			request.setSCMP(scmpComposite);
		}
		return command;
	}

	public boolean isComplete() {
		return complete;
	}
}
