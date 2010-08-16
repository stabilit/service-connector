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
package com.stabilit.scm.sc.service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPSrvAbortSessionCall;
import com.stabilit.scm.common.call.SCMPSrvChangeSubscriptionCall;
import com.stabilit.scm.common.call.SCMPSrvCreateSessionCall;
import com.stabilit.scm.common.call.SCMPSrvDataCall;
import com.stabilit.scm.common.call.SCMPSrvDeleteSessionCall;
import com.stabilit.scm.common.call.SCMPSrvEchoCall;
import com.stabilit.scm.common.call.SCMPSrvSubscribeCall;
import com.stabilit.scm.common.call.SCMPSrvUnsubscribeCall;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.net.res.ResponderRegistry;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.sc.req.SCRequester;

/**
 * The Class Server. Represents a server instance on a backend Server. Serves a service. Has control over the max of
 * sessions and holds a connection pool to communicate to backend server.
 * 
 * @author JTraber
 */
public class Server {

	/** The host. */
	private String host;
	/** The port number. */
	private int portNr;
	/** The socket address. */
	private InetSocketAddress socketAddress;
	/** The service name. */
	private String serviceName;
	/** The service. */
	private Service service;
	/** The max sessions. */
	private int maxSessions;
	/** The requester. */
	private IRequester requester;
	/** The connectionPool. */
	private IConnectionPool cp;
	/** The sessions, list of sessions allocated to the server. */
	private List<Session> sessions;

	/**
	 * Instantiates a new server.
	 * 
	 * @param socketAddress
	 *            the socket address
	 * @param portNr
	 *            the port number
	 * @param maxSessions
	 *            the max sessions
	 */
	public Server(InetSocketAddress socketAddress, String serviceName, int portNr, int maxSessions,
			int keepAliveInterval) {
		this.service = null;
		this.sessions = Collections.synchronizedList(new ArrayList<Session>());
		this.serviceName = serviceName;
		this.socketAddress = socketAddress;
		this.portNr = portNr;
		this.maxSessions = maxSessions;
		ResponderRegistry responderRegistry = ResponderRegistry.getCurrentInstance();
		IResponder responder = responderRegistry.getCurrentResponder();
		ICommunicatorConfig respConfig = responder.getResponderConfig();
		String connectionType = respConfig.getConnectionType();
		this.host = socketAddress.getHostName();
		this.cp = new ConnectionPool(host, portNr, connectionType, keepAliveInterval);
		this.cp.setMaxConnections(maxSessions);
		this.requester = new SCRequester(new RequesterContext(this.cp, null));
	}

	/**
	 * Gets the socket address.
	 * 
	 * @return the socket address
	 */
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	/**
	 * Immediate connect.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void immediateConnect() throws Exception {
		// set minimum connections to max for initial process
		this.cp.setMinConnections(cp.getMaxConnections());
		this.cp.initMinConnections();
		// initial done - set it back to 0
		this.cp.setMinConnections(0);
	}

	/**
	 * Creates the session.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @throws Exception
	 *             the exception
	 */
	public void createSession(SCMPMessage msgToForward, ISCMPCallback callback) {
		SCMPSrvCreateSessionCall createSessionCall = (SCMPSrvCreateSessionCall) SCMPCallFactory.SRV_CREATE_SESSION_CALL
				.newInstance(requester, msgToForward);
		try {
			createSessionCall.invoke(callback);
		} catch (Throwable e) {
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
	 */
	public void deleteSession(SCMPMessage message, ISCMPCallback callback) {
		SCMPSrvDeleteSessionCall deleteSessionCall = (SCMPSrvDeleteSessionCall) SCMPCallFactory.SRV_DELETE_SESSION_CALL
				.newInstance(requester, message);

		try {
			deleteSessionCall.invoke(callback);
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
	 */
	public void subscribe(SCMPMessage msgToForward, ISCMPCallback callback) {
		SCMPSrvSubscribeCall subscribeCall = (SCMPSrvSubscribeCall) SCMPCallFactory.SRV_SUBSCRIBE_CALL.newInstance(
				requester, msgToForward);
		try {
			subscribeCall.invoke(callback);
		} catch (Throwable e) {
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
	 */
	public void unsubscribe(SCMPMessage message, ISCMPCallback callback) {
		SCMPSrvUnsubscribeCall unsubscribeCall = (SCMPSrvUnsubscribeCall) SCMPCallFactory.SRV_UNSUBSCRIBE_CALL
				.newInstance(requester, message);

		try {
			unsubscribeCall.invoke(callback);
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
	 */
	public void changeSubscription(SCMPMessage message, ISCMPCallback callback) {
		SCMPSrvChangeSubscriptionCall changeSubscriptionCall = (SCMPSrvChangeSubscriptionCall) SCMPCallFactory.SRV_CHANGE_SUBSCRIPTION_CALL
				.newInstance(requester, message);

		try {
			changeSubscriptionCall.invoke(callback);
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
	 */
	public void sendData(SCMPMessage message, ISCMPCallback callback) {
		SCMPSrvDataCall srvDataCall = (SCMPSrvDataCall) SCMPCallFactory.SRV_DATA_CALL.newInstance(requester, message);
		try {
			srvDataCall.invoke(callback);
		} catch (Throwable th) {
			// send data failed
			callback.callback(th);
		}
	}

	/**
	 * Server echo.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 */
	public void serverEcho(SCMPMessage message, ISCMPCallback callback) {
		SCMPSrvEchoCall srvEchoCall = (SCMPSrvEchoCall) SCMPCallFactory.SRV_ECHO_CALL.newInstance(requester, message);
		try {
			srvEchoCall.invoke(callback);
		} catch (Throwable th) {
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
	public void serverAbortSession(SCMPMessage message, ISCMPCallback callback) {
		SCMPSrvAbortSessionCall srvAbortSessionCall = (SCMPSrvAbortSessionCall) SCMPCallFactory.SRV_ABORT_SESSION
				.newInstance(requester, message);
		try {
			srvAbortSessionCall.invoke(callback);
		} catch (Throwable th) {
			callback.callback(th);
		}
	}

	/**
	 * Destroy server.
	 */
	public void destroy() {
		this.cp.destroy();
		this.requester = null;
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Gets the port number.
	 * 
	 * @return the port number
	 */
	public int getPortNr() {
		return portNr;
	}

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions
	 */
	public int getMaxSessions() {
		return maxSessions;
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
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the service which is served by the server.
	 * 
	 * @param service
	 *            the new service
	 */
	public void setService(Service service) {
		this.service = service;
	}

	/**
	 * Gets the service which is served by the server.
	 * 
	 * @return the service
	 */
	public Service getService() {
		return service;
	}

	/**
	 * Adds an allocated session to the server.
	 * 
	 * @param session
	 *            the session
	 */
	public void addSession(Session session) {
		this.sessions.add(session);
	}

	/**
	 * Removes an allocated session from the server.
	 * 
	 * @param session
	 *            the session
	 */
	public void removeSession(Session session) {
		this.sessions.remove(session);
	}

	/**
	 * Gets the sessions.
	 * 
	 * @return the sessions
	 */
	public List<Session> getSessions() {
		return sessions;
	}

	public class ServerContext implements IContext {
	}

	@Override
	public String toString() {
		return serviceName + "_" + socketAddress.getHostName() + "/" + socketAddress.getPort() + ":" + portNr + " : "
				+ maxSessions;
	}
}
