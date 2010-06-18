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
import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.srv.service.ISessionServiceConnector;

/**
 * The Class SessionServerServiceConnector.
 * 
 * @author JTraber
 */
class SessionServerServiceConnector extends ServiceConnector implements ISessionServiceConnector {

	/** The number of threads to use on session server side. */
	private int numberOfThreadsSessionServer;
	/** The connection key, identifies low level component to use for communication (netty, nio). */
	private String connectionKeySessionServer;

	public SessionServerServiceConnector(String host, int port) {
		super(host, port, IConstants.DEFAULT_SERVER_CON, IConstants.DEFAULT_NR_OF_THREADS);

		this.connectionKeySessionServer = IConstants.DEFAULT_SERVER_CON;
		this.numberOfThreadsSessionServer = IConstants.DEFAULT_NR_OF_THREADS;
	}

	@Override
	public void connect() throws Exception {
		super.connect();
		// sets up the attach call
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);

		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);
		// attaches client
		attachCall.invoke();
	}

	@Override
	public void disconnect() throws Exception {
		// detach
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(this.requester);
		detachCall.invoke();

		super.disconnect();
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
