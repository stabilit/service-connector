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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

import javax.xml.bind.ValidationException;

import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.IPassThrough;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.ctx.IRequestContext;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.net.CommunicationException;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.scmp.Session;
import com.stabilit.scm.common.util.MapBean;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.registry.ClientRegistry;
import com.stabilit.scm.sc.registry.ServiceRegistry;
import com.stabilit.scm.sc.registry.ServiceRegistryItem;
import com.stabilit.scm.sc.registry.SessionRegistry;

/**
 * The Class ClnCreateSessionCommand. Responsible for validation and execution of creates session command. Command
 * runs successfully if backend server accepts clients request and allows creating a session. Session is saved in a
 * session registry of SC.
 */
public class ClnCreateSessionCommand extends CommandAdapter implements IPassThrough {

	/**
	 * Instantiates a new ClnCreateSessionCommand.
	 */
	public ClnCreateSessionCommand() {
		this.commandValidator = new ClnCreateSessionCommandValidator();
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_CREATE_SESSION;
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
		// first verify that client has correctly attached
		// TODO (TRN) This will not work when appl is connected twice to the same SC or two apps on the same node connects to the same SC!
		// instead of using socketAddress the client must have a clientId that is key to the ClientRegistry
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		ClientRegistry clientRegistry = ClientRegistry.getCurrentInstance();
		MapBean<?> mapBean = clientRegistry.get(socketAddress);

		if (mapBean == null) {
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: not attached");
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_ATTACHED);	// TODO (TRN) => unknown client
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}

		// get free server
		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();	
		// adding ip of current unit to header field ip address list
		String ipList = message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			ipList += inetSocketAddress.getAddress();
			message.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		}

		mapBean = serviceRegistry.get(serviceName);
		if (mapBean == null) {
			// no service known with incoming serviceName
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.UNKNOWN_SERVICE);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}

		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		// create session
		Session session = new Session();
		message.setSessionId(session.getId());
		ServiceRegistryItem serviceRegistryItem = null;
		try {
			// try to allocate session on a backend server
			// TODO (TRN) take care, no free server can be available! => throw new SCMPCommandException(SCMPError.NO_FREE_SERVER);

			serviceRegistryItem = serviceRegistry.allocate(request);
			// TODO (TRN) take care, the server can reject the session! The server response must be evaluated
			if (serviceRegistryItem == null) {
				System.out.println("ClnCreateSessionCommand.run()");
			}
		} catch (CommunicationException ex) {
			// allocate session failed, connection to backend server disturbed - clean up
			ExceptionPoint.getInstance().fireException(this, ex);
			SCMPCommunicationException communicationException = new SCMPCommunicationException(
					SCMPError.SERVER_ERROR);
			communicationException.setMessageType(getResponseKeyName());
			throw communicationException;
		}
		
		// finally add session to the registry
		session.setAttribute(ServiceRegistryItem.class.getName(), serviceRegistryItem);
		sessionRegistry.add(session.getId(), session);

		// creating reply
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setSessionId(session.getId());
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
	 * The Class ClnCreateSessionCommandValidator.
	 */
	public class ClnCreateSessionCommandValidator implements ICommandValidator {

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
			Map<String, String> scmpHeader = request.getMessage().getHeader();

			try {
				// serviceName
				String serviceName = (String) scmpHeader.get(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
				if (serviceName == null || serviceName.equals("")) {
					throw new ValidationException("serviceName must be set!");
				}
				// ipAddressList
				String ipAddressList = (String) scmpHeader.get(SCMPHeaderAttributeKey.IP_ADDRESS_LIST.getName());
				ValidatorUtility.validateIpAddressList(ipAddressList);
				// sessionInfo
				String sessionInfo = (String) scmpHeader.get(SCMPHeaderAttributeKey.SESSION_INFO.getName());
				ValidatorUtility.validateString(0, sessionInfo, 256);
			} catch (Throwable e) {
				ExceptionPoint.getInstance().fireException(this, e);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}