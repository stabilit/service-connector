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
import java.util.TimerTask;

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
import com.stabilit.scm.common.service.IRequestResponse;
import com.stabilit.scm.sc.registry.ISubscriptionPlace;
import com.stabilit.scm.sc.registry.SubscriptionSessionRegistry;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Service;
import com.stabilit.scm.sc.service.Session;

public class ClnSubscribeCommand extends CommandAdapter implements IPassThroughPartMsg {

	public ClnSubscribeCommand() {
		this.commandValidator = new ClnSubscribeCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_SUBSCRIBE;
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

		Server server = service.allocateServerAndSubscribe(reqMessage);

		// add server to session
		session.setServer(server);
		// finally add session to the registry
		SubscriptionSessionRegistry subscriptionSessionRegistry = SubscriptionSessionRegistry.getCurrentInstance();
		subscriptionSessionRegistry.addSession(session.getId(), session);
		ISubscriptionPlace subscriptionPlace = service.getSubscriptionPlace();
		TimerTask timerTask = new PublishTimerTask();
		subscriptionPlace.subscribe(session.getId(), timerTask);

		// creating reply
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getName());
		scmpReply.setSessionId(session.getId());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
	}

	private class ClnSubscribeCommandValidator implements ICommandValidator {

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

	private class PublishTimerTask extends TimerTask implements IRequestResponse {

		private IRequest request;
		private IResponse response;

		public PublishTimerTask() {
			this.request = null;
			this.response = null;
		}

		/**
		 * @param request
		 *            the request to set
		 */
		@Override
		public void setRequest(IRequest request) {
			this.request = request;
		}

		/**
		 * @return the request
		 */
		@Override
		public IRequest getRequest() {
			return request;
		}

		/**
		 * @param response
		 *            the response to set
		 */
		@Override
		public void setResponse(IResponse response) {
			this.response = response;
		}

		/**
		 * @return the response
		 */
		@Override
		public IResponse getResponse() {
			return response;
		}

		@Override
		public void run() {
			System.out.println("ReceivePublicationCommand.ReceivePublicationTimerTask.run()");
			SCMPMessage reply = new SCMPMessage();
			reply.setServiceName((String) request.getAttribute(SCMPHeaderAttributeKey.SERVICE_NAME));
			reply.setSessionId((String) request.getAttribute(SCMPHeaderAttributeKey.SESSION_ID));

		}

	}

}