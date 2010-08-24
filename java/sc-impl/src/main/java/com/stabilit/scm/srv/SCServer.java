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
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.service.ISC;
import com.stabilit.scm.common.service.ISCContext;
import com.stabilit.scm.common.service.SCServiceException;
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
	private String serviceName;
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
	/** The server listening state. */
	private boolean listening;
	/** The server register state. */
	private boolean registered;
	/** The responder. */
	private IResponder responder;

	public SCServer() {
		this.listening = false;
		this.scHost = null;
		this.scPort = -1;
		this.conType = Constants.DEFAULT_SERVER_CON;
		// attributes for registerService
		this.immediateConnect = true;
		this.keepAliveIntervalInSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;
		this.localServerHost = null;
		this.localServerPort = -1;
		this.serviceName = null;
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
	public int getKeepAliveIntervalInSeconds() {
		return this.keepAliveIntervalInSeconds;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxSessions() {
		return maxSessions;
	}

	/** {@inheritDoc} */
	@Override
	public void registerService(String scHost, int scPort, String serviceName, int keepAliveIntervalInSeconds,
			ISCServerCallback scCallback) throws Exception {
		if (this.listening == false) {
			throw new InvalidActivityException("Listener should first be started before register service is allowed.");
		}
		if (this.registered == true) {
			throw new SCServiceException(
					"already registered before - deregister first, registering in sequence is not allowed.");
		}
		if (scHost == null || scPort == -1) {
			throw new InvalidActivityException(
					"Host and port to SC must be configued by setters before calling register service.");
		}
		if (scPort < 0 || scPort > 0xFFFF) {
			throw new InvalidParameterException("Port is not within 0 and 0xFFFF.");
		}
		if (keepAliveIntervalInSeconds < 0 || keepAliveIntervalInSeconds > 3600) {
			throw new InvalidParameterException("Keep alive interval is not within 0 and 3600.");
		}
		if (serviceName == null) {
			throw new InvalidParameterException("Service name must be set");
		}
		if (scCallback == null) {
			throw new InvalidParameterException("Callback must be set");
		}
		this.keepAliveIntervalInSeconds = keepAliveIntervalInSeconds;
		this.serviceName = serviceName;
		this.scHost = scHost;
		this.scPort = scPort;
		// register called first time - initialize connection pool & requester
		this.connectionPool = new ConnectionPool(this.scHost, this.scPort, this.conType,
				this.keepAliveIntervalInSeconds);
		// register service only needs one connection
		this.connectionPool.setMaxConnections(1);
		this.requester = new Requester(new RequesterContext(context.getConnectionPool(), this.msgId));

		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(this.requester, serviceName);

		registerServiceCall.setMaxSessions(this.maxSessions);
		registerServiceCall.setPortNumber(this.localServerPort);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(this.keepAliveIntervalInSeconds);
		try {
			registerServiceCall.invoke(this.callback);
		} catch (Exception e) {
			this.connectionPool.destroy();
			throw new SCServiceException("register service failed", e);
		}
		SCMPMessage reply = this.callback.getMessageSync();
		if (reply.isFault()) {
			this.connectionPool.destroy();
			SCMPFault fault = (SCMPFault) reply;
			throw new SCServiceException("register service failed", fault.getCause());
		}
		// creating srvService & adding to registry
		SrvService srvService = new SrvService(serviceName, scCallback);
		this.srvServiceRegistry.addSrvService(serviceName, srvService);
		this.registered = true;
	}

	/** {@inheritDoc} */
	@Override
	public void deregisterService() throws Exception {
		try {
			if (this.registered == false) {
				// sc server not registered - deregister not necessary
				return;
			}
			// remove srvService from registry
			this.srvServiceRegistry.removeSrvService(this.serviceName);
			SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
					.newInstance(this.requester, this.serviceName);
			try {
				deRegisterServiceCall.invoke(this.callback);
			} catch (Exception e) {
				throw new SCServiceException("deregister service failed", e);
			}
			SCMPMessage reply = this.callback.getMessageSync();
			if (reply.isFault()) {
				SCMPFault fault = (SCMPFault) reply;
				throw new SCServiceException("deregister service failed", fault.getCause());
			}
		} finally {
			this.registered = false;
			if (this.connectionPool != null) {
				// destroy connection pool
				this.connectionPool.destroy();
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void startListener(String host, int port, int maxSessions) throws Exception {
		CommunicatorConfig respConfig = new CommunicatorConfig(SCServer.class.getSimpleName());
		respConfig.setConnectionType(this.conType);

		if (port < 0 || port > 0xFFFF) {
			throw new InvalidParameterException("Port is not within 0 and 0xFFFF.");
		}
		if (maxSessions < 1) {
			throw new InvalidParameterException("Max sessions must be greater than 0.");
		}
		if (host == null) {
			throw new InvalidParameterException("Host must be set.");
		}
		this.maxSessions = maxSessions;
		this.localServerHost = host;
		this.localServerPort = port;
		respConfig.setHost(host);
		respConfig.setPort(port);

		responder = new Responder(respConfig);
		try {
			responder.create();
			responder.startListenAsync();
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
			return;
		}
		this.listening = true;
	}

	/** {@inheritDoc} */
	@Override
	public void stopListening() {
		if (this.listening == false) {
			// server is not listening
			return;
		}
		this.listening = false;
		this.responder.stopListening();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isListening() {
		return this.listening;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRegistered() {
		return this.registered;
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
	public String getSCHost() {
		return this.scHost;
	}

	/** {@inheritDoc} */
	@Override
	public int getSCPort() {
		return this.scPort;
	}

	/** {@inheritDoc} */
	@Override
	public String getHost() {
		return this.localServerHost;
	}

	/** {@inheritDoc} */
	@Override
	public int getPort() {
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
