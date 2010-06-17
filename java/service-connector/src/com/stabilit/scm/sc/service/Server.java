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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnEchoCall;
import com.stabilit.scm.cln.call.SCMPClnSystemCall;
import com.stabilit.scm.cln.call.SCMPSrvCreateSessionCall;
import com.stabilit.scm.cln.call.SCMPSrvDataCall;
import com.stabilit.scm.cln.call.SCMPSrvDeleteSessionCall;
import com.stabilit.scm.cln.call.SCMPSrvEchoCall;
import com.stabilit.scm.cln.call.SCMPSrvSystemCall;
import com.stabilit.scm.common.conf.IRequesterConfigItem;
import com.stabilit.scm.common.conf.IResponderConfigItem;
import com.stabilit.scm.common.conf.RequesterConfig;
import com.stabilit.scm.common.ctx.IResponderContext;
import com.stabilit.scm.common.log.listener.ExceptionPoint;
import com.stabilit.scm.common.log.listener.RuntimePoint;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.registry.ResponderRegistry;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.MapBean;
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
	/** The socket address. */
	private SocketAddress socketAddress;
	/** The free requester list. */
	private List<IRequester> freeReqList;
	/** The occupied requester list. */
	private Map<String, IRequester> occupiedReqList;

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
	public Server(InetSocketAddress socketAddress, String serviceName, int portNr, int maxSessions) {
		this.service = null;
		this.serviceName = serviceName;
		this.socketAddress = socketAddress;
		this.portNr = portNr;
		this.maxSessions = maxSessions;
		ResponderRegistry responderRegistry = ResponderRegistry.getCurrentInstance();
		IResponderContext respContext = responderRegistry.getCurrentContext();
		IResponderConfigItem serverConfig = respContext.getResponder().getResponderConfig();
		// The connection key, identifies low level component to use (netty, nio)
		String connectionKey = serverConfig.getConnection();
		int numberOfThreads = serverConfig.getNumberOfThreads();
		this.host = socketAddress.getHostName();
		this.freeReqList = new ArrayList<IRequester>();
		this.occupiedReqList = new HashMap<String, IRequester>();

		IRequesterConfigItem config = new RequesterConfig().new RequesterConfigItem(this.host, this.portNr,
				connectionKey, numberOfThreads);
		// init list of requesters
		for (int i = 0; i < maxSessions; i++) {
			IRequester req = new SCRequester();
			req.setRequesterConfig(config);
			freeReqList.add(req);
		}
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
		// TODO what if second fails??
		for (IRequester req : freeReqList) {
			req.connect();
		}
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
		IRequester req = freeReqList.remove(0);

		SCMPSrvCreateSessionCall createSessionCall = (SCMPSrvCreateSessionCall) SCMPCallFactory.SRV_CREATE_SESSION_CALL
				.newInstance(req, msgToForward);
		SCMPMessage serverReply = null;
		try {
			serverReply = createSessionCall.invoke();
		} catch (Throwable e) {
			// create session failed - add requester to free list
			freeReqList.add(req);
			throw new SCServiceException("createSession failed", e);
		}
		Boolean rejectSessionFlag = serverReply.getHeaderBoolean(SCMPHeaderAttributeKey.REJECT_SESSION);
		if (Boolean.TRUE.equals(rejectSessionFlag)) {
			// server rejected session - throw exception with server errors
			// TODO what to do with requester when server rejects session
			SCSessionException e = new SCSessionException(SCMPError.SESSION_REJECTED, serverReply.getHeader());
			throw e;
		}
		occupiedReqList.put(msgToForward.getSessionId(), req);
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
		String sessionId = message.getSessionId();

		IRequester req = occupiedReqList.remove(sessionId);
		if (req == null) {
			RuntimePoint.getInstance().fireRuntime(this,
					"deleteSession not possible - req is null for sessionid: " + sessionId);
			throw new SCServiceException("deleteSession not possible - req is null for sessionid:");
		}
		SCMPSrvDeleteSessionCall deleteSessionCall = (SCMPSrvDeleteSessionCall) SCMPCallFactory.SRV_DELETE_SESSION_CALL
				.newInstance(req, message);

		try {
			deleteSessionCall.invoke();
		} catch (Exception e) {
			// delete session failed
			// TODO what to do with current requester
			throw new SCServiceException("deleteSession failed", e);
		}

		// delete session successful - add req to free list
		this.freeReqList.add(req);
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
		String sessionId = message.getSessionId();

		IRequester req = occupiedReqList.get(sessionId);
		if (req == null) {
			RuntimePoint.getInstance().fireRuntime(this,
					"sendData not possible - req is null for sessionid: " + sessionId);
			throw new SCServiceException("sendData not possible - req is null for sessionid:");
		}
		SCMPMessage serverReply = null;
		SCMPSrvDataCall srvDataCall = (SCMPSrvDataCall) SCMPCallFactory.SRV_DATA_CALL.newInstance(req, message);
		try {
			serverReply = srvDataCall.invoke();
		} catch (Exception e) {
			// TODO what to do with current requester
			throw new SCServiceException("sendData failed", e);
		}
		return serverReply;
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
		String sessionId = message.getSessionId();

		IRequester req = occupiedReqList.get(sessionId);
		if (req == null) {
			RuntimePoint.getInstance().fireRuntime(this,
					"srvEcho not possible - req is null for sessionid: " + sessionId);
			throw new SCServiceException("srvEcho not possible - req is null for sessionid:");
		}
		SCMPMessage serverReply = null;
		SCMPSrvEchoCall srvEchoCall = (SCMPSrvEchoCall) SCMPCallFactory.SRV_ECHO_CALL.newInstance(req, message);
		try {
			serverReply = srvEchoCall.invoke();
		} catch (Exception e) {
			// TODO what to do with current requester
			throw new SCServiceException("srvEcho failed", e);
		}
		return serverReply;
	}

	public SCMPMessage clnEcho(SCMPMessage message) throws SCServiceException {
		String sessionId = message.getSessionId();

		IRequester req = occupiedReqList.get(sessionId);
		if (req == null) {
			RuntimePoint.getInstance().fireRuntime(this,
					"srvEcho not possible - req is null for sessionid: " + sessionId);
			throw new SCServiceException("srvEcho not possible - req is null for sessionid:");
		}
		SCMPMessage serverReply = null;
		SCMPClnEchoCall clnEchoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(req, message);
		try {
			serverReply = clnEchoCall.invoke();
		} catch (Exception e) {
			// TODO what to do with current requester
			throw new SCServiceException("srvEcho failed", e);
		}
		return serverReply;
	}

	public SCMPMessage srvSystem(SCMPMessage message) throws SCServiceException {
		String sessionId = message.getSessionId();

		IRequester req = occupiedReqList.get(sessionId);
		if (req == null) {
			RuntimePoint.getInstance().fireRuntime(this,
					"srvSystem not possible - req is null for sessionid: " + sessionId);
			throw new SCServiceException("srvSystem not possible - req is null for sessionid:");
		}
		SCMPMessage serverReply = null;
		SCMPSrvSystemCall srvSystemCall = (SCMPSrvSystemCall) SCMPCallFactory.SRV_SYSTEM_CALL.newInstance(req, message);
		try {
			serverReply = srvSystemCall.invoke();
		} catch (Exception e) {
			// TODO what to do with current requester
			throw new SCServiceException("srvSystem failed", e);
		}
		return serverReply;
	}

	public SCMPMessage clnSystem(SCMPMessage message) throws SCServiceException {
		String sessionId = message.getSessionId();

		IRequester req = occupiedReqList.get(sessionId);
		if (req == null) {
			RuntimePoint.getInstance().fireRuntime(this,
					"clnSystem not possible - req is null for sessionid: " + sessionId);
			throw new SCServiceException("clnSystem not possible - req is null for sessionid:");
		}
		SCMPMessage serverReply = null;
		SCMPClnSystemCall clnSystemCall = (SCMPClnSystemCall) SCMPCallFactory.CLN_SYSTEM_CALL.newInstance(req, message);
		try {
			serverReply = clnSystemCall.invoke();
		} catch (Exception e) {
			// TODO what to do with current requester
			throw new SCServiceException("clnSystem failed", e);
		}
		return serverReply;
	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		for (IRequester req : freeReqList) {
			try {
				req.disconnect();
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
				continue;
			} finally {
				try {
					req.destroy();
				} catch (Exception e) {
					ExceptionPoint.getInstance().fireException(this, e);
					continue;
				}
			}
		}
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
		return this.freeReqList.size() > 0;
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
}
