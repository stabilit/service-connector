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
package com.stabilit.scm.srv.ps;

import java.util.List;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.common.call.SCMPPublishCall;
import com.stabilit.scm.common.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.conf.ResponderConfigPool;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.srv.ISCPublishServer;
import com.stabilit.scm.srv.ISCPublishServerContext;
import com.stabilit.scm.srv.ps.cmd.factory.impl.PublishServerCommandFactory;

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
	private SCPublishServerCallback callback;

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
		this.requester = new Requester(new RequesterContext(context.getConnectionPool()));
		this.callback = new SCPublishServerCallback();
	}

	public SCPublishServer(String host, int port, String connectionType) {
		this(host, port, connectionType, IConstants.DEFAULT_KEEP_ALIVE_INTERVAL, IConstants.DEFAULT_NR_OF_THREADS);
	}

	@Override
	public void registerService() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
		.newInstance(this.requester, "publish-simulation");
		registerServiceCall.setMaxSessions(2);
		registerServiceCall.setPortNumber(14000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(0);
		registerServiceCall.invoke(this.callback);
		this.callback.getMessageSync(IConstants.OPERATION_TIMEOUT_MILLIS);
	}

	@Override
	public void deregisterService() throws Exception {
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(this.requester, "publish-simulation");

		deRegisterServiceCall.invoke(this.callback);
		this.callback.getMessageSync(IConstants.OPERATION_TIMEOUT_MILLIS);
	}

	@Override
	public void publish(String mask, Object data) throws Exception {
		SCMPPublishCall publishCall = (SCMPPublishCall) SCMPCallFactory.PUBLISH_CALL.newInstance(this.requester,
				"publish-simulation");
		publishCall.setRequestBody(data);
		publishCall.invoke(this.callback);
		this.callback.getMessageSync(IConstants.OPERATION_TIMEOUT_MILLIS);
	}

	@Override
	public void startServer(String fileName) throws Exception {
		ResponderConfigPool srvConfig = new ResponderConfigPool();
		srvConfig.load(fileName);
		RequesterConfigPool clientConfig = new RequesterConfigPool();
		clientConfig.load(fileName);

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new PublishServerCommandFactory());
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
	
	/** {@inheritDoc} */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.connectionPool.destroy();
	}

	private class SCPublishServerCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
