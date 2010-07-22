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

import com.stabilit.scm.common.call.SCMPCallFactory;
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
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.sc.req.SCRequester;

/**
 * The Class Server.
 * 
 * @author JTraber
 */
public class Server {

	/** The host. */
	private String host;
	/** The port nr. */
	private int portNr;
	/** The service name. */
	private String serviceName;
	/** The service. */
	private Service service;
	/** The max sessions. */
	private int maxSessions;
	private int keepAliveInterval;
	/** The socket address. */
	private SocketAddress socketAddress;

	private IRequester requester;
	private IConnectionPool cp;

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
		this.serviceName = serviceName;
		this.socketAddress = socketAddress;
		this.portNr = portNr;
		this.keepAliveInterval = keepAliveInterval;
		this.maxSessions = maxSessions;
		ResponderRegistry responderRegistry = ResponderRegistry.getCurrentInstance();
		IResponder responder = responderRegistry.getCurrentResponder();
		ICommunicatorConfig respConfig = responder.getResponderConfig();
		String connectionType = respConfig.getConnectionType();
		int numberOfThreads = respConfig.getNumberOfThreads();
		this.host = socketAddress.getHostName();
		this.cp = new ConnectionPool(host, portNr, connectionType, keepAliveInterval, numberOfThreads);
		this.cp.setMaxConnections(maxSessions);
		this.requester = new SCRequester(new RequesterContext(this.cp));
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
		this.cp.setMinConnections(cp.getMaxConnections());
		this.cp.initMinConnections();
	}

	/**
	 * Creates the session.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @return the scmp message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void createSession(SCMPMessage msgToForward, ISCMPCallback callback) throws Exception {
		SCMPSrvCreateSessionCall createSessionCall = (SCMPSrvCreateSessionCall) SCMPCallFactory.SRV_CREATE_SESSION_CALL
				.newInstance(requester, msgToForward);
		try {
			createSessionCall.invoke(callback);
		} catch (Throwable e) {
			// create session failed
			callback.callback(e);
		}
	}

	public void subscribe(SCMPMessage msgToForward, ISCMPCallback callback) throws Exception {
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
	 * Delete session.
	 * 
	 * @param session
	 *            the session to delete
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public void deleteSession(SCMPMessage message, ISCMPCallback callback) throws SCServiceException {
		SCMPSrvDeleteSessionCall deleteSessionCall = (SCMPSrvDeleteSessionCall) SCMPCallFactory.SRV_DELETE_SESSION_CALL
				.newInstance(requester, message);

		try {
			deleteSessionCall.invoke(callback);
		} catch (Exception e) {
			// delete session failed
			throw new SCServiceException("deleteSession failed", e);
		}
	}

	public void unsubscribe(SCMPMessage message, ISCMPCallback callback) throws SCServiceException {
		SCMPSrvUnsubscribeCall unsubscribeCall = (SCMPSrvUnsubscribeCall) SCMPCallFactory.SRV_UNSUBSCRIBE_CALL
				.newInstance(requester, message);

		try {
			unsubscribeCall.invoke(callback);
		} catch (Exception e) {
			// unsubscribe failed
			throw new SCServiceException("unsubscribe failed", e);
		}
	}

	/**
	 * Send data. Tries sending data to server asynchronous.
	 * 
	 * @param message
	 *            the message
	 * @return the sCMP message
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public void sendData(SCMPMessage message, ISCMPCallback callback) {
		SCMPSrvDataCall srvDataCall = (SCMPSrvDataCall) SCMPCallFactory.SRV_DATA_CALL.newInstance(requester, message);
		try {
			srvDataCall.invoke(callback);
		} catch (Throwable th) {
			Exception ex = new SCServiceException("sendData failed", th);
			callback.callback(ex);
		}
		return;
	}

	/**
	 * Srv echo.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @return the scmp message
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public void srvEcho(SCMPMessage message, ISCMPCallback callback) throws SCServiceException {
		SCMPSrvEchoCall srvEchoCall = (SCMPSrvEchoCall) SCMPCallFactory.SRV_ECHO_CALL.newInstance(requester, message);
		try {
			srvEchoCall.invoke(callback);
		} catch (Throwable th) {
			callback.callback(th);
		}
	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		this.cp.destroy();
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
	 * Gets the port nr.
	 * 
	 * @return the port nr
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
		return this.cp.hasFreeConnections();
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public String toString() {
		return serviceName + "_" + socketAddress + " : " + portNr + " : " + maxSessions;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Service getService() {
		return service;
	}

	/**
	 * @return the keepAliveInterval
	 */
	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public class ServerContext implements IContext {
	}
}
