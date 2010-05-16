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

import javax.xml.bind.ValidationException;

import com.stabilit.sc.cln.net.CommunicationException;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.registry.ServiceRegistry;
import com.stabilit.sc.registry.ServiceRegistryItem;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.scmp.IRequest;
import com.stabilit.sc.scmp.IResponse;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.SCMPReply;
import com.stabilit.sc.scmp.Session;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.IPassThrough;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

/**
 * The Class ClnDeleteSessionCommand. Responsible for validation and execution of delete session command. Deleting
 * a session means: Free up backend server from session and delete session entry in SC session registry.
 * 
 * @author JTraber
 */
public class ClnDeleteSessionCommand extends CommandAdapter implements IPassThrough {

	/**
	 * Instantiates a new ClnDeleteSessionCommand.
	 */
	public ClnDeleteSessionCommand() {
		this.commandValidator = new ClnDeleteSessionCommandValidator();
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_DELETE_SESSION;
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
		SCMPMessage message = request.getMessage();
		String sessionId = message.getSessionId();
		Session session = getSessionById(sessionId);

		ServiceRegistryItem serviceRegistryItem = (ServiceRegistryItem) session
				.getAttribute(ServiceRegistryItem.class.getName());
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();

		try {
			serviceRegistry.deallocate(serviceRegistryItem, message); // calls srvDeleteSession inside
		} catch (CommunicationException e) {
			// deallocate failed, connection to backend server disturbed - clean up
			serviceRegistryItem.markObsolete();			
			ExceptionListenerSupport.getInstance().fireException(this, e);
		}
		// delete session entry from session registry
		SessionRegistry.getCurrentInstance().remove(sessionId);
		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, message
				.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
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
	 * The Class ClnDeleteSessionCommandValidator.
	 */
	public class ClnDeleteSessionCommandValidator implements ICommandValidator {

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
					throw new ValidationException("serviceName must be set!");
				}
				// sessionId
				String sessionId = message.getSessionId();
				if (sessionId == null || sessionId.equals("")) {
					throw new ValidationException("sessonId must be set!");
				}
			} catch (Throwable e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}
