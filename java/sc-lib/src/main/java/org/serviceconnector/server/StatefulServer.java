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
package org.serviceconnector.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPSrvAbortSessionCall;
import org.serviceconnector.call.SCMPSrvChangeSubscriptionCall;
import org.serviceconnector.call.SCMPSrvCreateSessionCall;
import org.serviceconnector.call.SCMPSrvDeleteSessionCall;
import org.serviceconnector.call.SCMPSrvExecuteCall;
import org.serviceconnector.call.SCMPSrvSubscribeCall;
import org.serviceconnector.call.SCMPSrvUnsubscribeCall;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.sc.CommandCallback;
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.ServiceType;
import org.serviceconnector.service.Session;
import org.serviceconnector.service.StatefulService;
import org.serviceconnector.service.Subscription;

public class StatefulServer extends Server implements IStatefulServer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(StatefulServer.class);

	private static SessionRegistry sessionRegistry = AppContext.getSessionRegistry();
	private static SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();
	/** The basic configuration. */
	protected BasicConfiguration basicConf = AppContext.getBasicConfiguration();
	/** The sessions, list of sessions allocated to the server. */
	private List<AbstractSession> sessions;
	/** The max sessions. */
	private int maxSessions;
	private StatefulService service;
	private String serviceName;
	private RemoteNodeConfiguration sasRemoteNodeConfiguration;

	public StatefulServer(RemoteNodeConfiguration remoteNodeConfiguration, String serviceName, InetSocketAddress socketAddress) {
		super(remoteNodeConfiguration, socketAddress);
		this.sessions = Collections.synchronizedList(new ArrayList<AbstractSession>());
		this.maxSessions = remoteNodeConfiguration.getMaxSessions();
		this.serverKey = serviceName + "_" + socketAddress.getHostName() + "/" + socketAddress.getPort();
		this.serviceName = serviceName;
		this.service = null;
		// set up separate remote node configuration for server session abort request in case of busy connection pool
		this.sasRemoteNodeConfiguration = new RemoteNodeConfiguration(ServerType.UNDEFINED, remoteNodeConfiguration.getName(),
				remoteNodeConfiguration.getHost(), remoteNodeConfiguration.getPort(), remoteNodeConfiguration.getConnectionType(),
				0, 1, 1);
	}

	/** {@inheritDoc} */
	@Override
	public void addSession(AbstractSession session) {
		this.sessions.add(session);
	}

	/** {@inheritDoc} */
	@Override
	public void removeSession(AbstractSession session) {
		if (this.sessions == null) {
			// might be the case if server got already destroyed
			return;
		}
		this.sessions.remove(session);
	}

	/** {@inheritDoc} */
	@Override
	public List<AbstractSession> getSessions() {
		return this.sessions;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasFreeSession() {
		return this.sessions.size() < this.maxSessions;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxSessions() {
		return this.maxSessions;
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return this.serviceName;
	}

	/**
	 * Creates the session.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @throws ConnectionPoolBusyException
	 * @throws Exception
	 *             the exception
	 */
	public void createSession(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvCreateSessionCall createSessionCall = new SCMPSrvCreateSessionCall(requester, msgToForward);
		try {
			createSessionCall.invoke(callback, timeoutMillis);
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// create session failed
			callback.receive(e);
		}
	}

	/**
	 * Delete session.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @throws ConnectionPoolBusyException
	 */
	public void deleteSession(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvDeleteSessionCall deleteSessionCall = new SCMPSrvDeleteSessionCall(requester, message);

		try {
			deleteSessionCall.invoke(callback, timeoutMillis);
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// delete session failed
			callback.receive(e);
		}
	}

	/**
	 * Subscribe.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @throws ConnectionPoolBusyException
	 */
	public void subscribe(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvSubscribeCall subscribeCall = new SCMPSrvSubscribeCall(requester, msgToForward);
		try {
			subscribeCall.invoke(callback, timeoutMillis);
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// subscribe failed
			callback.receive(e);
		}
	}

	/**
	 * Unsubscribe.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @throws ConnectionPoolBusyException
	 */
	public void unsubscribe(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvUnsubscribeCall unsubscribeCall = new SCMPSrvUnsubscribeCall(this.requester, message);

		try {
			unsubscribeCall.invoke(callback, timeoutMillis);
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// unsubscribe failed
			callback.receive(e);
		}
	}

	/**
	 * Change subscription.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 * @throws ConnectionPoolBusyException
	 */
	public void changeSubscription(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvChangeSubscriptionCall changeSubscriptionCall = new SCMPSrvChangeSubscriptionCall(this.requester, message);

		try {
			changeSubscriptionCall.invoke(callback, timeoutMillis);
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// changeSubscription failed
			callback.receive(e);
		}
	}

	/**
	 * Send data. Tries sending data to server asynchronous.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @throws ConnectionPoolBusyException
	 */
	public void execute(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis) throws ConnectionPoolBusyException {
		SCMPSrvExecuteCall srvExecuteCall = new SCMPSrvExecuteCall(this.requester, message);
		try {
			srvExecuteCall.invoke(callback, timeoutMillis);
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception th) {
			// send data failed
			callback.receive(th);
		}
	}

	/**
	 * Server abort session.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @throws ConnectionPoolBusyException
	 */
	public void serverAbortSession(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		this.serverAbortSessionWithExtraRequester(this.requester, message, callback, timeoutMillis);
	}

	/**
	 * Server abort session with extra requester.
	 * 
	 * @param requester
	 *            the requester
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 * @throws ConnectionPoolBusyException
	 *             the connection pool busy exception
	 */
	void serverAbortSessionWithExtraRequester(Requester requester, SCMPMessage message, ISCMPMessageCallback callback,
			int timeoutMillis) throws ConnectionPoolBusyException {
		SCMPSrvAbortSessionCall srvAbortSessionCall = new SCMPSrvAbortSessionCall(this.requester, message);
		try {
			srvAbortSessionCall.invoke(callback, timeoutMillis);
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception th) {
			callback.receive(th);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void abortSession(AbstractSession session, String reason) {
		// delete session in global registries
		if (session instanceof Subscription) {
			StatefulServer.subscriptionRegistry.removeSubscription(session.getId());
			PublishMessageQueue<SCMPMessage> publishMessageQueue = ((PublishService) ((StatefulServer) session.getServer())
					.getService()).getMessageQueue();
			// unsubscribe subscription
			publishMessageQueue.unsubscribe(session.getId());
			// remove non referenced nodes
			publishMessageQueue.removeNonreferencedNodes();
		} else {
			StatefulServer.sessionRegistry.removeSession((Session) session);
		}
		// delete session on this server
		this.removeSession(session);

		if (session.isCascaded() == true) {
			// session is of type cascaded - do not forward to server
			return;
		}
		int oti = this.basicConf.getSrvAbortOTIMillis();
		// set up abort message
		SCMPMessage abortMessage = new SCMPMessage();
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, SCMPError.SESSION_ABORT.getErrorText(reason));
		abortMessage.setServiceName(this.getServiceName());
		abortMessage.setSessionId(session.getId());
		abortMessage.setHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, oti);
		this.abortSessionAndWaitMech(oti, abortMessage, reason);
	}

	public void abortSessionAndWaitMech(int oti, SCMPMessage abortMessage, String reason) {
		int tries = (int) ((oti * basicConf.getOperationTimeoutMultiplier()) / Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		int i = 0;
		CommandCallback callback = null;
		int otiOnServerMillis = 0;
		try {
			// Following loop implements the wait mechanism in case of a busy connection pool
			do {
				callback = new CommandCallback(true);
				try {
					otiOnServerMillis = oti - (i * Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
					this.serverAbortSession(abortMessage, callback, otiOnServerMillis);
					// no exception has been thrown - get out of wait loop
					break;
				} catch (ConnectionPoolBusyException ex) {
					LOGGER.warn("ConnectionPoolBusyException caught in wait mec of session abort");
					if (i >= (tries - 1)) {
						// only one loop outstanding - don't continue throw current exception
						LOGGER.warn(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + abortMessage.getServiceName()));
						SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION,
								"service=" + abortMessage.getServiceName());
						throw scmpCommandException;
					}
				}
				// sleep for a while and then try again
				Thread.sleep(Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
			} while (++i < tries);

			if (this.service.getType() == ServiceType.SESSION_SERVICE) {
				// session server - validate reply of server
				SCMPMessage reply = callback.getMessageSync(oti);
				if (reply.isFault()) {
					// error in server abort session - destroy server
					this.abortSessionsAndDestroy("Session abort failed, abort reason: " + reason);
				}
			}
		} catch (SCMPCommandException scmpCommandException) {
			LOGGER.warn("ConnectionPoolBusyException in aborting session wait mec");
			// ConnectionPoolBusyException after wait mec - try opening a new connection

			// RemoteNodeConfiguration remoteNodeConfiguration = this.requester.getRemoteNodeConfiguration();
			// set up a new requester to make the SAS - only 1 connection is allowed
			Requester sasRequester = new Requester(this.sasRemoteNodeConfiguration);
			try {
				this.serverAbortSessionWithExtraRequester(sasRequester, abortMessage, callback, oti);
			} catch (ConnectionPoolBusyException e) {
				sasRequester.destroy();
				LOGGER.warn("ConnectionPoolBusyException in aborting session wait mec over special connection");
				if (this.service.getType() == ServiceType.SESSION_SERVICE) {
					this.abortSessionsAndDestroy("Session abort over a new connection failed");
				}
				return;
			}
			sasRequester.destroy();
			if (this.service.getType() == ServiceType.SESSION_SERVICE) {
				// session server - validate reply of server
				SCMPMessage reply = callback.getMessageSync(oti);
				if (reply.isFault()) {
					LOGGER.warn("Fault in aborting session wait mec over special connection");
					// error in server abort session - destroy server
					this.abortSessionsAndDestroy("Session abort over a new connection failed");
				}
			}
		} catch (Exception e) {
			if (this.service.getType() == ServiceType.SESSION_SERVICE) {
				LOGGER.error("Exceptiont in aborting session wait mec over special connection", e);
				// session server - destroy server in case of an error
				this.abortSessionsAndDestroy("Session abort failed, abort reason: " + reason);
			}
		}
	}

	/**
	 * Abort sessions and destroy. All sessions are aborted and server gets destroyed.
	 */
	public void abortSessionsAndDestroy(String reason) {
		// deregister server from service
		this.getService().removeServer(this);
		// set up server abort session message
		SCMPMessage abortMessage = new SCMPMessage();
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, SCMPError.SESSION_ABORT.getErrorText(reason));
		abortMessage.setHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, AppContext.getBasicConfiguration().getSrvAbortOTIMillis());

		for (AbstractSession session : this.sessions) {
			// delete session in global registries
			if (session instanceof Subscription) {
				StatefulServer.subscriptionRegistry.removeSubscription(session.getId());
				if (session.getServer() == null) {
					// server already destroyed
					continue;
				}
				PublishMessageQueue<SCMPMessage> publishMessageQueue = ((PublishService) ((StatefulServer) session.getServer())
						.getService()).getMessageQueue();
				// unsubscribe subscription
				publishMessageQueue.unsubscribe(session.getId());
				// remove non referenced nodes
				publishMessageQueue.removeNonreferencedNodes();
			} else {
				StatefulServer.sessionRegistry.removeSession((Session) session);
			}
			if (session.isCascaded() == true) {
				// session is of type cascaded - do not forward to server
				return;
			}
			abortMessage.setSessionId(session.getId());
			abortMessage.setServiceName(this.getServiceName());
			try {
				this.serverAbortSession(abortMessage, new CommandCallback(false), AppContext.getBasicConfiguration()
						.getSrvAbortOTIMillis());
			} catch (ConnectionPoolBusyException e) {
				LOGGER.warn("aborting session failed because of busy connection pool");
			}
			SessionLogger.logAbortSession(this.getClass().getName(), abortMessage.getSessionId());
		}
		this.destroy();
		this.sessions = null;
	}

	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	public StatefulService getService() {
		return this.service;
	}

	/**
	 * Sets the service.
	 * 
	 * @param service
	 *            the new service
	 */
	public void setService(StatefulService service) {
		this.service = service;
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		super.destroy();
		this.sessions = null;
		this.service = null;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return super.getServerKey() + ":" + this.remoteNodeConfiguration.getPort() + " : " + maxSessions;
	}
}
