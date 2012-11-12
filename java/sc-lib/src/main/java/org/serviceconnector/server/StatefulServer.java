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
import java.util.Set;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPSrvAbortSessionCall;
import org.serviceconnector.call.SCMPSrvAbortSubscriptionCall;
import org.serviceconnector.call.SCMPSrvChangeSubscriptionCall;
import org.serviceconnector.call.SCMPSrvCreateSessionCall;
import org.serviceconnector.call.SCMPSrvDeleteSessionCall;
import org.serviceconnector.call.SCMPSrvExecuteCall;
import org.serviceconnector.call.SCMPSrvSubscribeCall;
import org.serviceconnector.call.SCMPSrvUnsubscribeCall;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.sc.CommandCallback;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPVersion;
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.ServiceType;
import org.serviceconnector.service.Session;
import org.serviceconnector.service.StatefulService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class StatefulServer.
 */
public class StatefulServer extends Server implements IStatefulServer {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(StatefulServer.class);

	/** List of sessions and subscriptions allocated to the server. */
	private List<AbstractSession> sessions;
	/** The max sessions. */
	private int maxSessions;
	/** The service. */
	private StatefulService service;
	/** The service name. */
	private String serviceName;
	/** The remote node configuration for SRV_SBORT_SESSION request . */
	private RemoteNodeConfiguration sasRemoteNodeConfiguration;

	/**
	 * Instantiates a new stateful server.
	 * 
	 * @param remoteNodeConfiguration
	 *            the remote node configuration
	 * @param serviceName
	 *            the service name
	 * @param socketAddress
	 *            the socket address
	 */
	public StatefulServer(RemoteNodeConfiguration remoteNodeConfiguration, String serviceName, InetSocketAddress socketAddress) {
		super(remoteNodeConfiguration, socketAddress);
		this.sessions = Collections.synchronizedList(new ArrayList<AbstractSession>());
		this.maxSessions = remoteNodeConfiguration.getMaxSessions();
		this.serverKey = serviceName + Constants.UNDERLINE + socketAddress.getHostName() + Constants.SLASH
				+ socketAddress.getPort();
		this.serviceName = serviceName;
		this.service = null;
		// set up separate remote node configuration for SRV_ABORT_SESSION request in case of busy connection pool
		this.sasRemoteNodeConfiguration = new RemoteNodeConfiguration(ServerType.UNDEFINED, remoteNodeConfiguration.getName(),
				remoteNodeConfiguration.getHost(), remoteNodeConfiguration.getPort(), remoteNodeConfiguration.getConnectionType(),
				0, 0, 1, 1, remoteNodeConfiguration.getHttpUrlFileQualifier());
		// calculate server timeout: multiply check registration interval with checkRegistrationIntervalMultiplier!
		this.serverTimeoutMillis = (remoteNodeConfiguration.getCheckRegistrationIntervalSeconds()
				* Constants.SEC_TO_MILLISEC_FACTOR * AppContext.getBasicConfiguration().getCheckRegistrationIntervalMultiplier());
	}

	/**
	 * Checks for free session.
	 * 
	 * @return true, if successful {@inheritDoc}
	 */
	@Override
	public boolean hasFreeSession() {
		if (this.destroyed == true) {
			// server already destroyed no sessions available
			return false;
		}
		return this.sessions.size() < this.maxSessions;
	}

	/**
	 * Adds the session.
	 * 
	 * @param session
	 *            the session {@inheritDoc}
	 */
	@Override
	public void addSession(AbstractSession session) {
		this.sessions.add(session);
	}

	/**
	 * Removes the session.
	 * 
	 * @param session
	 *            the session {@inheritDoc}
	 */
	@Override
	public void removeSession(AbstractSession session) {
		if (this.sessions == null || this.destroyed == true) {
			// might be the case if server got already destroyed
			return;
		}
		this.sessions.remove(session);
		this.service.notifyRemovedSession();
	}

	/**
	 * Gets the sessions.
	 * 
	 * @return the sessions {@inheritDoc}
	 */
	@Override
	public List<AbstractSession> getSessions() {
		return this.sessions;
	}

