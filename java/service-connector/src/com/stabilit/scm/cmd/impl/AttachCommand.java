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
package com.stabilit.scm.cmd.impl;

import java.net.SocketAddress;
import java.util.Date;

import com.stabilit.scm.ctx.IRequestContext;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.listener.ExceptionPoint;
import com.stabilit.scm.listener.LoggerPoint;
import com.stabilit.scm.registry.ClientRegistry;
import com.stabilit.scm.scmp.IRequest;
import com.stabilit.scm.scmp.IResponse;
import com.stabilit.scm.scmp.SCMPError;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMsgType;
import com.stabilit.scm.scmp.internal.KeepAlive;
import com.stabilit.scm.srv.cmd.ICommandValidator;
import com.stabilit.scm.srv.cmd.IPassThrough;
import com.stabilit.scm.srv.cmd.SCMPCommandException;
import com.stabilit.scm.srv.cmd.SCMPValidatorException;
import com.stabilit.scm.util.DateTimeUtility;
import com.stabilit.scm.util.MapBean;
import com.stabilit.scm.util.ValidatorUtility;

/**
 * The Class AttachCommand. Responsible for validation and execution of attach command. Allows client to attach
 * (virtual attach) to SC. Client is registered in Client Registry of SC.
 * 
 * @author JTraber
 */
public class AttachCommand extends CommandAdapter implements IPassThrough {

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
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		ClientRegistry clientRegistry = ClientRegistry.getCurrentInstance();

		MapBean<?> mapBean = clientRegistry.get(socketAddress);

		if (mapBean != null) {
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: already attache");
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.ALREADY_ATTACHED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		// add entry in connection registry for current client
		clientRegistry.add(socketAddress, request.getAttributeMapBean());

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, DateTimeUtility.getCurrentTimeZoneMillis());
		response.setSCMP(scmpReply);
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
				String scVersion = message.getHeader(SCMPHeaderAttributeKey.SC_VERSION);
				SCMPMessage.SC_VERSION.isSupported(scVersion);
				request.setAttribute(SCMPHeaderAttributeKey.SC_VERSION.getName(), scVersion);
				// compression default = true
				Boolean compression = message.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION);
				if (compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION.getName(), compression);
				// localDateTime
				Date localDateTime = ValidatorUtility.validateLocalDateTime(message
						.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME));
				request.setAttribute(SCMPHeaderAttributeKey.LOCAL_DATE_TIME.getName(), localDateTime);
				// KeepAliveTimeout && KeepAliveInterval
				KeepAlive keepAlive = ValidatorUtility.validateKeepAlive(message
						.getHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT), message
						.getHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL));
				request.setAttribute(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT.getName(), keepAlive);
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}