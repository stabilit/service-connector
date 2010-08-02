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

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.NullCommandValidator;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.sc.registry.ISubscriptionPlace;
import com.stabilit.scm.sc.registry.ServiceRegistry;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.registry.SubscriptionSessionRegistry;
import com.stabilit.scm.sc.service.PublishService;
import com.stabilit.scm.sc.service.Server;
import com.stabilit.scm.sc.service.Service;
import com.stabilit.scm.sc.service.ServiceType;
import com.stabilit.scm.sc.service.Session;
import com.stabilit.scm.sc.service.SessionService;

/**
 * The Class CommandAdapter.
 * 
 * @author JTraber
 */
public abstract class CommandAdapter implements ICommand {

	/** The command validator. */
	protected ICommandValidator commandValidator;
	/** The session registry. */
	protected SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
	/** The subscription registry. */
	protected SubscriptionSessionRegistry subscriptionRegistry = SubscriptionSessionRegistry.getCurrentInstance();

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
	 *             occurs when session is not in registry, invalid session id
	 */
	protected Session getSessionById(String sessionId) throws SCMPCommandException {
		Session session = sessionRegistry.getSession(sessionId);

		if (session == null) {
			// incoming session not found
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: no session found for id :" + sessionId);
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_SESSION_FOUND);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return session;
	}

	protected Session getSubscriptionSessionById(String sessionId) throws SCMPCommandException {
		SubscriptionSessionRegistry sessionRegistry = SubscriptionSessionRegistry.getCurrentInstance();
		Session session = sessionRegistry.getSession(sessionId);

		if (session == null) {
			// incoming session not found
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: no session found for id :" + sessionId);
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_SESSION_FOUND);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return session;
	}

	/**
	 * Validate server. Checks properness of allocated server. If server null no free server available.
	 * 
	 * @param server
	 *            the server
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	public void validateServer(Server server) throws SCMPCommandException {
		if (server == null) {
			// no available server for this service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_SERVER);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
	}

	/**
	 * Gets the subscription place by id. Looks up the subscription place by session id.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the subscription place by id
	 * @throws Exception
	 *             the exception thrown if no session is found
	 */
	protected ISubscriptionPlace<SCMPMessage> getSubscriptionPlaceById(String sessionId) throws Exception {
		SubscriptionSessionRegistry subscriptionSessionRegistry = SubscriptionSessionRegistry.getCurrentInstance();
		Session session = subscriptionSessionRegistry.getSession(sessionId);

		if (session == null) {
			// incoming session not found
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: no session found for id :" + sessionId);
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_SESSION_FOUND);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return ((PublishService) session.getServer().getService()).getSubscriptionPlace();
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
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.UNKNOWN_SERVICE);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return service;
	}

	protected SessionService validateSessionService(String serviceName) throws SCMPCommandException {
		Service service = this.validateService(serviceName);
		if (service.getType() != ServiceType.SESSION_SERVICE) {
			// no service known with incoming serviceName
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.UNKNOWN_SERVICE);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return (SessionService) service;
	}

	protected PublishService validatePublishService(String serviceName) throws SCMPCommandException {
		Service service = this.validateService(serviceName);
		if (service.getType() != ServiceType.PUBLISH_SERVICE) {
			// no service known with incoming serviceName
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.UNKNOWN_SERVICE);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return (PublishService) service;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Throwable {
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
