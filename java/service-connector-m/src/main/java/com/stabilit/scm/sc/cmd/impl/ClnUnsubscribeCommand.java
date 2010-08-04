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
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.sc.registry.ISubscriptionPlace;
import com.stabilit.scm.sc.registry.SubscriptionSessionRegistry;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class ClnUnsubscribeCommand. Responsible for validation and execution of unsubscribe command. Allows
 * unsubscribing from a publish service.
 */
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
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		SubscriptionSessionRegistry.getCurrentInstance().getSession(sessionId);

		// lookup session and checks properness
		Session session = this.getSubscriptionSessionById(sessionId);

		Server server = session.getServer();
		SCMPMessage reply = null;
		try {
			ClnSubscribeCommandCallback callback = new ClnSubscribeCommandCallback();
			server.unsubscribe(message, callback);
			reply = callback.getMessageSync();
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
			/**
			 * error in unsubscribe process<br>
			 * 1. delete subscription in registry on SC<br>
			 * 2. destroy subscription - dereference messages in subscription queue<br>
			 * 3. EXC message to client<br>
			 **/
			// TODO error handling
		}
		// looks up subscription place
		ISubscriptionPlace<SCMPMessage> subscriptionPlace = this.getSubscriptionPlaceById(sessionId);
		subscriptionPlace.unsubscribe(sessionId);
		// delete session on server successful - delete entry from session registry
		this.subscriptionRegistry.removeSession(sessionId);

		// forward reply to client
		reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
		reply.setIsReply(true);
		reply.setMessageType(this.getKey());
		response.setSCMP(reply);
	}

	/**
	 * The Class ClnUnsubscribeCommandValidator.
	 */
	private class ClnUnsubscribeCommandValidator implements ICommandValidator {

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
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException("sessionId must be set!");
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
	 * The Class ClnSubscribeCommandCallback.
	 */
	private class ClnSubscribeCommandCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}