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
package com.stabilit.sc.cmd.impl;

import java.net.SocketAddress;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.registry.ConnectionRegistry;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.KeepAlive;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.SCMPReply;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.CommandException;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;
import com.stabilit.sc.util.MapBean;
import com.stabilit.sc.util.ValidatorUtility;

public class ConnectCommand extends CommandAdapter implements IPassThrough {

	private static Logger log = Logger.getLogger(ConnectCommand.class);

	public ConnectCommand() {
		this.commandValidator = new ConnectCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CONNECT;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		log.debug("Run command " + this.getKey());
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		ConnectionRegistry connectionRegistry = ConnectionRegistry.getCurrentInstance();
		// TODO is socketAddress the right thing to save as a unique key?

		MapBean<?> mapBean = connectionRegistry.get(socketAddress);

		if (mapBean != null) {
			log.debug("command error: already connected");
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.ALREADY_CONNECTED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		connectionRegistry.add(socketAddress, request.getAttributeMapBean());

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setLocalDateTime();
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class ConnectCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMP scmp = request.getSCMP();

			try {

				Map<String, String> scmpHeader = scmp.getHeader();
				// scVersion TODO correct validation
				String scVersion = (String) scmpHeader.get(SCMPHeaderAttributeKey.SC_VERSION.getName());
				ValidatorUtility.validateSCVersion(SCMP.SC_VERSION, scVersion);
				request.setAttribute(SCMPHeaderAttributeKey.SC_VERSION.getName(), scVersion);

				//compression default = true
				Boolean compression = scmp.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION);
				if (compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION.getName(), compression);

				// localDateTime
				Date localDateTime = ValidatorUtility.validateLocalDateTime((String) scmpHeader
						.get(SCMPHeaderAttributeKey.LOCAL_DATE_TIME.getName()));
				request.setAttribute(SCMPHeaderAttributeKey.LOCAL_DATE_TIME.getName(), localDateTime);

				// KeepAliveTimeout && KeepAliveInterval
				KeepAlive keepAlive = ValidatorUtility.validateKeepAlive((String) scmpHeader
						.get(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT.getName()), (String) scmpHeader
						.get(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL.getName()));
				request.setAttribute(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT.getName(), keepAlive);
			} catch (Throwable e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				log.debug("validation error: " + e.getMessage());
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}