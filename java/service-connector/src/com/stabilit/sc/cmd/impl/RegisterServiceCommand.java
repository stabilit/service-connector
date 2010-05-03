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

import javax.xml.bind.ValidationException;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.listener.ExceptionListenerSupport;
import com.stabilit.sc.common.scmp.IRequest;
import com.stabilit.sc.common.scmp.IResponse;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPErrorCode;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPMsgType;
import com.stabilit.sc.common.scmp.SCMPReply;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.common.util.ValidatorUtility;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.registry.ServiceRegistryItemPool;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class RegisterServiceCommand extends CommandAdapter implements IPassThrough {

	private static Logger log = Logger.getLogger(RegisterServiceCommand.class);

	public RegisterServiceCommand() {
		this.commandValidator = new RegisterServiceCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REGISTER_SERVICE;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		request.setAttribute(SocketAddress.class.getName(), socketAddress);
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();

		SCMP scmp = request.getSCMP();
		String serviceName = scmp.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
		MapBean<?> mapBean = serviceRegistry.get(serviceName);

		if (mapBean != null) {
			log.debug("command error: service already registered");
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.ALREADY_REGISTERED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}

		ServiceRegistryItemPool serviceRegistryItemPool = new ServiceRegistryItemPool(scmp, socketAddress);
		serviceRegistry.add(serviceName, serviceRegistryItemPool);

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class RegisterServiceCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request) throws Exception {
			SCMP scmp = request.getSCMP();
			try {
				// serviceName
				String serviceName = (String) scmp.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("ServiceName must be set!");
				}

				// maxSessions
				String maxSessions = (String) scmp.getHeader(SCMPHeaderAttributeKey.MAX_SESSIONS);
				ValidatorUtility.validateInt(0, maxSessions);
				request.setAttribute(SCMPHeaderAttributeKey.MAX_SESSIONS.getName(), maxSessions);

				// multiThreaded default = false
				Boolean multiThreaded = scmp.getHeaderBoolean(SCMPHeaderAttributeKey.MULTI_THREADED);
				if (multiThreaded == null) {
					multiThreaded = false;
				}
				request.setAttribute(SCMPHeaderAttributeKey.MULTI_THREADED.getName(), multiThreaded);

				// portNr
				String portNr = (String) scmp.getHeader(SCMPHeaderAttributeKey.PORT_NR);
				ValidatorUtility.validateInt(1, portNr, 99999);
				request.setAttribute(SCMPHeaderAttributeKey.PORT_NR.getName(), portNr);
			} catch (Throwable e) {
				ExceptionListenerSupport.fireException(this, e);
				log.debug("validation error: " + e.getMessage());
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}
