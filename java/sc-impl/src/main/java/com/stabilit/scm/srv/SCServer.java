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
package com.stabilit.scm.srv;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.common.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.CommunicatorConfig;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.service.ISC;
import com.stabilit.scm.common.service.ISCContext;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.srv.cmd.factory.impl.UnitServerCommandFactory;

/**
 * The Class SCServer. Basic class for any kind of a server which communicates with an SC.
 * 
 * @author JTraber
 */
public class SCServer implements ISCServer {

	/** The host of the SC. */
	private String scHost;
	/** The port of the SC. */
	private int scPort;
	/** The connection type, identifies low level component to use for communication (netty, nio). */
	private String conType;
	/** The context. */
	private SCServerContext context;

	// fields for register service
	/** The requester. */
	protected IRequester requester;
	/** The max sessions. */
	private int maxSessions;
	/** The connection pool. */
	private IConnectionPool connectionPool;
	/** The immediate connect. */
	private boolean immediateConnect;
	/** The keep alive interval. */
	private int keepAliveInterval;
	/** The running port. */
	private int runningPort;
	/** The srv service registry. */
	private SrvServiceRegistry srvServiceRegistry;
	/** The callback. */
	protected SrvServerCallback callback;
	/** The msg id. */
	private SCMPMessageId msgId;

	/**
	 * Instantiates a new sC server.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public SCServer(String host, int port) {
		this(host, port, Constants.DEFAULT_SERVER_CON, Constants.DEFAULT_KEEP_ALIVE_INTERVAL);
	}

	/**
	 * Instantiates a new sC server.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionType
	 *            the connection type
	 */
	public SCServer(String host, int port, String connectionType) {
		this(host, port, connectionType, Constants.DEFAULT_KEEP_ALIVE_INTERVAL);
	}

	/**
	 * Instantiates a new sC server.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionType
	 *            the connection type
	 * @param keepAliveInterval
	 *            the keep alive interval
	 */
	public SCServer(String host, int port, String connectionType, int keepAliveInterval) {
		this.scHost = host;
		this.scPort = port;
		this.conType = connectionType;

		// attributes for registerService
		this.maxSessions = Constants.DEFAULT_MAX_CONNECTIONS;
		this.immediateConnect = true;
		this.keepAliveInterval = 0;
		this.runningPort = 0;
		this.connectionPool = new ConnectionPool(this.scHost, this.scPort, this.conType, keepAliveInterval);
		// register service only needs one connection
		this.connectionPool.setMaxConnections(1);
		this.context = new SCServerContext();
		this.msgId = new SCMPMessageId();
		this.requester = new Requester(new RequesterContext(context.getConnectionPool(), this.msgId));
		this.srvServiceRegistry = SrvServiceRegistry.getCurrentInstance();
		this.callback = new SrvServerCallback();
	}

	/** {@inheritDoc} */
	@Override
	public ISCContext getContext() {
		return context;
	}

	/** {@inheritDoc} */
	@Override
	public String getConnectionType() {
		return conType;
	}

	/** {@inheritDoc} */
	@Override
	public String getHost() {
		return scHost;
	}

	/** {@inheritDoc} */
	@Override
	public int getPort() {
		return scPort;
	}

	/** {@inheritDoc} */
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
	public void registerService(String serviceName, ISCServerCallback scCallback) throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(this.requester, serviceName);

		registerServiceCall.setMaxSessions(this.maxSessions);
		registerServiceCall.setPortNumber(this.runningPort);
		if (this.immediateConnect) {
			registerServiceCall.setImmediateConnect(true);
		}
		registerServiceCall.setKeepAliveInterval(this.keepAliveInterval);
		registerServiceCall.invoke(this.callback);
		this.callback.getMessageSync();
		// creating srvService & adding to registry
		SrvService srvService = new SrvService(serviceName, scCallback);
		this.srvServiceRegistry.addSrvService(serviceName, srvService);
	}

	/** {@inheritDoc} */
	@Override
	public void deregisterService(String serviceName) throws Exception {
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(this.requester, serviceName);
		deRegisterServiceCall.invoke(this.callback);
		this.callback.getMessageSync();
		// remove srvService from registry
		this.srvServiceRegistry.removeSrvService(serviceName);
		// destroy the connection pool
		this.connectionPool.destroy();
	}

	/** {@inheritDoc} */
	@Override
	public void startServer(String host) throws Exception {
		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new UnitServerCommandFactory());
		}

		CommunicatorConfig respConfig = new CommunicatorConfig(SCServer.class.getSimpleName());
		respConfig.setConnectionType(this.conType);
		respConfig.setHost(host);
		respConfig.setPort(this.runningPort);
		respConfig.setKeepAliveInterval(this.keepAliveInterval);

		IResponder resp = new Responder(respConfig);
		try {
			resp.create();
			resp.runAsync();
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
	}

	/**
	 * The Class SCServerContext.
	 */
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

	/** {@inheritDoc} */
	@Override
	public void setImmediateConnect(boolean immediateConnect) {
		this.immediateConnect = immediateConnect;
	}

	/** {@inheritDoc} */
	@Override
	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	/** {@inheritDoc} */
	@Override
	public void setRunningPortNr(int runningPort) {
		this.runningPort = runningPort;
	}

	/** {@inheritDoc} */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.connectionPool.destroy();
	}

	/**
	 * The Class SrvServerCallback.
	 */
	protected class SrvServerCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
