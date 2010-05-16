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

import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.registry.ServiceRegistryItemPool;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPError;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.SCMPReply;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;
import com.stabilit.sc.util.MapBean;
import com.stabilit.sc.util.ValidatorUtility;

/**
 * The Class RegisterServiceCommand. Responsible for validation and execution of register command. Used to register
 * backend server in SC. Backend server will be registered in server registry of SC.
 * 
 * @author JTraber
 */
public class RegisterServiceCommand extends CommandAdapter implements IPassThrough {

	/**
	 * Instantiates a new RegisterServiceCommand.
	 */
	public RegisterServiceCommand() {
		this.commandValidator = new RegisterServiceCommandValidator();
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REGISTER_SERVICE;
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
		request.setAttribute(SocketAddress.class.getName(), socketAddress);
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();

		SCMPMessage message = request.getMessage();
		String serviceName = message.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
		MapBean<?> mapBean = serviceRegistry.get(serviceName);

		if (mapBean != null) {
			// server already registered
			if (LoggerListenerSupport.getInstance().isWarn()) {
				LoggerListenerSupport.getInstance().fireWarn(this, "command error: service already registered");
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.ALREADY_REGISTERED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}

		ServiceRegistryItemPool serviceRegistryItemPool = new ServiceRegistryItemPool(message, socketAddress);
		serviceRegistry.add(serviceName, serviceRegistryItemPool);

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
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
	 * The Class RegisterServiceCommandValidator.
	 */
	public class RegisterServiceCommandValidator implements ICommandValidator {

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
				// serviceName
				String serviceName = (String) message.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME);
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("ServiceName must be set!");
				}
				// maxSessions
				String maxSessions = (String) message.getHeader(SCMPHeaderAttributeKey.MAX_SESSIONS);
				ValidatorUtility.validateInt(0, maxSessions);
				request.setAttribute(SCMPHeaderAttributeKey.MAX_SESSIONS.getName(), maxSessions);
				// multiThreaded default = false
				Boolean multiThreaded = message.getHeaderBoolean(SCMPHeaderAttributeKey.MULTI_THREADED);
				if (multiThreaded == null) {
					multiThreaded = false;
				}
				request.setAttribute(SCMPHeaderAttributeKey.MULTI_THREADED.getName(), multiThreaded);
				// portNr
				String portNr = (String) message.getHeader(SCMPHeaderAttributeKey.PORT_NR);
				ValidatorUtility.validateInt(1, portNr, 99999);
				request.setAttribute(SCMPHeaderAttributeKey.PORT_NR.getName(), portNr);
			} catch (Throwable e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}
