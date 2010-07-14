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

import java.util.List;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.conf.ResponderConfigPool;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.srv.rr.ISCServerCallback;
import com.stabilit.scm.srv.rr.cmd.factory.impl.SessionServerCommandFactory;

public class SCServer implements ISCServer {

	/** The host of the SC. */
	private String host;
	/** The port of the SC. */
	private int port;
	/** The connection type, identifies low level component to use for communication (netty, nio). */
	private String conType;
	/** The connection pool. */
	private IConnectionPool connectionPool;
	/** The number of threads to use on client side. */
	private int numberOfThreads;
	private int maxSessions;
	/** The requester. */
	protected IRequester requester;
	/** The context. */
	private SCServerContext context;

	public SCServer(String host, int port) {
		this(host, port, IConstants.DEFAULT_SERVER_CON, IConstants.DEFAULT_KEEP_ALIVE_INTERVAL,
				IConstants.DEFAULT_NR_OF_THREADS);
	}

	public SCServer(String host, int port, String connectionType) {
		this(host, port, connectionType, IConstants.DEFAULT_KEEP_ALIVE_INTERVAL, IConstants.DEFAULT_NR_OF_THREADS);
	}

	public SCServer(String host, int port, String connectionType, int keepAliveInterval) {
		this(host, port, connectionType, keepAliveInterval, IConstants.DEFAULT_NR_OF_THREADS);
	}

	public SCServer(String host, int port, String connectionType, int keepAliveInterval, int numberOfThreads) {
		this.host = host;
		this.port = port;
		this.conType = connectionType;
		this.maxSessions = IConstants.DEFAULT_MAX_CONNECTIONS;
		this.numberOfThreads = numberOfThreads;
		this.connectionPool = new ConnectionPool(this.host, this.port, this.conType, keepAliveInterval, numberOfThreads);
		// register service only needs one connection
		this.connectionPool.setMaxConnections(1);
		this.context = new SCServerContext();
	}

	/** {@inheritDoc} */
	@Override
	public ISCContext getContext() {
		return context;
	}

	/** {@inheritDoc} */
	@Override
	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	/** {@inheritDoc} */
	@Override
	public String getConnectionKey() {
		return conType;
	}

	/** {@inheritDoc} */
	@Override
	public String getHost() {
		return host;
	}

	/** {@inheritDoc} */
	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxSessions() {
		return maxSessions;
	}

	/** {@inheritDoc} */
	@Override
	public void registerService(String servicName, ISCServerCallback scCallback) throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(this.requester, servicName);

		registerServiceCall.setMaxSessions(this.maxSessions);
		registerServiceCall.setPortNumber(14000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(0);

		registerServiceCall.invoke();
	}

	/** {@inheritDoc} */
	@Override
	public void deregisterService(String serviceName) throws Exception {
	}

	public void startServer(String fileName) throws Exception {
		ResponderConfigPool srvConfig = new ResponderConfigPool();
		srvConfig.load(fileName);
		RequesterConfigPool clientConfig = new RequesterConfigPool();
		clientConfig.load(fileName);

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new SessionServerCommandFactory());
		}
		List<ICommunicatorConfig> respConfigList = srvConfig.getResponderConfigList();

		for (ICommunicatorConfig respConfig : respConfigList) {
			IResponder resp = new Responder(respConfig);
			try {
				resp.create();
				resp.runAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class SCServerContext implements ISCContext {

		/** {@inheritDoc} */
		@Override
		public IConnectionPool getConnectionPool() {
			return connectionPool;
		}

		/** {@inheritDoc} */
		@Override
		public ISC getServiceConnector() {
			return SCServer.this;
		}
	}
}
