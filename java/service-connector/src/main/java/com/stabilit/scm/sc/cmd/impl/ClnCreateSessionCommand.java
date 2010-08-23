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
import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;
import com.stabilit.scm.sc.service.SessionService;

/**
 * The Class ClnCreateSessionCommand. Responsible for validation and execution of creates session command. Command runs
 * successfully if backend server accepts clients request and allows creating a session. Session is saved in a session
 * registry of SC.
 * 
 * @author JTraber
 */
public class ClnCreateSessionCommand extends CommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a new ClnCreateSessionCommand.
	 */
	public ClnCreateSessionCommand() {
		this.commandValidator = new ClnCreateSessionCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_CREATE_SESSION;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		// check service is present
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		SessionService service = this.validateSessionService(serviceName);

		// create session
		Session session = new Session();
		reqMessage.setSessionId(session.getId());
		// no need to forward echo attributes
		reqMessage.removeHeader(SCMPHeaderAttributeKey.ECHO_TIMEOUT);
		reqMessage.removeHeader(SCMPHeaderAttributeKey.ECHO_INTERVAL);

		// tries allocating a server for this session
		ISCMPSynchronousCallback callback = new CommandCallback();
		Server server = service.allocateServerAndCreateSession(reqMessage, callback, session);
		SCMPMessage reply = callback.getMessageSync();

		if (reply.isFault() == false) {
			boolean rejectSessionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			if (Boolean.FALSE.equals(rejectSessionFlag)) {
				// session has not been rejected, add server to session
				session.setServer(server);
				session.setEchoTimeoutSeconds((Integer) request.getAttribute(SCMPHeaderAttributeKey.ECHO_TIMEOUT));
				session.setEchoIntervalSeconds((Integer) request.getAttribute(SCMPHeaderAttributeKey.ECHO_INTERVAL));
				// finally add session to the registry
				SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
				sessionRegistry.addSession(session.getId(), session);
			} else {
				// session has been rejected - remove session id from header
				reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
			}
		} else {
			reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
		}
		// forward server reply to client
		reply.setIsReply(true);
		reply.setMessageType(getKey());
		response.setSCMP(reply);
	}

	/**
	 * The Class ClnCreateSessionCommandValidator.
	 */
	private class ClnCreateSessionCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			try {
				SCMPMessage message = request.getMessage();
				// messageId
				String messageId = (String) message.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID.getValue());
				if (messageId == null || messageId.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_ID, "messageId must be set");
				}
				// serviceName
				String serviceName = (String) message.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME.getValue());
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
				}
				// ipAddressList
				String ipAddressList = (String) message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getValue());
				ValidatorUtility.validateIpAddressList(ipAddressList);
				// sessionInfo
				String sessionInfo = (String) message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO.getValue());
				ValidatorUtility.validateStringLength(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
				// echoTimeout
				String echoTimeoutValue = message.getHeader(SCMPHeaderAttributeKey.ECHO_TIMEOUT.getValue());
				int echoTimeout = ValidatorUtility.validateInt(1, echoTimeoutValue, 3600,
						SCMPError.HV_WRONG_ECHO_TIMEOUT);
				request.setAttribute(SCMPHeaderAttributeKey.ECHO_TIMEOUT, echoTimeout);
				// echoInterval
				String echoIntervalValue = message.getHeader(SCMPHeaderAttributeKey.ECHO_INTERVAL.getValue());
				int echoInterval = ValidatorUtility.validateInt(1, echoIntervalValue, 3600,
						SCMPError.HV_WRONG_ECHO_INTERVAL);
				request.setAttribute(SCMPHeaderAttributeKey.ECHO_INTERVAL, echoInterval);
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