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
package com.stabilit.scm.srv.rr.cmd.impl;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.log.IExceptionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SCMessageFault;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.srv.ISCSessionServerCallback;
import com.stabilit.scm.srv.SrvService;

/**
 * The Class SrvDataCommand. Responsible for validation and execution of server data command. Allows executing data on
 * backend server.
 * 
 * @author JTraber
 */
public class SrvDataCommand extends SrvCommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SrvDataCommand.class);
	
	/**
	 * Instantiates a new SrvDataCommand.
	 */
	public SrvDataCommand() {
		this.commandValidator = new SrvDataCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.SRV_DATA;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		String serviceName = (String) request.getAttribute(SCMPHeaderAttributeKey.SERVICE_NAME);
		// look up srvService
		SrvService srvService = this.getSrvServiceByServiceName(serviceName);

		SCMPMessage scmpMessage = request.getMessage();
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(scmpMessage.getBody());
		scMessage.setCompressed(scmpMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(scmpMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(scmpMessage.getSessionId());

		// inform callback with scMessages
		ISCMessage scReply = ((ISCSessionServerCallback) srvService.getCallback()).execute(scMessage);

		// handling messageId
		SCMPMessageId messageId = this.sessionCompositeRegistry.getSCMPMessageId(scmpMessage.getSessionId());
		messageId.incrementMsgSequenceNr();
		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setServiceName(serviceName);
		reply.setSessionId(scmpMessage.getSessionId());
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_ID, messageId.getCurrentMessageID());
		reply.setMessageType(this.getKey());
		String msgInfo = scReply.getMessageInfo();
		if (msgInfo != null) {
			reply.setHeader(SCMPHeaderAttributeKey.MSG_INFO, msgInfo);
		}
		reply.setBody(scReply.getData());

		if (scReply.isFault()) {
			SCMessageFault scFault = (SCMessageFault) scReply;
			reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scFault.getAppErrorCode());
			reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scFault.getAppErrorText());
		}
		response.setSCMP(reply);
	}

	/**
	 * The Class SrvDataCommandValidator.
	 */
	public class SrvDataCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();

			try {
				// messageId
				String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID.getValue());
				if (messageId == null || messageId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_ID, "messageId must be set");
				}
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SESSION_ID, "sessionId must be set");
				}
				// serviceName
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
				}
				// message info
				String messageInfo = (String) message.getHeader(SCMPHeaderAttributeKey.MSG_INFO.getValue());
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
			} catch (Throwable th) {
				IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
				exceptionLogger.logErrorException(logger, this.getClass().getName(), "validate", th);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}
}
