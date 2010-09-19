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
package org.serviceconnector.sc.cmd;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.IAsyncCommand;
import org.serviceconnector.cmd.ICommandValidator;
import org.serviceconnector.cmd.IPassThroughPartMsg;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.IResponderCallback;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.sc.service.Server;
import org.serviceconnector.sc.service.Session;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.ValidatorUtility;


/**
 * The Class ClnExecuteCommand. Responsible for validation and execution of execute command. Execute command sends any
 * data to the server. Execute command runs asynchronously and passes through any parts messages.
 * 
 * @author JTraber
 */
public class ClnExecuteCommand extends CommandAdapter implements IPassThroughPartMsg, IAsyncCommand {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnExecuteCommand.class);

	/**
	 * Instantiates a new ClnExecuteCommand.
	 */
	public ClnExecuteCommand() {
		this.commandValidator = new ClnExecuteCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_EXECUTE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		ClnExecuteCommandCallback callback = new ClnExecuteCommandCallback(request, response, responderCallback);
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		Session session = this.getSessionById(sessionId);

		Server server = session.getServer();
		// try sending to the server
		server.sendData(message, callback,
				((Double) request.getAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT)));
		return;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAsynchronous() {
		return true;
	}

	/**
	 * The Class ClnExecuteCommandValidator.
	 */
	private class ClnExecuteCommandValidator implements ICommandValidator {

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
				int oti = ValidatorUtility.validateInt(1, otiValue, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
				request.setAttribute(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, oti);
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
				boolean compression = message.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION, compression);
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

		/** The Constant ERROR_STRING. */
		private static final String ERROR_STRING_TIMEOUT = "executing command timed out";
		/** The Constant ERROR_STRING_CONNECTION. */
		private static final String ERROR_STRING_CONNECTION = "broken connection";
		/** The Constant ERROR_STRING_FAIL. */
		private static final String ERROR_STRING_FAIL = "executing command failed";

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
		public ClnExecuteCommandCallback(IRequest request, IResponse response, IResponderCallback callback) {
			this.callback = callback;
			this.request = request;
			this.response = response;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) {
			scmpReply.setMessageType(getKey());
			this.response.setSCMP(scmpReply);
			this.callback.callback(request, response);
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
		}
	}
}