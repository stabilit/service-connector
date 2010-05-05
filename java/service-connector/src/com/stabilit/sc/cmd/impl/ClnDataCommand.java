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

import java.util.logging.Logger;

import javax.xml.bind.ValidationException;

import com.stabilit.sc.cln.net.TransportException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.Session;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;
import com.stabilit.sc.srv.net.SCMPTransportException;
import com.stabilit.sc.util.ValidatorUtility;

public class ClnDataCommand extends CommandAdapter implements IPassThrough {

	public ClnDataCommand() {
		this.commandValidator = new ClnDataCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_DATA;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMP scmp = request.getSCMP();

		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		Session session = (Session) sessionRegistry.get(scmp.getSessionId());
		ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) session
				.getAttribute(ServiceRegistryItem.class.getName());
		try {
			SCMP scmpReply = serviceRegistryItem.srvData(scmp);
			scmpReply.setMessageType(getKey().getResponseName());
			response.setSCMP(scmpReply);
		} catch (TransportException e) {
			//clnDatat could not be sent successfully
			//TODO what is consequence?
			ExceptionListenerSupport.getInstance().fireException(this, e);
			throw new SCMPTransportException(SCMPErrorCode.SERVER_ERROR);
		}
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class ClnDataCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMP scmp = request.getSCMP();
			try {
				// sessionId
				String sessionId = scmp.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessionId must be set!");
				}
				if (!SessionRegistry.getCurrentInstance().containsKey(sessionId)) {
					throw new ValidationException("session does not exists!");
				}

				// serviceName
				String serviceName = scmp.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}

				// bodyLength
				String bodyLength = scmp.getHeader(SCMPHeaderAttributeKey.BODY_LENGTH);
				ValidatorUtility.validateInt(1, bodyLength);
				request.setAttribute(SCMPHeaderAttributeKey.BODY_LENGTH.getName(), bodyLength);

				// TODO messageId

				// compression default = true
				Boolean compression = scmp.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION);
				if (compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION.getName(), compression);

				// messageInfo
				String messageInfo = (String) scmp.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
				ValidatorUtility.validateString(0, messageInfo, 256);
			} catch (Throwable e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}
