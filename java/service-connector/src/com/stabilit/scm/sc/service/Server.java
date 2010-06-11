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
import java.util.List;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPSrvCreateSessionCall;
import com.stabilit.scm.common.conf.IResponderConfigItem;
import com.stabilit.scm.common.ctx.IResponderContext;
import com.stabilit.scm.common.log.listener.ExceptionPoint;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.RequesterFactory;
import com.stabilit.scm.common.registry.ResponderRegistry;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.MapBean;

/**
 * The Class Server.
 * 
 * @author JTraber
 */
public class Server extends MapBean<String> {

	/** The host. */
	private String host;
	/** The port nr. */
	private int portNr;
	/** The max sessions. */
	private int maxSessions;
	/** The socket address. */
	private SocketAddress socketAddress;
	/** The free requester list. */
	private List<IRequester> freeReqList;
	/** The occupied requester list. */
	private List<IRequester> occupiedReqList;

	/**
	 * Instantiates a new server.
	 * 
	 * @param socketAdress
	 *            the socket adress
	 * @param portNr
	 *            the port nr
	 * @param maxSessions
	 *            the max sessions
	 */
	public Server(InetSocketAddress socketAdress, int portNr, int maxSessions) {
		this.socketAddress = socketAdress;
		this.portNr = portNr;
		this.maxSessions = maxSessions;
		ResponderRegistry responderRegistry = ResponderRegistry.getCurrentInstance();
		IResponderContext respContext = responderRegistry.getCurrentContext();
		IResponderConfigItem serverConfig = respContext.getResponder().getResponderConfig();
		// The connection key, identifies low level component to use (netty, nio)
		String connectionKey = serverConfig.getConnection();
		int numberOfThreads = serverConfig.getNumberOfThreads();
		this.host = socketAdress.getHostName();
		this.freeReqList = new ArrayList<IRequester>();

		// init list of requesters
		RequesterFactory reqFactory = new RequesterFactory();
		IRequester req = null;
		for (int i = 0; i < maxSessions; i++) {
			req = reqFactory.newInstance(this.host, this.portNr, connectionKey, numberOfThreads);
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
		for (IRequester req : freeReqList) {
			req.connect();
		}
	}

	/**
	 * Creates the session.
	 * 
	 * @param msgToForward
	 *            the msg to forward
	 * @return the scmp message
	 * @throws Exception
	 *             the exception
	 */
	public SCMPMessage createSession(SCMPMessage msgToForward) throws Exception {
		IRequester req = freeReqList.remove(0);

		SCMPSrvCreateSessionCall createSessionCall = (SCMPSrvCreateSessionCall) SCMPCallFactory.SRV_CREATE_SESSION_CALL
				.newInstance(req);
		createSessionCall.setHeader(msgToForward.getHeader());
		SCMPMessage serverReply = null;
		try {
			serverReply = createSessionCall.invoke();
		} catch (Exception e) {
			// create session failed - add requester to free list
			freeReqList.add(req);
			throw e;
		}
		occupiedReqList.add(req);
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
		return this.freeReqList.size() < this.maxSessions;
	}

	@Override
	public String toString() {
		return socketAddress + " : " + portNr + " : " + maxSessions;
	}
}
