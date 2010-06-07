/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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

import javax.xml.bind.ValidationException;

import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.listener.ExceptionPoint;
import com.stabilit.scm.listener.LoggerPoint;
import com.stabilit.scm.sc.registry.ServiceRegistry;
import com.stabilit.scm.scmp.IRequest;
import com.stabilit.scm.scmp.IResponse;
import com.stabilit.scm.scmp.SCMPError;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMessage;
import com.stabilit.scm.scmp.SCMPMsgType;
import com.stabilit.scm.srv.cmd.ICommandValidator;
import com.stabilit.scm.srv.cmd.IPassThrough;
import com.stabilit.scm.srv.cmd.SCMPCommandException;
import com.stabilit.scm.srv.cmd.SCMPValidatorException;
import com.stabilit.scm.util.MapBean;

/**
 * The Class DeRegisterServiceCommand. Responsible for validation and execution of deregister command. Used to
 * deregister backend server from SC. Backend server will be removed from server registry of SC.
 * 
 * @author JTraber
 */
public class DeRegisterServiceCommand extends CommandAdapter implements IPassThrough {

	/**
	 * Instantiates a new DeRegisterServiceCommand.
	 */
	public DeRegisterServiceCommand() {
		this.commandValidator = new DeRegisterServiceCommandValidator();
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.DEREGISTER_SERVICE;
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
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();
		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		MapBean<?> mapBean = serviceRegistry.get(serviceName);

		if (mapBean == null) {
			// server not registered - deregister not possible
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: service not registered");
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_REGISTERED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		serviceRegistry.remove(serviceName);
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
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
	 * The Class DeRegisterServiceCommandValidator.
	 */
	public class DeRegisterServiceCommandValidator implements ICommandValidator {

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
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("ServiceName must be set!");
				}
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}
