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
package com.stabilit.scm.common.service;

import java.net.InetAddress;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.service.ISCActionListener;
import com.stabilit.scm.common.conf.CommunicatorConfig;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.common.util.MapBean;
import com.stabilit.scm.srv.service.ISessionServiceConnector;

/**
 * The Class ServiceConnector. Represents the service connector on client side. Provides functions to create sessions to
 * a server and to connect/disconnect to an SC. This component is responsible to observe the availability of the SC. If
 * the SC gets unreachable every open session has to be deleted.
 * 
 * @author JTraber
 */
class SessionServerServiceConnector implements ISessionServiceConnector {

	/** The port of the local session server. */
	public int sessionServerPort;
	/** The number of threads to use on session server side. */
	public int numberOfThreadsSessionServer;
	/** The connection key, identifies low level component to use for communication (netty, nio). */
	public String connectionKeySessionServer;
	/** The host of the SC. */
	public String scHost;
	/** The port of the SC. */
	public int scPort;
	/** The number of threads to use on client side. */
	public int numberOfThreadsClientToSC;
	/** The connection key, identifies low level component to use for communication (netty, nio). */
	public String connectionKeyClientToSC;
	/** The requester. */
	private IRequester requester; // becomes a pool later
	/** The attributes. */
	private MapBean<Object> attributes;

	/**
	 * Instantiates a new service connector.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public SessionServerServiceConnector(String host, int port) {
		this.scHost = host;
		this.scPort = port;
		this.connectionKeyClientToSC = "netty.tcp"; // default is netty tcp
		this.numberOfThreadsClientToSC = 16; // default is 16 threads
		this.attributes = new MapBean<Object>();

		this.connectionKeySessionServer = "netty.tcp";
		this.numberOfThreadsSessionServer = 16;
		this.sessionServerPort = 0;
	}

	/**
	 * Connect to SC. With this connect observing the SC starts.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void connect() throws Exception {
		// TODO start server
		requester = new Requester();
		ICommunicatorConfig config = new CommunicatorConfig("Session-Server", this.scHost, this.scPort,
				this.connectionKeyClientToSC, this.numberOfThreadsClientToSC);
		requester.setRequesterConfig(config);
		requester.connect();
		// sets up the attach call
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(requester);

		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);
		// attaches client
		attachCall.invoke();
	}

	/**
	 * Disconnect from SC. Every open session needs to be closed.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void disconnect() throws Exception {
		// detach
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(requester);
		detachCall.invoke();

		this.requester.disconnect(); // physical disconnect
		this.requester.destroy();
	}

	/**
	 * Sets the attribute.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	@Override
	public void setAttribute(String name, Object value) {
		this.attributes.setAttribute(name, value);
	}

	public int getNumberOfThreads() {
		return numberOfThreadsClientToSC;
	}

	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreadsClientToSC = numberOfThreads;
	}

	public String getConnectionKey() {
		return connectionKeyClientToSC;
	}

	public void setConnectionKey(String connectionKey) {
		this.connectionKeyClientToSC = connectionKey;
	}

	public String getHost() {
		return scHost;
	}

	public int getPort() {
		return scPort;
	}

	@Override
	public void addActionListener(ISCActionListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createServer(int portSessionServer) throws Exception {
		this.createServer(portSessionServer, this.connectionKeySessionServer);
	}

	@Override
	public void createServer(int portSessionServer, String connectionKeySessionServer) throws Exception {
		this.createServer(portSessionServer, connectionKeySessionServer, this.numberOfThreadsSessionServer);
	}

	@Override
	public void createServer(int portSessionServer, String connectionKeySessionServer, int numberOfThreadsSessionServer)
			throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		ICommunicatorConfig respConfig = new CommunicatorConfig("Session-Server", localHost.getHostAddress(),
				portSessionServer, connectionKeySessionServer, numberOfThreadsSessionServer);
		IResponder responder = new Responder(respConfig);
		responder.create();
		responder.runAsync();
	}
}
