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
package org.serviceconnector.cmd.srv;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.ICommandValidator;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageId;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.SCMessage;
import org.serviceconnector.srv.ISCPublishServerCallback;
import org.serviceconnector.srv.SrvService;


/**
 * The Class SrvUnsubscribeCommand. Responsible for validation and execution of server unsubscribe command. Allows
 * unsubscribing from service.
 * 
 * @author JTraber
 */
public class SrvUnsubscribeCommand extends SrvCommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SrvUnsubscribeCommand.class);
	
	/**
	 * Instantiates a new SrvUnsubscribeCommand.
	 */
	public SrvUnsubscribeCommand() {
		this.commandValidator = new SrvUnsubscribeCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_UNSUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		String serviceName = (String) request.getAttribute(SCMPHeaderAttributeKey.SERVICE_NAME);
		// look up srvService
		SrvService srvService = this.getSrvServiceByServiceName(serviceName);

		SCMPMessage scmpMessage = request.getMessage();
		String sessionId = scmpMessage.getSessionId();
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(scmpMessage.getBody());
		scMessage.setOperationTimeout(Integer.parseInt(scmpMessage.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT)));
		scMessage.setSessionId(sessionId);

		// inform callback with scMessages
		((ISCPublishServerCallback) srvService.getCallback()).unsubscribe(scMessage);
		// handling messageId
		SCMPMessageId messageId = this.sessionCompositeRegistry.getSCMPMessageId(sessionId);
		messageId.incrementMsgSequenceNr();
		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, messageId.getCurrentMessageID());
		reply.setServiceName(serviceName);
		reply.setSessionId(scmpMessage.getSessionId());
		reply.setMessageType(this.getKey());
		response.setSCMP(reply);
		// delete session in SCMPSessionCompositeRegistry
		this.sessionCompositeRegistry.removeSession(sessionId);
	}

	/**
	 * The Class SrvUnsubscribeCommandValidator.
	 */
	private class SrvUnsubscribeCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();
			try {
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
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
				}
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(getKey());
				throw ex;
			} catch (Throwable th) {
				logger.error("validate", th);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}
}