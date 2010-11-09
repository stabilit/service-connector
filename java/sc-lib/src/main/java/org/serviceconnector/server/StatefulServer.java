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
package org.serviceconnector.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPSrvAbortSessionCall;
import org.serviceconnector.call.SCMPSrvChangeSubscriptionCall;
import org.serviceconnector.call.SCMPSrvCreateSessionCall;
import org.serviceconnector.call.SCMPSrvDeleteSessionCall;
import org.serviceconnector.call.SCMPSrvExecuteCall;
import org.serviceconnector.call.SCMPSrvSubscribeCall;
import org.serviceconnector.call.SCMPSrvUnsubscribeCall;
import org.serviceconnector.cmd.sc.CommandCallback;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.Session;
import org.serviceconnector.service.StatefulService;

public class StatefulServer extends Server {

	private SessionLogger sessionLogger = SessionLogger.getInstance();
	private static SessionRegistry sessionRegistry = AppContext.getCurrentContext().getSessionRegistry();
	private static SubscriptionRegistry subscriptionRegistry = AppContext.getCurrentContext().getSubscriptionRegistry();
	/** The sessions, list of sessions allocated to the server. */
	private List<AbstractSession> sessions;
	/** The max sessions. */
	private int maxSessions;
	private StatefulService service;

	public StatefulServer(InetSocketAddress socketAddress, String serviceName, int portNr, int maxSessions,
			int maxConnections, String connectionType, int keepAliveInterval, double operationTimeoutMultiplier) {
		super(ServerType.STATEFUL_SERVER, socketAddress, serviceName, portNr, maxConnections, connectionType,
				keepAliveInterval, operationTimeoutMultiplier);
		this.sessions = Collections.synchronizedList(new ArrayList<AbstractSession>());
		this.maxSessions = maxSessions;
		this.service = null;
	}

	/**
	 * Adds an allocated session to the server.
	 * 
	 * @param session
	 *            the session
	 */
	public void addSession(AbstractSession session) {
		this.sessions.add(session);
	}

	/**
	 * Removes an allocated session from the server.
	 * 
	 * @param session
	 *            the session
	 */
	public void removeSession(AbstractSession session) {
		this.sessions.remove(session);
	}

	/**
	 * Gets the sessions.
	 * 
	 * @return the sessions
	 */
	public List<AbstractSession> getSessions() {
		return this.sessions;
	}

	/**
	 * Checks for free session.
	 * 
	 * @return true, if successful
	 */
	public boolean hasFreeSession() {
		return this.sessions.size() < this.maxSessions;
	}

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions
	 */
	public int getMaxSessions() {
		return this.maxSessions;
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
	public void createSession(SCMPMessage msgToForward, ISCMPCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvCreateSessionCall createSessionCall = (SCMPSrvCreateSessionCall) SCMPCallFactory.SRV_CREATE_SESSION_CALL
				.newInstance(requester, msgToForward);
		try {
			createSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// create session failed
			callback.callback(e);
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
	public void deleteSession(SCMPMessage message, ISCMPCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvDeleteSessionCall deleteSessionCall = (SCMPSrvDeleteSessionCall) SCMPCallFactory.SRV_DELETE_SESSION_CALL
				.newInstance(requester, message);

		try {
			deleteSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// delete session failed
			callback.callback(e);
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
	public void subscribe(SCMPMessage msgToForward, ISCMPCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvSubscribeCall subscribeCall = (SCMPSrvSubscribeCall) SCMPCallFactory.SRV_SUBSCRIBE_CALL.newInstance(
				requester, msgToForward);
		try {
			subscribeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// subscribe failed
			callback.callback(e);
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
	public void unsubscribe(SCMPMessage message, ISCMPCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvUnsubscribeCall unsubscribeCall = (SCMPSrvUnsubscribeCall) SCMPCallFactory.SRV_UNSUBSCRIBE_CALL
				.newInstance(requester, message);

		try {
			unsubscribeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// unsubscribe failed
			callback.callback(e);
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
	public void changeSubscription(SCMPMessage message, ISCMPCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvChangeSubscriptionCall changeSubscriptionCall = (SCMPSrvChangeSubscriptionCall) SCMPCallFactory.SRV_CHANGE_SUBSCRIPTION_CALL
				.newInstance(requester, message);

		try {
			changeSubscriptionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// changeSubscription failed
			callback.callback(e);
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
	public void execute(SCMPMessage message, ISCMPCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvExecuteCall srvExecuteCall = (SCMPSrvExecuteCall) SCMPCallFactory.SRV_EXECUTE_CALL.newInstance(
				requester, message);
		try {
			srvExecuteCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception th) {
			// send data failed
			callback.callback(th);
		}
	}

	/**
	 * Server abort session.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 */
	public void serverAbortSession(SCMPMessage message, ISCMPCallback callback, int timeoutMillis) {
		SCMPSrvAbortSessionCall srvAbortSessionCall = (SCMPSrvAbortSessionCall) SCMPCallFactory.SRV_ABORT_SESSION
				.newInstance(this.requester, message);
		try {
			srvAbortSessionCall.invoke(callback, timeoutMillis);
		} catch (Exception th) {
			callback.callback(th);
		}
	}

	/**
	 * Abort session.
	 * 
	 * @param abortMessage
	 *            the abort message
	 */
	public void abortSession(AbstractSession session) {
		// delete session in global registries
		StatefulServer.sessionRegistry.removeSession(session.getId());
		StatefulServer.subscriptionRegistry.removeSubscription(session.getId());
		// delete session on this server
		this.removeSession(session);
		CommandCallback callback = new CommandCallback(true);
		try {
			// no need for forwarding message id
			SCMPMessage abortMessage = new SCMPMessage();
			abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
			abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, SCMPError.SESSION_ABORT.getErrorText()
					+ " [delete session failed]");
			abortMessage.setServiceName(this.getServiceName());
			abortMessage.setSessionId(session.getId());
			this.serverAbortSession(abortMessage, callback, Constants.SERVER_ABORT_OTI_MILLIS);
		} catch (Exception e) {
			// server session abort failed - clean up server
			this.abortSessionsAndDestroy();
			return;
		}
		SCMPMessage reply = callback.getMessageSync();
		if (reply.isFault()) {
			// error in server abort session operation
			this.abortSessionsAndDestroy();
		}
	}

	/**
	 * Abort sessions and destroy. All sessions are aborted and server gets destroyed.
	 */
	public void abortSessionsAndDestroy() {
		// deregister server from service
		this.getService().removeServer(this);
		// set up server abort session message
		SCMPMessage abortMessage = new SCMPMessage();
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE, SCMPError.SESSION_ABORT.getErrorCode());
		abortMessage.setHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT, SCMPError.SESSION_ABORT.getErrorText()
				+ " [delete session failed]");

		for (AbstractSession session : this.sessions) {
			// delete session in global registries
			StatefulServer.sessionRegistry.removeSession((Session) session);
			StatefulServer.subscriptionRegistry.removeSubscription(session.getId());
			abortMessage.setSessionId(session.getId());
			abortMessage.setServiceName(this.getServiceName());
			this.serverAbortSession(abortMessage, new CommandCallback(false), Constants.SERVER_ABORT_OTI_MILLIS);
			sessionLogger.logAbortSession(this.getClass().getName(), abortMessage.getSessionId());
		}
		super.destroy();
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

	/** @{inheritDoc */
	@Override
	public void destroy() {
		super.destroy();
		this.sessions = null;
		this.service = null;
	}

	/** @{inheritDoc */
	@Override
	public String toString() {
		return super.getServerKey() + ":" + portNr + " : " + maxSessions;
	}
}
