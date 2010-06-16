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
package com.stabilit.scm.common.net.res.netty;

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.scm.common.scmp.internal.SCMPPart;

/**
 * The Class NettyCommandRequest. NettyCommandRequest provides functions to read the command from a incoming request and
 * knows how to handle large requests.
 * 
 * @author JTraber
 */
public class NettyCommandRequest {

	/** The complete. */
	private boolean complete;
	/** The scmp composite receiver. */
	private SCMPCompositeReceiver compositeReceiver;

	/**
	 * The Constructor.
	 */
	public NettyCommandRequest() {
		// default, request is complete
		complete = true;
	}

	/**
	 * Read command from incoming request.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the i command
	 * @throws Exception
	 *             the exception
	 */
	public ICommand readCommand(IRequest request, IResponse response) throws Exception {
		request.read();
		// gets the command
		ICommand command = CommandFactory.getCurrentCommandFactory().newCommand(request);
		if (command == null) {
			return null;
		}
		SCMPMessage message = request.getMessage();
		if (message == null) {
			return null;
		}		
		if (command instanceof IPassThroughPartMsg) {
			// request not for local server, forward to next server
			complete = true;
			return command;
		}

		if (compositeReceiver == null) {
			if (message.isPart() == false) {
				// request not chunk
				return command;
			}
			// first part of a large request received - introduce composite receiver
			compositeReceiver = new SCMPCompositeReceiver(message, (SCMPMessage) message);
		} else {
			// next part of a large request received - add to composite receiver
			compositeReceiver.add(message);
		}

		if (message.isPart()) {
			// received message part - request not complete
			complete = false;
			// set up pull request
			SCMPMessage scmpReply = new SCMPPart();
			scmpReply.setIsReply(true);
			scmpReply.setMessageType(message.getMessageType());
			response.setSCMP(scmpReply);
		} else {
			// last message of a chunk message received - request complete
			complete = true;
			request.setMessage(compositeReceiver);
		}
		return command;
	}

	/**
	 * Checks if is complete.
	 * 
	 * @return true, if is complete
	 */
	public boolean isComplete() {
		return complete;
	}
}