	/** {@inheritDoc} */
	@Override
	public int getSessionCount() {
		return this.sessions.size();
	}

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions {@inheritDoc}
	 */
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
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout millis
	 * @throws ConnectionPoolBusyException
	 *             the connection pool busy exception
	 */
	public void createSession(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		// setting the http url file qualifier which is necessary to communicate with the server.
		msgToForward.setHttpUrlFileQualifier(this.remoteNodeConfiguration.getHttpUrlFileQualifier());
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
	 * @param timeoutMillis
	 *            the timeout millis
	 * @throws ConnectionPoolBusyException
	 *             the connection pool busy exception
	 */
	public void deleteSession(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		// setting the http url file qualifier which is necessary to communicate with the server.
		message.setHttpUrlFileQualifier(this.remoteNodeConfiguration.getHttpUrlFileQualifier());
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
	 * @param timeoutMillis
	 *            the timeout millis
	 * @throws ConnectionPoolBusyException
	 *             the connection pool busy exception
	 */
	public void subscribe(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		// setting the http url file qualifier which is necessary to communicate with the server.
		msgToForward.setHttpUrlFileQualifier(this.remoteNodeConfiguration.getHttpUrlFileQualifier());
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
	 * @param timeoutMillis
	 *            the timeout millis
	 * @throws ConnectionPoolBusyException
	 *             the connection pool busy exception
	 */
	public void unsubscribe(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		// setting the http url file qualifier which is necessary to communicate with the server.
		message.setHttpUrlFileQualifier(this.remoteNodeConfiguration.getHttpUrlFileQualifier());
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
	 *             the connection pool busy exception
	 */
	public void changeSubscription(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		// setting the http url file qualifier which is necessary to communicate with the server.
		message.setHttpUrlFileQualifier(this.remoteNodeConfiguration.getHttpUrlFileQualifier());
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
	 * @param timeoutMillis
	 *            the timeout millis
	 * @throws ConnectionPoolBusyException
	 *             the connection pool busy exception
	 */
	public void execute(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis) throws ConnectionPoolBusyException {
		// setting the http url file qualifier which is necessary to communicate with the server.
		message.setHttpUrlFileQualifier(this.remoteNodeConfiguration.getHttpUrlFileQualifier());
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
	 * @param timeoutMillis
	 *            the timeout millis
	 * @throws ConnectionPoolBusyException
	 *             the connection pool busy exception
	 */
	private void serverAbortSession(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
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
	private void serverAbortSessionWithExtraRequester(Requester requester, SCMPMessage message, ISCMPMessageCallback callback,
			int timeoutMillis) throws ConnectionPoolBusyException {
		// setting the http url file qualifier which is necessary to communicate with the server.
		message.setHttpUrlFileQualifier(this.remoteNodeConfiguration.getHttpUrlFileQualifier());
		SCMPSrvAbortSessionCall srvAbortSessionCall = new SCMPSrvAbortSessionCall(this.requester, message);
		try {
			srvAbortSessionCall.invoke(callback, timeoutMillis);
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception th) {
			callback.receive(th);
		}
	}

	/**
	 * Server abort subscription.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout millis
	 * @throws ConnectionPoolBusyException
	 *             the connection pool busy exception
	 */
	private void serverAbortSubscription(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		this.serverAbortSubscriptionWithRequester(this.requester, message, callback, timeoutMillis);
	}

	/**
	 * Server abort subscription with requester.
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
	private void serverAbortSubscriptionWithRequester(Requester requester, SCMPMessage message, ISCMPMessageCallback callback,
			int timeoutMillis) throws ConnectionPoolBusyException {
		// setting the http url file qualifier which is necessary to communicate with the server.
		message.setHttpUrlFileQualifier(this.remoteNodeConfiguration.getHttpUrlFileQualifier());
		SCMPSrvAbortSubscriptionCall srvAbortSubscriptionCall = new SCMPSrvAbortSubscriptionCall(this.requester, message);
		try {
			srvAbortSubscriptionCall.invoke(callback, timeoutMillis);
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception th) {
			callback.receive(th);
		}
	}

	/**
	 * Abort session.
	 * 
	 * @param session
	 *            the session
	 * @param reason
	 *            the reason {@inheritDoc}
	 */
	@Override
	public void abortSession(AbstractSession session, String reason) {
		synchronized (this) {
			if (this.destroyed == true) {
				// server got already destroyed - no need to continue.
				return;
			}
			// delete session in global registries
			if (session instanceof Subscription) {
				AppContext.getSubscriptionRegistry().removeSubscription(session.getId());
				PublishMessageQueue<SCMPMessage> publishMessageQueue = ((PublishService) ((StatefulServer) session.getServer())
						.getService()).getMessageQueue();
				// unsubscribe subscription
				publishMessageQueue.unsubscribe(session.getId());
				// remove non referenced nodes
				publishMessageQueue.removeNonreferencedNodes();
				SubscriptionLogger.logAbortSubscription((Subscription) session, reason);
			} else {
				AppContext.getSessionRegistry().removeSession((Session) session);
				SessionLogger.logAbortSession((Session) session, reason);
			}
		}
		// delete session on this server
		this.removeSession(session);

		int oti = AppContext.getBasicConfiguration().getSrvAbortOTIMillis();
		// set up abort message - SCMP Version current
		SCMPMessage abortMessage = new SCMPMessage(SCMPVersion.LOWEST);
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, SCMPError.SESSION_ABORT.getErrorText(reason));
		abortMessage.setServiceName(this.getServiceName());
		abortMessage.setHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, oti);

		if (session.isCascaded() == true) {
			Subscription subscription = ((Subscription) session);
			// XAB procedure for casc subscriptions
			Set<String> subscriptionIds = subscription.getCscSubscriptionIds().keySet();

			for (String id : subscriptionIds) {
				abortMessage.setSessionId(id);
				this.abortSessionAndWaitMech(oti, abortMessage, reason, true);
			}
			subscription.getCscSubscriptionIds().clear();
			// subscription is of type cascaded - do not forward to server
			return;
		}

		abortMessage.setSessionId(session.getId());
		if (session instanceof Subscription) {
			this.abortSessionAndWaitMech(oti, abortMessage, reason, true);
		} else {
			this.abortSessionAndWaitMech(oti, abortMessage, reason, false);
		}
	}

	/**
	 * Abort session and wait mech.
	 * 
	 * @param oti
	 *            the oti
	 * @param abortMessage
	 *            the abort message
	 * @param reason
	 *            the reason
	 * @param abortSubscription
	 *            the abort subscription
	 */
	public void abortSessionAndWaitMech(int oti, SCMPMessage abortMessage, String reason, boolean abortSubscription) {
		if (this.destroyed == true) {
			// server got already destroyed - no need to continue.
			return;
		}
		int tries = (int) ((oti * AppContext.getBasicConfiguration().getOperationTimeoutMultiplier()) / Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
		int i = 0;
		CommandCallback callback = null;
		int otiOnServerMillis = 0;
		try {
			// Following loop implements the wait mechanism in case of a busy connection pool
			do {
				callback = new CommandCallback(true);
				try {
					otiOnServerMillis = oti - (i * Constants.WAIT_FOR_FREE_CONNECTION_INTERVAL_MILLIS);
					if (abortSubscription == true) {
						this.serverAbortSubscription(abortMessage, callback, otiOnServerMillis);
					} else {
						this.serverAbortSession(abortMessage, callback, otiOnServerMillis);
					}
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

			// validate reply of server
			SCMPMessage reply = callback.getMessageSync(oti);
			if (reply.isFault()) {
				// error in server abort session - destroy server
				this.abortSessionsAndDestroy("Session abort failed, abort reason: " + reason);
			}
		} catch (SCMPCommandException scmpCommandException) {
			LOGGER.warn("ConnectionPoolBusyException in aborting session wait mec " + scmpCommandException.toString());
			// ConnectionPoolBusyException after wait mec - try opening a new connection

			// RemoteNodeConfiguration remoteNodeConfiguration = this.requester.getRemoteNodeConfiguration();
			// set up a new requester to make the SAS - only 1 connection is allowed
			Requester sasRequester = new Requester(this.sasRemoteNodeConfiguration);
			try {
				this.serverAbortSessionWithExtraRequester(sasRequester, abortMessage, callback, oti);
			} catch (ConnectionPoolBusyException e) {
				sasRequester.destroy();
				LOGGER.warn("ConnectionPoolBusyException in aborting session wait mec over special connection. " + e.toString());
				if (this.service.getType() == ServiceType.SESSION_SERVICE) {
					this.abortSessionsAndDestroy("Session abort over a new connection failed");
				}
				return;
			}
			sasRequester.destroy();
			// validate reply of server
			SCMPMessage reply = callback.getMessageSync(oti);
			if (reply.isFault()) {
				LOGGER.warn("Fault in aborting session wait mec over special connection");
				// error in server abort session - destroy server
				this.abortSessionsAndDestroy("Session abort over a new connection failed");
			}
		} catch (Exception e) {
			LOGGER.error("Exceptiont in aborting session wait mec over special connection", e);
			// session server - destroy server in case of an error
			this.abortSessionsAndDestroy("Session abort failed, abort reason: " + reason);
		}
	}

	/**
	 * Abort sessions and destroy. All sessions are aborted and server gets destroyed.
	 * 
	 * @param reason
	 *            the reason
	 */
	public synchronized void abortSessionsAndDestroy(String reason) {
		if (this.destroyed == true) {
			// server got already destroyed - no need to continue.
			return;
		}
		this.destroyed = true;
		// deregister server from service
		this.getService().removeServer(this);
		AbstractSession[] sessionsArr = this.sessions.toArray(new AbstractSession[0]);

		for (AbstractSession session : sessionsArr) {
			// first of all delete sessions from registries
			AppContext.getSubscriptionRegistry().removeSubscription(session.getId());
			AppContext.getSessionRegistry().removeSession(session.getId());
		}
		// set up server abort session message - SCMP Version current
		SCMPMessage abortMessage = new SCMPMessage(SCMPVersion.LOWEST);
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, SCMPError.SESSION_ABORT.getErrorText(reason));
		abortMessage.setHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, AppContext.getBasicConfiguration().getSrvAbortOTIMillis());

		for (AbstractSession session : sessionsArr) {
			abortMessage.setSessionId(session.getId());
			abortMessage.setServiceName(this.getServiceName());
			// delete session in global registries
			if (session instanceof Subscription) {
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
				SubscriptionLogger.logAbortSubscription((Subscription) session, reason);

				if (session.isCascaded() == true) {
					Subscription subscription = ((Subscription) session);
					// XAB procedure for casc subscriptions
					Set<String> subscriptionIds = subscription.getCscSubscriptionIds().keySet();

					for (String id : subscriptionIds) {
						abortMessage.setSessionId(id);
						try {
							this.serverAbortSubscription(abortMessage, new CommandCallback(false), AppContext
									.getBasicConfiguration().getSrvAbortOTIMillis());
						} catch (ConnectionPoolBusyException e) {
							LOGGER.warn("aborting subscription failed because of busy connection pool. " + e.toString());
						} catch (Exception e) {
							LOGGER.warn("aborting subscription failed. " + e.toString());
						}
					}
					subscription.getCscSubscriptionIds().clear();
					// subscription is of type cascaded - do not forward to server
					continue;
				}
				try {
					this.serverAbortSubscription(abortMessage, new CommandCallback(false), AppContext.getBasicConfiguration()
							.getSrvAbortOTIMillis());
				} catch (ConnectionPoolBusyException e) {
					LOGGER.warn("aborting subscription failed because of busy connection pool. " + e.toString());
				} catch (Exception e) {
					LOGGER.warn("aborting subscription failed. " + e.toString());
				}
			} else {
				SessionLogger.logAbortSession((Session) session, reason);
				try {
					this.serverAbortSession(abortMessage, new CommandCallback(false), AppContext.getBasicConfiguration()
							.getSrvAbortOTIMillis());
				} catch (ConnectionPoolBusyException e) {
					LOGGER.warn("aborting session failed because of busy connection pool. " + e.toString());
				} catch (Exception e) {
					LOGGER.warn("aborting session failed. " + e.toString());
				}
			}
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

	/**
	 * Destroy. {@inheritDoc}
	 */
	@Override
	public void destroy() {
		super.destroy();
		this.service.notifyRemovedSession();
		this.sessions = null;
		this.service = null;
	}

	/**
	 * To string.
	 * 
	 * @return the string {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.getServerKey() + ":" + this.remoteNodeConfiguration.getPort() + " : " + maxSessions;
	}

	/**
	 * Dump the server into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("stateful-server");
		writer.writeAttribute("key", this.serverKey);
		writer.writeAttribute("serviceName", this.serviceName);
		writer.writeAttribute("maxSessions", this.maxSessions);
		writer.writeAttribute("socketAddress", this.socketAddress.getHostName() + Constants.SLASH + this.socketAddress.getPort());
		writer.writeAttribute("operationTimeoutMultiplier", this.operationTimeoutMultiplier);
		this.requester.dump(writer);
		writer.writeStartElement("sessionIds");
		AbstractSession[] sessionArr = this.sessions.toArray(new AbstractSession[0]);
		for (AbstractSession session : sessionArr) {
			writer.writeElement("sid", session.getId());
		}
		writer.writeEndElement(); // end of sessionIds
		writer.writeEndElement(); // end of stateful-server
	}
}
