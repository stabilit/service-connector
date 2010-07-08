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
import com.stabilit.scm.common.service.IFilterMask;
import com.stabilit.scm.sc.registry.ISubscriptionPlace;
import com.stabilit.scm.sc.registry.SubscriptionSessionRegistry;
import com.stabilit.scm.sc.service.FilterMask;
import com.stabilit.scm.sc.service.IPublishTimerRun;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Service;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class ClnSubscribeCommand. Subscribes client to a subscription place.
 */
public class ClnSubscribeCommand extends CommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a ClnSubscribeCommand.
	 */
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
		// IP and port
		SocketAddress socketAddress = request.getRemoteSocketAddress();
		// lookup if client is correctly attached
		this.validateClientAttached(socketAddress);

		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		String mask = reqMessage.getHeader(SCMPHeaderAttributeKey.MASK);
		// check service is present
		Service service = this.validateService(serviceName);

		// create session
		Session session = new Session();
		reqMessage.setSessionId(session.getId());

		Server server = service.allocateServerAndSubscribe(reqMessage);

		// add server to session
		session.setServer(server);
		// finally add subscription to the registry
		SubscriptionSessionRegistry subscriptionSessionRegistry = SubscriptionSessionRegistry.getCurrentInstance();
		subscriptionSessionRegistry.addSession(session.getId(), session);

		ISubscriptionPlace<SCMPMessage> subscriptionPlace = service.getSubscriptionPlace();
		// TODO verify with jan - timeout arrives with cln_subscribe
		IPublishTimerRun timerRun = new PublishTimerRun(subscriptionPlace, 300);
		IFilterMask filterMask = new FilterMask(mask);
		subscriptionPlace.subscribe(session.getId(), filterMask, timerRun);

		// creating reply
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getName());
		scmpReply.setSessionId(session.getId());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
	}

	/**
	 * The Class ClnSubscribeCommandValidator.
	 */
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

	/**
	 * The Class PublishTimerRun. PublishTimerRun defines action to get in place when subscription times out.
	 */
	private class PublishTimerRun implements IPublishTimerRun {

		/** The timeout. */
		private int timeout;
		/** The subscription place. */
		private ISubscriptionPlace<SCMPMessage> subscriptionPlace;
		/** The request. */
		private IRequest request;
		/** The response. */
		private IResponse response;

		/**
		 * Instantiates a new publish timer run.
		 * 
		 * @param subscriptionPlace
		 *            the subscription place
		 * @param timeout
		 *            the timeout
		 */
		public PublishTimerRun(ISubscriptionPlace<SCMPMessage> subscriptionPlace, int timeout) {
			this.request = null;
			this.response = null;
			this.timeout = timeout;
			this.subscriptionPlace = subscriptionPlace;
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeout() {
			return this.timeout;
		}

		/** {@inheritDoc} */
		@Override
		public void setRequest(IRequest request) {
			this.request = request;
		}

		/** {@inheritDoc} */
		@Override
		public void setResponse(IResponse response) {
			this.response = response;
		}

		/** {@inheritDoc} */
		@Override
		public void timeout() {
			SCMPMessage reply = new SCMPMessage();
			String sessionId = (String) request.getAttribute(SCMPHeaderAttributeKey.SESSION_ID);
			reply.setServiceName((String) request.getAttribute(SCMPHeaderAttributeKey.SERVICE_NAME));
			reply.setSessionId(sessionId);
			reply.setMessageType((String) request.getAttribute(SCMPHeaderAttributeKey.MSG_TYPE));
			reply.setIsReply(true);
			SCMPMessage message = this.subscriptionPlace.poll(sessionId);
			if (message == null) {
				reply.setHeader(SCMPHeaderAttributeKey.NO_DATA, true);
			} else {
				reply.setBody(message.getBody());
				reply
						.setHeader(SCMPHeaderAttributeKey.MASK, (String) request
								.getAttribute(SCMPHeaderAttributeKey.MASK));
			}
			response.setSCMP(reply);
			try {
				// send message back to client
				response.write();
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}
}