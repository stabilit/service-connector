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
package org.serviceconnector.sc.cmd;

import org.apache.log4j.Logger;
import org.serviceconnector.common.cmd.ICommand;
import org.serviceconnector.common.cmd.ICommandValidator;
import org.serviceconnector.common.cmd.NullCommandValidator;
import org.serviceconnector.common.cmd.SCMPCommandException;
import org.serviceconnector.common.factory.IFactoryable;
import org.serviceconnector.common.scmp.IRequest;
import org.serviceconnector.common.scmp.IResponse;
import org.serviceconnector.common.scmp.SCMPError;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.scmp.SCMPMsgType;
import org.serviceconnector.sc.registry.ServerRegistry;
import org.serviceconnector.sc.registry.ServiceRegistry;
import org.serviceconnector.sc.registry.SessionRegistry;
import org.serviceconnector.sc.registry.SubscriptionQueue;
import org.serviceconnector.sc.registry.SubscriptionSessionRegistry;
import org.serviceconnector.sc.service.PublishService;
import org.serviceconnector.sc.service.Service;
import org.serviceconnector.sc.service.ServiceType;
import org.serviceconnector.sc.service.Session;
import org.serviceconnector.sc.service.SessionService;


/**
 * The Class CommandAdapter. Adapter for every kind of command. Provides basic functions that is used by executions of
 * commands.
 * 
 * @author JTraber
 */
public abstract class CommandAdapter implements ICommand {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CommandAdapter.class);
	
	/** The command validator. */
	protected ICommandValidator commandValidator;
	/** The session registry. */
	protected SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
	/** The subscription registry. */
	protected SubscriptionSessionRegistry subscriptionRegistry = SubscriptionSessionRegistry.getCurrentInstance();
	/** The server registry. */
	protected ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();

	/**
	 * Instantiates a new command adapter.
	 */
	public CommandAdapter() {
		this.commandValidator = NullCommandValidator.newInstance(); // www.refactoring.com Introduce NULL Object
	}

	/**
	 * Gets the session by id. Checks properness of session, if session is null given session id is wrong - no session
	 * found.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the session by id
	 * @throws SCMPCommandException
	 *             session is not in registry, invalid session id
	 */
	protected Session getSessionById(String sessionId) throws SCMPCommandException {
		Session session = sessionRegistry.getSession(sessionId);

		if (session == null) {
			// incoming session not found
			logger.warn("command error: no session found for id :" + sessionId);
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND,
					"no session found for " + sessionId);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return session;
	}

	/**
	 * Gets the subscription session by id.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the subscription session by id
	 * @throws SCMPCommandException
	 *             the sCMP command exception
	 */
	protected Session getSubscriptionSessionById(String sessionId) throws SCMPCommandException {
		SubscriptionSessionRegistry sessionRegistry = SubscriptionSessionRegistry.getCurrentInstance();
		Session session = sessionRegistry.getSession(sessionId);

		if (session == null) {
			// incoming session not found
			logger.warn("command error: no subscription session found for id :" + sessionId);
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND,
					"subscriptionQueue not found for " + sessionId);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return session;
	}

	/**
	 * Gets the subscription place by id.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the subscription place by id
	 * @throws Exception
	 *             the exception
	 */
	protected SubscriptionQueue<SCMPMessage> getSubscriptionQueueById(String sessionId) throws Exception {
		Session session = this.getSubscriptionSessionById(sessionId);
		return ((PublishService) session.getServer().getService()).getSubscriptionQueue();
	}

	/**
	 * Validate service. Lookup service in service registry and verify service existence.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the service
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	protected Service validateService(String serviceName) throws SCMPCommandException {
		ServiceRegistry serviceRegistry = ServiceRegistry.getCurrentInstance();

		Service service = serviceRegistry.getService(serviceName);
		if (service == null) {
			// no service known with incoming serviceName
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND,
					"service: "+serviceName+" not found");
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return service;
	}

	/**
	 * Validate session service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the session service
	 * @throws SCMPCommandException
	 *             the sCMP command exception
	 */
	protected SessionService validateSessionService(String serviceName) throws SCMPCommandException {
		Service service = this.validateService(serviceName);
		if (service.getType() != ServiceType.SESSION_SERVICE) {
			// no service known with incoming serviceName
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND,
					"service: "+serviceName+" is not session service");
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return (SessionService) service;
	}

	/**
	 * Validate publish service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the publish service
	 * @throws SCMPCommandException
	 *             the sCMP command exception
	 */
	protected PublishService validatePublishService(String serviceName) throws SCMPCommandException {
		Service service = this.validateService(serviceName);
		if (service.getType() != ServiceType.PUBLISH_SERVICE) {
			// no service known with incoming serviceName
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND,
					"service: "+serviceName+" is not publish service");
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return (PublishService) service;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public ICommandValidator getCommandValidator() {
		return commandValidator;
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAsynchronous() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public abstract SCMPMsgType getKey();
}
