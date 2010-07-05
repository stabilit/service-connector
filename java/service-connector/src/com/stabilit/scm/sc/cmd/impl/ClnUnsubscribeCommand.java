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
import java.util.Map;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Service;
import com.stabilit.scm.sc.service.Session;

public class ClnUnsubscribeCommand extends CommandAdapter implements IPassThroughPartMsg {

	public ClnUnsubscribeCommand() {
		this.commandValidator = new ClnUnsubscribeCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_UNSUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SocketAddress socketAddress = request.getRemoteSocketAddress(); // IP and port

		// lookup if client is correctly attached
		this.validateClientAttached(socketAddress);

		// check service is present
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		Service service = this.validateService(serviceName);

		// create session
		Session session = new Session();
		reqMessage.setSessionId(session.getId());

		session.setEchoTimeout((Integer) request.getAttribute(SCMPHeaderAttributeKey.ECHO_TIMEOUT));
		session.setEchoInterval((Integer) request.getAttribute(SCMPHeaderAttributeKey.ECHO_INTERVAL));

		// tries allocating a server for this session if server rejects session exception will be thrown
		// error codes and error text from server in reject case are inside the exception
		Server server = service.allocateServerAndCreateSession(reqMessage);

		// add server to session
		session.setServer(server);
		// finally add session to the registry
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		sessionRegistry.addSession(session.getId(), session);

		// creating reply
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getName());
		scmpReply.setSessionId(session.getId());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
	}

	private class ClnUnsubscribeCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			Map<String, String> scmpHeader = request.getMessage().getHeader();

			try {

				// serviceName
				String serviceName = (String) scmpHeader.get(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(getKey());
				throw ex;
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}
}