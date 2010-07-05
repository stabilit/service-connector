/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.cln.call.SCMPPublishCall;
import com.stabilit.scm.cln.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;

public class SCPublishServer implements ISCPublishServer {

	/** The host of the SC. */
	private String host;
	/** The port of the SC. */
	private int port;
	/** The connection pool. */
	private IConnectionPool connectionPool;
	/** The number of threads to use on client side. */
	private int numberOfThreads;
	/** The connection type, identifies low level component to use for communication (netty, nio). */
	private String conType;
	/** The requester. */
	protected IRequester requester;
	/** The context. */
	private SCPublishServerContext context;

	/**
	 * Instantiates a new service connector.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public SCPublishServer(String host, int port) {
		this(host, port, IConstants.DEFAULT_CLIENT_CON, IConstants.DEFAULT_KEEP_ALIVE_INTERVAL,
				IConstants.DEFAULT_NR_OF_THREADS);
	}

	public SCPublishServer(String host, int port, String connectionType, int keepAliveInterval, int numberOfThreads) {
		this.host = host;
		this.port = port;
		this.conType = connectionType;
		this.numberOfThreads = numberOfThreads;
		this.connectionPool = new ConnectionPool(this.host, this.port, this.conType, keepAliveInterval, numberOfThreads);
		this.context = new SCPublishServerContext();
		this.requester = new Requester(this.context);
	}

	public SCPublishServer(String host, int port, String connectionType) {
		this(host, port, connectionType, IConstants.DEFAULT_KEEP_ALIVE_INTERVAL, IConstants.DEFAULT_NR_OF_THREADS);
	}

	@Override
	public void deregister() throws Exception {
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(this.requester, "simulation2");

		deRegisterServiceCall.invoke();
	}

	@Override
	public void publish(String mask, Object data) throws Exception {
		SCMPPublishCall publishCall = (SCMPPublishCall) SCMPCallFactory.PUBLISH_CALL.newInstance(this.requester,
				"simulation2");
		publishCall.invoke();
	}

	@Override
	public void register() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(this.requester, "simulation2");

		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(7000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);

		registerServiceCall.invoke();
	}

	class SCPublishServerContext implements ISCPublishServerContext {

		/** {@inheritDoc} */
		@Override
		public IConnectionPool getConnectionPool() {
			return connectionPool;
		}

		@Override
		public ISCPublishServer getSCPublishServer() {
			return SCPublishServer.this;
		}
	}
}
