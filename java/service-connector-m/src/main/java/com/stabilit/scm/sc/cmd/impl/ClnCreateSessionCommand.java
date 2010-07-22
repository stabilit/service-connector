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

import java.util.Map;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.service.SCSessionException;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Session;
import com.stabilit.scm.sc.service.SessionService;

/**
 * The Class ClnCreateSessionCommand. Responsible for validation and execution of creates session command. Command runs
 * successfully if backend server accepts clients request and allows creating a session. Session is saved in a session
 * registry of SC.
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

		session.setEchoTimeout((Integer) request.getAttribute(SCMPHeaderAttributeKey.ECHO_TIMEOUT));
		session.setEchoInterval((Integer) request.getAttribute(SCMPHeaderAttributeKey.ECHO_INTERVAL));

		reqMessage.removeHeader(SCMPHeaderAttributeKey.ECHO_TIMEOUT);
		reqMessage.removeHeader(SCMPHeaderAttributeKey.ECHO_INTERVAL);
		// tries allocating a server for this session if server rejects session exception will be thrown
		// error codes and error text from server in reject case are inside the exception
		ClnCreateSessionCommandCallback callback = new ClnCreateSessionCommandCallback();
		Server server = service.allocateServerAndCreateSession(reqMessage, callback);
		SCMPMessage reply = callback.getMessageSync();

		Boolean rejectSessionFlag = reply.getHeaderBoolean(SCMPHeaderAttributeKey.REJECT_SESSION);

		// TODO verify
		if (Boolean.TRUE.equals(rejectSessionFlag)) {
			// server rejected session - throw exception with server errors
			SCSessionException e = new SCSessionException(SCMPError.SESSION_REJECTED, reply.getHeader());
			throw e;
		}

		this.validateServer(server);

		// add server to session
		session.setServer(server);
		// finally add session to the registry
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		sessionRegistry.addSession(session.getId(), session);

		// creating reply
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getValue());
		scmpReply.setSessionId(session.getId());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
	}

	/**
	 * Validate server. Checks properness of allocated server. If server null no free server available.
	 * 
	 * @param server
	 *            the server
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	private void validateServer(Server server) throws SCMPCommandException {
		if (server == null) {
			// no available server for this service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_SERVER);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
	}

	/**
	 * The Class ClnCreateSessionCommandValidator.
	 */
	public class ClnCreateSessionCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			Map<String, String> scmpHeader = request.getMessage().getHeader();

			try {
				// serviceName
				String serviceName = (String) scmpHeader.get(SCMPHeaderAttributeKey.SERVICE_NAME.getValue());
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException("serviceName must be set!");
				}
				// ipAddressList
				String ipAddressList = (String) scmpHeader.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getValue());
				ValidatorUtility.validateIpAddressList(ipAddressList);
				// sessionInfo
				String sessionInfo = (String) scmpHeader.get(SCMPHeaderAttributeKey.SESSION_INFO.getValue());
				ValidatorUtility.validateString(0, sessionInfo, 256);
				// echoTimeout
				String echoTimeoutValue = scmpHeader.get(SCMPHeaderAttributeKey.ECHO_TIMEOUT.getValue());
				int echoTimeout = ValidatorUtility.validateInt(0, echoTimeoutValue, 3601);
				request.setAttribute(SCMPHeaderAttributeKey.ECHO_TIMEOUT.getValue(), echoTimeout);
				// echoInterval
				String echoIntervalValue = scmpHeader.get(SCMPHeaderAttributeKey.ECHO_INTERVAL.getValue());
				int echoInterval = ValidatorUtility.validateInt(0, echoIntervalValue, 3601);
				request.setAttribute(SCMPHeaderAttributeKey.ECHO_INTERVAL.getValue(), echoInterval);

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

	private class ClnCreateSessionCommandCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}