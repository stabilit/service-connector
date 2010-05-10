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

import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.sc.scmp.internal.SCMPPart;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;

/**
 * The Class NioCommandRequest. NioCommandRequest provides functions to read the command from a incoming request
 * and knows how to handle large requests.
 * 
 * @author JTraber
 */
public class NioCommandRequest {

	/** The request. */
	protected IRequest request;
	/** The response. */
	protected IResponse response;
	/** The command. */
	protected ICommand command;

	/**
	 * Instantiates a new nio command request.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	public NioCommandRequest(IRequest request, IResponse response) {
		this.request = request;
		this.response = response;
		this.command = null;
	}

	/**
	 * Read request.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void readRequest() throws Exception {
		this.request.read();
	}

	/**
	 * Read command.
	 * 
	 * @return the i command
	 * @throws Exception
	 *             the exception
	 */
	public ICommand readCommand() throws Exception {
		this.request.read();
		// gets the command
		this.command = CommandFactory.getCurrentCommandFactory().newCommand(this.request);
		if (this.command == null) {
			return null;
		}
		SCMP scmp = this.request.getSCMP();
		if (scmp == null) {
			return null;
		}
		if (scmp.isPart() == false) {
			// scmp is complete request
			return this.command;
		}
		if (this.command instanceof IPassThrough) {
			// request not for local server, forward to next server
			return this.command;
		}
		SCMPCompositeReceiver scmpCompositeRecv = null;
		// pulling data until request is complete
		while (scmp.isPart()) {
			if (scmpCompositeRecv == null) {
				scmpCompositeRecv = new SCMPCompositeReceiver(scmp, (SCMP) scmp);
			}
			// set up pull request
			SCMP scmpReply = new SCMPPart();
			scmpReply.setIsReply(true);
			scmpReply.setMessageType(scmp.getMessageType());
			response.setSCMP(scmpReply);
			response.write();
			request.readNext();
			scmp = request.getSCMP();
			if (scmp != null) {
				// add the scmp to composite receiver
				scmpCompositeRecv.add(scmp);
			}
		}
		this.request.setSCMP(scmpCompositeRecv);
		return this.command;
	}
}