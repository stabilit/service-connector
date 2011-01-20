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
package org.serviceconnector.cmd.sc;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.FileService;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.ServiceState;
import org.serviceconnector.service.ServiceType;
import org.serviceconnector.service.Session;
import org.serviceconnector.service.SessionService;
import org.serviceconnector.service.StatefulService;
import org.serviceconnector.service.Subscription;

/**
 * The Class CommandAdapter. Adapter for every kind of command. Provides basic functions that is used by executions of commands.
 * 
 * @author JTraber
 */
public abstract class CommandAdapter implements ICommand {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CommandAdapter.class);

	/** The session registry. */
	protected SessionRegistry sessionRegistry = AppContext.getSessionRegistry();
	/** The subscription registry. */
	protected SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();
	/** The server registry. */
	protected ServerRegistry serverRegistry = AppContext.getServerRegistry();
	/** The service registry. */
	protected ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
	/** The basic conf. */
	protected BasicConfiguration basicConf = AppContext.getBasicConfiguration();

	/**
	 * Gets the session by id. Checks properness of session, if session is null given session id is wrong - no session found.
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
			// session not found in registry
			logger.info("session not found sid=" + sessionId);
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.SESSION_NOT_FOUND, sessionId);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return session;
	}

	protected Subscription getSubscriptionById(String subscriptionId) throws SCMPCommandException {
		Subscription subscription = this.subscriptionRegistry.getSubscription(subscriptionId);

		if (subscription == null) {
			// subscription not found in registry
			logger.info("subscription not found sid=" + subscriptionId);
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.SUBSCRIPTION_NOT_FOUND, subscriptionId);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return subscription;
	}

	/**
	 * Gets the subscription queue by id.
	 * 
	 * @param subscriptionId
	 *            the subscription id
	 * @return the subscription queue by id
	 * @throws Exception
	 *             the exception
	 */
	protected SubscriptionQueue<SCMPMessage> getSubscriptionQueueById(String subscriptionId) throws Exception {
		Subscription subscription = this.getSubscriptionById(subscriptionId);
		return ((PublishService) subscription.getServer().getService()).getSubscriptionQueue();
	}

	/**
	 * Gets the service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the service
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	protected Service getService(String serviceName) throws SCMPCommandException {
		Service service = this.serviceRegistry.getService(serviceName);
		if (service == null) {
			// service not found in registry
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.SERVICE_NOT_FOUND, serviceName);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return service;
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
		Service service = this.getService(serviceName);
		if (service.getState() == ServiceState.DISABLED) {
			// no session allowed for DISABLED service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.SERVICE_DISABLED, "service="
					+ serviceName + " is disabled");
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
	 *             the SCMP command exception
	 */
	protected SessionService validateSessionService(String serviceName) throws SCMPCommandException {
		Service service = this.validateService(serviceName);
		if (service.getType() != ServiceType.SESSION_SERVICE) {
			// service is not session service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.V_WRONG_SERVICE_TYPE, serviceName
					+ " is not session service");
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
	 *             the SCMP command exception
	 */
	protected PublishService validatePublishService(String serviceName) throws SCMPCommandException {
		Service service = this.validateService(serviceName);
		if (service.getType() != ServiceType.PUBLISH_SERVICE) {
			// service is not pblish service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.V_WRONG_SERVICE_TYPE, serviceName
					+ " is not publish service");
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return (PublishService) service;
	}

	/**
	 * Validate file service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the publish service
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	protected FileService validateFileService(String serviceName) throws SCMPCommandException {
		Service service = this.validateService(serviceName);
		if (service.getType() != ServiceType.FILE_SERVICE) {
			// service is not file service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.V_WRONG_SERVICE_TYPE, serviceName
					+ " is not file service");
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return (FileService) service;
	}

	/**
	 * Gets the stateful service. This method does not control the state of the service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the stateful service
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	protected StatefulService getStatefulService(String serviceName) throws SCMPCommandException {
		Service service = this.getService(serviceName);
		if (service.getType() != ServiceType.PUBLISH_SERVICE && service.getType() != ServiceType.SESSION_SERVICE) {
			// service is not the right type
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.V_WRONG_SERVICE_TYPE, serviceName
					+ " is not session or publish service");
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return (StatefulService) service;
	}


	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		throw new SCMPValidatorException(SCMPError.HV_ERROR, "validator is not implemented");
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPassThroughPartMsg() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public abstract SCMPMsgType getKey();
}
