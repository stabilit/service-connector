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

import java.net.SocketAddress;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.registry.ClientRegistry;
import com.stabilit.scm.sc.service.Client;

/**
 * The Class DetachCommand. Responsible for validation and execution of detach command. Allows client to
 * detach (virtual detach) to SC. Client will be removed from Client Registry of SC.
 * 
 * @author JTraber
 */
public class DetachCommand extends CommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a new DetachCommand.
	 */
	public DetachCommand() {
		this.commandValidator = new DetachCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.DETACH;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SocketAddress socketAddress = request.getRemoteSocketAddress();
		ClientRegistry clientRegistry = ClientRegistry.getCurrentInstance();

		Client client = clientRegistry.getClient(socketAddress);
		if (client == null) {
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: client not connected");
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.UNKNOWN_CLIENT);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		// remove client entry from connection registry
		clientRegistry.removeClient(socketAddress);
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getName());
		response.setSCMP(scmpReply);
	}

	/**
	 * The Class DetachCommandValidator.
	 */
	public class DetachCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws SCMPValidatorException {
		}
	}
}
