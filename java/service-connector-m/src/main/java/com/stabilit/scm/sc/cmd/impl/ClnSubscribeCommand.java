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

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.SubscriptionPoint;
import com.stabilit.scm.common.net.req.netty.OperationTimeoutException;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.scmp.internal.SCMPPart;
import com.stabilit.scm.common.service.IFilterMask;
import com.stabilit.scm.common.service.SCSessionException;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.SubscriptionQueue;
import com.stabilit.scm.sc.registry.SubscriptionSessionRegistry;
import com.stabilit.scm.sc.service.IPublishTimerRun;
import com.stabilit.scm.sc.service.PublishService;
import com.stabilit.scm.sc.service.SCMPMessageFilterMask;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class ClnSubscribeCommand. Responsible for validation and execution of subscribe command. Allows subscribing to a
 * publish service.
 * 
 * @author JTraber
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
	public void run(IRequest request, IResponse response) throws Throwable {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		String mask = (String) request.getAttribute(SCMPHeaderAttributeKey.MASK);
		// check service is present
		PublishService service = this.validatePublishService(serviceName);

		// create session
		Session session = new Session();
		reqMessage.setSessionId(session.getId());

		int noDataInterval = (Integer) request.getAttribute(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);
		reqMessage.removeHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);

		ClnSubscribeCommandCallback callback = new ClnSubscribeCommandCallback();
		Server server = service.allocateServerAndSubscribe(reqMessage, callback, session);
		SCMPMessage reply = callback.getMessageSync();

		if (reply.isFault()) {
			// exception handling
			reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
			SCMPFault fault = (SCMPFault) reply;
			Throwable th = fault.getCause();
			if (th instanceof OperationTimeoutException) {
				// operation timeout handling
				HasFaultResponseException scmpEx = new SCMPCommandException(SCMPError.OPERATION_TIMEOUT);
				scmpEx.setMessageType(getKey());
				throw scmpEx;
			}
			throw th;
		}
		Boolean rejectSessionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
		if (Boolean.TRUE.equals(rejectSessionFlag)) {
			reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
			// server rejected session - throw exception with server errors
			SCSessionException e = new SCSessionException(SCMPError.SESSION_REJECTED, reply.getHeader());
			e.setMessageType(getKey());
			throw e;
		}
		// add server to session
		session.setServer(server);
		// finally add subscription to the registry
		SubscriptionSessionRegistry subscriptionSessionRegistry = SubscriptionSessionRegistry.getCurrentInstance();
		subscriptionSessionRegistry.addSession(session.getId(), session);

		SubscriptionQueue<SCMPMessage> subscriptionQueue = service.getSubscriptionQueue();

		IPublishTimerRun timerRun = new PublishTimerRun(subscriptionQueue, noDataInterval);
		IFilterMask<SCMPMessage> filterMask = new SCMPMessageFilterMask(mask);
		subscriptionQueue.subscribe(session.getId(), filterMask, timerRun);

		// forward reply to client
		reply.setIsReply(true);
		reply.setMessageType(getKey());
		response.setSCMP(reply);
	}

	/**
	 * The Class ClnSubscribeCommandValidator.
	 */
	private class ClnSubscribeCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();

			try {
				// messageId
				String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID);
				if (messageId == null || messageId.equals("")) {
					throw new SCMPValidatorException("messageId must be set!");
				}
				// serviceName
				String serviceName = message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
				// mask
				String mask = (String) message.getHeader(SCMPHeaderAttributeKey.MASK);
				if (mask == null) {
					throw new SCMPValidatorException("mask must be set!");
				}
				if (mask.indexOf("%") != -1) {
					// percent sign in mask not allowed
					throw new SCMPValidatorException("percent sign found in mask - not allowed.");
				}
				ValidatorUtility.validateString(1, mask, 256);
				// ipAddressList
				String ipAddressList = (String) message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
				ValidatorUtility.validateIpAddressList(ipAddressList);
				// sessionInfo
				String sessionInfo = (String) message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
				ValidatorUtility.validateString(1, sessionInfo, 256);
				// noDataInterval
				String noDataIntervalValue = message.getHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);
				int noi = ValidatorUtility.validateInt(1, noDataIntervalValue, 3600);
				request.setAttribute(SCMPHeaderAttributeKey.NO_DATA_INTERVAL, noi);
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
		private int timeoutSeconds;
		/** The subscription queue. */
		private SubscriptionQueue<SCMPMessage> subscriptionQueue;
		/** The request. */
		private IRequest request;
		/** The response. */
		private IResponse response;

		/**
		 * Instantiates a new publish timer run.
		 * 
		 * @param subscriptionPlace
		 *            the subscription place
		 * @param timeoutSeconds
		 *            the timeout
		 */
		public PublishTimerRun(SubscriptionQueue<SCMPMessage> subscriptionPlace, int timeoutSeconds) {
			this.request = null;
			this.response = null;
			this.timeoutSeconds = timeoutSeconds;
			this.subscriptionQueue = subscriptionPlace;
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutSeconds() {
			return this.timeoutSeconds;
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
			// extracting sessionId from request message
			SCMPMessage reqMsg = request.getMessage();
			String sessionId = reqMsg.getSessionId();

			// tries polling from queue
			SCMPMessage message = this.subscriptionQueue.poll(sessionId);
			if (message == null) {
				// no message found on queue - subscription timeout set up no data message
				reqMsg.setHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
				reqMsg.setIsReply(true);
				this.response.setSCMP(reqMsg);
				SubscriptionPoint.getInstance().fireSubscriptionNoDataTimeout(this, sessionId);
			} else {
				// set up reply
				SCMPMessage reply = null;
				if (message.isPart()) {
					reply = new SCMPPart();
				} else {
					reply = new SCMPMessage();
				}
				reply.setServiceName((String) request.getAttribute(SCMPHeaderAttributeKey.SERVICE_NAME));
				reply.setSessionId(sessionId);
				reply.setMessageType((String) request.getAttribute(SCMPHeaderAttributeKey.MSG_TYPE));
				reply.setIsReply(true);

				// message polling successful
				reply.setBody(message.getBody());
				reply
						.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, message
								.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
				reply.setHeader(SCMPHeaderAttributeKey.MSG_INFO, message.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
				reply.setHeader(SCMPHeaderAttributeKey.MASK, message.getHeader(SCMPHeaderAttributeKey.MASK));
				reply.setHeader(SCMPHeaderAttributeKey.ORIGINAL_MSG_ID, message
						.getHeader(SCMPHeaderAttributeKey.ORIGINAL_MSG_ID));
				reply.setBody(message.getBody());
				this.response.setSCMP(reply);
			}

			try {
				// send message back to client
				this.response.write();
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}

	/**
	 * The Class ClnSubscribeCommandCallback.
	 */
	private class ClnSubscribeCommandCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}