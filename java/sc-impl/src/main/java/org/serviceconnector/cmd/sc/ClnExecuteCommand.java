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
package org.serviceconnector.cmd.sc;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.IAsyncCommand;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.Server;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnExecuteCommand. Responsible for validation and execution of execute command. Execute command sends any
 * data to the server. Execute command runs asynchronously and passes through any parts messages.
 * 
 * @author JTraber
 */
public class ClnExecuteCommand extends CommandAdapter implements IAsyncCommand {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnExecuteCommand.class);

	/**
	 * Instantiates a new ClnExecuteCommand.
	 */
	public ClnExecuteCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_EXECUTE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		ClnExecuteCommandCallback callback = new ClnExecuteCommandCallback(request, response, responderCallback,
				sessionId);
		Session session = this.getSessionById(sessionId);
		// cancel session timeout
		this.sessionRegistry.cancelSessionTimeout(session);
		try {
			Server server = session.getServer();
			// try sending to the server
			int oti = message.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			server.execute(message, callback, oti);
		} catch (Exception ex) {
			// schedule session timeout
			this.sessionRegistry.scheduleSessionTimeout(session);
		}
		return;
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		try {
			SCMPMessage message = request.getMessage();
			// messageId
			String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID);
			if (messageId == null || messageId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_ID, "messageId must be set");
			}
			// serviceName
			String serviceName = message.getServiceName();
			if (serviceName == null || serviceName.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
			}
			// operation timeout
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT.getValue());
			ValidatorUtility.validateInt(10, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// sessionId
			String sessionId = message.getSessionId();
			if (sessionId == null || sessionId.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
			}
			// message info
			String messageInfo = (String) message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
			if (messageInfo != null) {
				ValidatorUtility.validateStringLength(1, messageInfo, 256, SCMPError.HV_WRONG_MESSAGE_INFO);
			}
			// compression
			message.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable ex) {
			logger.error("validate", ex);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAsynchronous() {
		return true;
	}

	/**
	 * The Class ClnExecuteCommandCallback.
	 */
	private class ClnExecuteCommandCallback extends CommandCallback {

		/** The callback. */
		private IResponderCallback callback;
		/** The request. */
		private IRequest request;
		/** The response. */
		private IResponse response;
		/** The session id. */
		private String sessionId;
		/** The Constant ERROR_STRING. */
		private static final String ERROR_STRING_TIMEOUT = "executing command timed out";
		/** The Constant ERROR_STRING_CONNECTION. */
		private static final String ERROR_STRING_CONNECTION = "broken connection";
		/** The Constant ERROR_STRING_FAIL. */
		private static final String ERROR_STRING_FAIL = "executing command failed";

		private SessionRegistry sessionRegistry = AppContext.getCurrentContext().getSessionRegistry();

		/**
		 * Instantiates a new ClnExecuteCommandCallback.
		 * 
		 * @param request
		 *            the request
		 * @param response
		 *            the response
		 * @param callback
		 *            the callback
		 */
		public ClnExecuteCommandCallback(IRequest request, IResponse response, IResponderCallback callback,
				String sessionId) {
			this.callback = callback;
			this.request = request;
			this.response = response;
			this.sessionId = sessionId;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) {
			scmpReply.setMessageType(getKey());
			this.response.setSCMP(scmpReply);
			this.callback.callback(request, response);
			// schedule session timeout
			Session session = this.sessionRegistry.getSession(this.sessionId);
			this.sessionRegistry.scheduleSessionTimeout(session);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(Exception ex) {
			SCMPMessage fault = null;
			if (ex instanceof IdleTimeoutException) {
				// operation timeout handling
				fault = new SCMPFault(SCMPError.GATEWAY_TIMEOUT, ERROR_STRING_TIMEOUT);
			} else if (ex instanceof IOException) {
				fault = new SCMPFault(SCMPError.CONNECTION_EXCEPTION, ERROR_STRING_CONNECTION);
			} else {
				fault = new SCMPFault(SCMPError.SC_ERROR, ERROR_STRING_FAIL);
			}
			this.callback(fault);
			// schedule session timeout
			Session session = this.sessionRegistry.getSession(this.sessionId);
			this.sessionRegistry.scheduleSessionTimeout(session);
		}
	}
}