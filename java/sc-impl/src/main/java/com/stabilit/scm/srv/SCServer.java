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

import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.common.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.CommunicatorConfig;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.req.ConnectionFactory;
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
	/** Identifies low level component to use for communication default for severs is {netty.tcp}. */
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
	private int keepAliveIntervalInSeconds;
	/** The local server host. */
	private String localServerHost;
	/** The local server port. */
	private int localServerPort;
	/** The srv service registry. */
	private SrvServiceRegistry srvServiceRegistry;
	/** The callback. */
	protected SrvServerCallback callback;
	/** The message id. */
	private SCMPMessageId msgId;
	/** The server started state. */
	private boolean serverStarted;

	public SCServer() {
		this.serverStarted = false;
		this.scHost = null;
		this.scPort = -1;
		this.conType = Constants.DEFAULT_SERVER_CON;
		// attributes for registerService
		this.immediateConnect = true;
		this.keepAliveIntervalInSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;
		this.localServerHost = null;
		this.localServerPort = -1;
		this.maxSessions = Constants.DEFAULT_MAX_CONNECTIONS;
		this.context = new SCServerContext();
		this.msgId = new SCMPMessageId();
		this.connectionPool = null;
		this.srvServiceRegistry = SrvServiceRegistry.getCurrentInstance();
		this.callback = new SrvServerCallback();

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new UnitServerCommandFactory());
		}
	}

	/** {@inheritDoc} */
	@Override
	public ISCContext getContext() {
		return context;
	}

	/** {@inheritDoc} */
	@Override
	public String getHost() {
		return this.scHost;
	}

	/** {@inheritDoc} */
	@Override
	public int getPort() {
		return this.scPort;
	}

	/** {@inheritDoc} */
	@Override
	public int getKeepAliveIntervalInSeconds() {
		return this.keepAliveIntervalInSeconds;
	}

	/** {@inheritDoc} */
	@Override
	public void setMaxSessions(int maxSessions) {
		if (maxSessions < 1) {
			throw new InvalidParameterException("Max sessions must be greater than 0.");
		}
		this.maxSessions = maxSessions;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxSessions() {
		return maxSessions;
	}

	/** {@inheritDoc} */
	@Override
	public void registerService(String scHost, int scPort, String serviceName, ISCServerCallback scCallback)
			throws Exception {
		if (this.serverStarted == false) {
			throw new InvalidActivityException("Start server has to be called before register service is allowed.");
		}
		if (serviceName == null) {
			throw new InvalidParameterException("Service name must be set");
		}
		if (scPort < 1 || scPort > 0xFFFF) {
			throw new InvalidParameterException("Port is not within 1 and 0xFFFF.");
		}
		if (scHost == null) {
			throw new InvalidParameterException("Host must be set.");
		}
		if (scCallback == null) {
			throw new InvalidParameterException("Callback must be set");
		}
		this.scHost = scHost;
		this.scPort = scPort;
		this.connectionPool = new ConnectionPool(this.scHost, this.scPort, this.conType,
				this.keepAliveIntervalInSeconds);
		// register service only needs one connection
		this.connectionPool.setMaxConnections(1);
		this.requester = new Requester(new RequesterContext(context.getConnectionPool(), this.msgId));

		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(this.requester, serviceName);

		registerServiceCall.setMaxSessions(this.maxSessions);
		registerServiceCall.setPortNumber(this.localServerPort);
		if (this.immediateConnect) {
			registerServiceCall.setImmediateConnect(true);
		}
		registerServiceCall.setKeepAliveInterval(this.keepAliveIntervalInSeconds);
		registerServiceCall.invoke(this.callback);
		this.callback.getMessageSync();
		// creating srvService & adding to registry
		SrvService srvService = new SrvService(serviceName, scCallback);
		this.srvServiceRegistry.addSrvService(serviceName, srvService);
	}

	/** {@inheritDoc} */
	@Override
	public void deregisterService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("Service name must be set");
		}
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
	public void startServer(String host, int port, int keepAliveIntervalInSeconds) throws Exception {
		CommunicatorConfig respConfig = new CommunicatorConfig(SCServer.class.getSimpleName());
		respConfig.setConnectionType(this.conType);

		if (port < 1 || port > 0xFFFF) {
			throw new InvalidParameterException("Port is not within 1 and 0xFFFF.");
		}
		if (keepAliveIntervalInSeconds < 0 || keepAliveIntervalInSeconds > 3600) {
			throw new InvalidParameterException("Keep alive interval is not within 0 and 3600.");
		}
		if (host == null) {
			throw new InvalidParameterException("Host must be set.");
		}
		this.localServerHost = host;
		this.localServerPort = port;
		this.keepAliveIntervalInSeconds = keepAliveIntervalInSeconds;
		respConfig.setHost(host);
		respConfig.setPort(port);
		respConfig.setKeepAliveInterval(keepAliveIntervalInSeconds);

		IResponder resp = new Responder(respConfig);
		try {
			resp.create();
			resp.runAsync();
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
		this.serverStarted = true;
	}

	/** {@inheritDoc} */
	@Override
	public void startServer(String host, int port) throws Exception {
		this.startServer(host, port, Constants.DEFAULT_KEEP_ALIVE_INTERVAL);
	}

	/** {@inheritDoc} */
	@Override
	public void setImmediateConnect(boolean immediateConnect) {
		this.immediateConnect = immediateConnect;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isImmediateConnect() {
		return this.immediateConnect;
	}

	/** {@inheritDoc} */
	@Override
	public String getLocalServerHost() {
		return this.localServerHost;
	}

	/** {@inheritDoc} */
	@Override
	public int getLocalServerPort() {
		return this.localServerPort;
	}

	/**
	 * Gets the connection type. Default {netty.tcp}
	 * 
	 * @return the connection type in use
	 */
	@Override
	public String getConnectionType() {
		return this.conType;
	}

	/**
	 * Sets the connection type. Should only be used if you really need to change low level technology careful.
	 * 
	 * @param conType
	 *            the new connection type, identifies low level communication technology
	 */
	public void setConnectionType(String conType) {
		this.conType = conType;
	}

	/** {@inheritDoc} */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.connectionPool.destroy();
		// destroy connection resource
		ConnectionFactory.shutdownConnectionFactory();
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

	/**
	 * The Class SrvServerCallback.
	 */
	protected class SrvServerCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
