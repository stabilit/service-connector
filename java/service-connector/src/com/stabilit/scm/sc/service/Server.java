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

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnEchoCall;
import com.stabilit.scm.cln.call.SCMPClnSystemCall;
import com.stabilit.scm.cln.call.SCMPSrvCreateSessionCall;
import com.stabilit.scm.cln.call.SCMPSrvDataCall;
import com.stabilit.scm.cln.call.SCMPSrvDeleteSessionCall;
import com.stabilit.scm.cln.call.SCMPSrvEchoCall;
import com.stabilit.scm.cln.call.SCMPSrvSubscribeCall;
import com.stabilit.scm.cln.call.SCMPSrvSystemCall;
import com.stabilit.scm.cln.call.SCMPSrvUnsubscribeCall;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.registry.ResponderRegistry;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
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
	private IContext serverContext;
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

		this.serverContext = new ServerContext();
		this.cp = new ConnectionPool(host, portNr, connectionType, keepAliveInterval, numberOfThreads);
		this.cp.setMaxConnections(maxSessions);
		this.requester = new SCRequester(this.serverContext);
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
	public synchronized void createSession(SCMPMessage msgToForward) throws Exception {
		SCMPSrvCreateSessionCall createSessionCall = (SCMPSrvCreateSessionCall) SCMPCallFactory.SRV_CREATE_SESSION_CALL
				.newInstance(requester, msgToForward);
		SCMPMessage serverReply = null;
		try {
			serverReply = createSessionCall.invoke();
		} catch (Throwable e) {
			// create session failed
			throw new SCServiceException("createSession failed", e);
		}
		Boolean rejectSessionFlag = serverReply.getHeaderBoolean(SCMPHeaderAttributeKey.REJECT_SESSION);
		if (Boolean.TRUE.equals(rejectSessionFlag)) {
			// server rejected session - throw exception with server errors
			SCSessionException e = new SCSessionException(SCMPError.SESSION_REJECTED, serverReply.getHeader());
			throw e;
		}
	}

	public void subscribe(SCMPMessage msgToForward) throws Exception {
		SCMPSrvSubscribeCall subscribeCall = (SCMPSrvSubscribeCall) SCMPCallFactory.SRV_SUBSCRIBE_CALL.newInstance(
				requester, msgToForward);
		SCMPMessage serverReply = null;
		try {
			serverReply = subscribeCall.invoke();
		} catch (Throwable e) {
			// subscribe failed
			throw new SCServiceException("subscribe failed", e);
		}

		// TODO reject flag throw exception
	}

	/**
	 * Delete session.
	 * 
	 * @param session
	 *            the session to delete
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public void deleteSession(SCMPMessage message) throws SCServiceException {
		SCMPSrvDeleteSessionCall deleteSessionCall = (SCMPSrvDeleteSessionCall) SCMPCallFactory.SRV_DELETE_SESSION_CALL
				.newInstance(requester, message);

		try {
			deleteSessionCall.invoke();
		} catch (Exception e) {
			// delete session failed
			throw new SCServiceException("deleteSession failed", e);
		}
	}

	public void unsubscribe(SCMPMessage message) throws SCServiceException {
		SCMPSrvUnsubscribeCall unsubscribeCall = (SCMPSrvUnsubscribeCall) SCMPCallFactory.SRV_UNSUBSCRIBE_CALL
				.newInstance(requester, message);

		try {
			unsubscribeCall.invoke();
		} catch (Exception e) {
			// unsubscribe failed
			throw new SCServiceException("unsubscribe failed", e);
		}
	}

	/**
	 * Send data. Tries sending data to server.
	 * 
	 * @param message
	 *            the message
	 * @return the sCMP message
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public SCMPMessage sendData(SCMPMessage message) throws SCServiceException {
		SCMPMessage serverReply = null;
		SCMPSrvDataCall srvDataCall = (SCMPSrvDataCall) SCMPCallFactory.SRV_DATA_CALL.newInstance(requester, message);
		try {
			serverReply = srvDataCall.invoke();
		} catch (Exception e) {
			throw new SCServiceException("sendData failed", e);
		}
		return serverReply;
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
		} catch (Exception e) {
			Exception ex = new SCServiceException("sendData failed", e);
			callback.callback(ex);
		}
		return;
	}

	/**
	 * Srv echo.
	 * 
	 * @param message
	 *            the message
	 * @return the scmp message
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public SCMPMessage srvEcho(SCMPMessage message) throws SCServiceException {
		SCMPMessage serverReply = null;
		SCMPSrvEchoCall srvEchoCall = (SCMPSrvEchoCall) SCMPCallFactory.SRV_ECHO_CALL.newInstance(requester, message);
		try {
			serverReply = srvEchoCall.invoke();
		} catch (Exception e) {
			throw new SCServiceException("srvEcho failed", e);
		}
		return serverReply;
	}

	public SCMPMessage clnEcho(SCMPMessage message) throws SCServiceException {
		SCMPMessage serverReply = null;
		SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(requester, message);
		try {
			serverReply = clnEchoCall.invoke();
		} catch (Exception e) {
			throw new SCServiceException("srvEcho failed", e);
		}
		return serverReply;
	}

	public SCMPMessage srvSystem(SCMPMessage message) throws SCServiceException {
		SCMPMessage serverReply = null;
		SCMPSrvSystemCall srvSystemCall = (SCMPSrvSystemCall) SCMPCallFactory.SRV_SYSTEM_CALL.newInstance(requester,
				message);
		try {
			serverReply = srvSystemCall.invoke();
		} catch (Exception e) {
			throw new SCServiceException("srvSystem failed", e);
		}
		return serverReply;
	}

	public SCMPMessage clnSystem(SCMPMessage message) throws SCServiceException {
		SCMPMessage serverReply = null;
		SCMPClnSystemCall clnSystemCall = (SCMPClnSystemCall) SCMPCallFactory.CLN_SYSTEM_CALL.newInstance(requester,
				message);
		try {
			serverReply = clnSystemCall.invoke();
		} catch (Exception e) {
			throw new SCServiceException("clnSystem failed", e);
		}
		return serverReply;
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

		@Override
		public IConnectionPool getConnectionPool() {
			return Server.this.cp;
		}
	}
}
