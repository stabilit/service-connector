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
import java.util.Date;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThroughPartMsg;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.log.listener.ExceptionPoint;
import com.stabilit.scm.common.log.listener.LoggerPoint;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.scmp.internal.KeepAlive;
import com.stabilit.scm.common.util.DateTimeUtility;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.ClientRegistry;
import com.stabilit.scm.sc.service.Client;

/**
 * The Class AttachCommand. Responsible for validation and execution of attach command. Allows client to attach (virtual
 * attach) to SC. Client is registered in Client Registry of SC.
 * 
 * @author JTraber
 */
public class AttachCommand extends CommandAdapter implements IPassThroughPartMsg {

	/**
	 * Instantiates a new AttachCommand.
	 */
	public AttachCommand() {
		this.commandValidator = new AttachCommandValidator();
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ATTACH;
	}

	/**
	 * Gets the command validator.
	 * 
	 * @return the command validator
	 */
	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	/**
	 * Run command.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SocketAddress socketAddress = request.getRemoteSocketAddress();
		ClientRegistry clientRegistry = ClientRegistry.getCurrentInstance();

		// check if client has been attached already
		Client client = clientRegistry.getClient(socketAddress);
		this.validateClientNotAttached(client);

		client = new Client(socketAddress, request);
		// attach client - add entry in client registry for current client
		clientRegistry.addClient(client.getSocketAddress(), client);

		// set up response
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, DateTimeUtility.getCurrentTimeZoneMillis());
		response.setSCMP(scmpReply);
	}

	/**
	 * Validate client. Controls if requesting client has not been attached before. Attaching a client two times
	 * violates the protocol.
	 * 
	 * @param client
	 *            the client
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	private void validateClientNotAttached(Client client) throws SCMPCommandException {
		if (client != null) {
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: already attache");
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.ALREADY_ATTACHED);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
	}

	/**
	 * New instance.
	 * 
	 * @return the factoryable
	 */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/**
	 * The Class AttachCommandValidator.
	 */
	public class AttachCommandValidator implements ICommandValidator {

		/**
		 * Validate request.
		 * 
		 * @param request
		 *            the request
		 * @throws Exception
		 *             the exception
		 */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();

			try {
				// scVersion
				String scVersion = message.getHeader(SCMPHeaderAttributeKey.SC_VERSION);
				SCMPMessage.SC_VERSION.isSupported(scVersion);

				// localDateTime
				Date localDateTime = ValidatorUtility.validateLocalDateTime(message
						.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME));
				request.setAttribute(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, localDateTime);

				// KeepAliveTimeout && KeepAliveInterval
				KeepAlive keepAlive = ValidatorUtility.validateKeepAlive(message
						.getHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT), message
						.getHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL));
				request.setAttribute(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT, keepAlive);
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