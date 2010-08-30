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

import org.apache.log4j.Logger;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.common.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.CommunicatorConfig;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.log.IExceptionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.net.res.Responder;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.srv.cmd.factory.impl.UnitServerCommandFactory;

/**
 * The Class SCServer. Basic class for any kind of a server which communicates with an SC.
 * 
 * @author JTraber
 */
public class SCServer implements ISCServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCServer.class);

	/** Identifies low level component to use for communication default for severs is {netty.tcp}. */
	private String conType;
	/** The srv service registry. */
	protected SrvServiceRegistry srvServiceRegistry;
	/** The message id. */
	private SCMPMessageId msgId;
	/** The server listening state. */
	private boolean listening;
	/** The responder. */
	private IResponder responder;
	// fields for register service
	protected ISCMPSynchronousCallback callback;
	/** The immediate connect. */
	private boolean immediateConnect;
	/** The keep alive interval. */
	private int keepAliveIntervalInSeconds;
	/** The local server host. */
	private String localServerHost;
	/** The local server port. */
	private int localServerPort;

	public SCServer() {
		this.listening = false;
		this.conType = Constants.DEFAULT_SERVER_CON;
		// attributes for registerService
		this.immediateConnect = true;
		this.keepAliveIntervalInSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;
		this.localServerHost = null;
		this.localServerPort = -1;
		this.responder = null;
		this.msgId = new SCMPMessageId();
		this.srvServiceRegistry = SrvServiceRegistry.getCurrentInstance();
		this.callback = new SrvServerCallback();

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new UnitServerCommandFactory());
		}
	}

	/** {@inheritDoc} */
	@Override
	public int getKeepAliveIntervalInSeconds() {
		return this.keepAliveIntervalInSeconds;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxSessions(String serviceName) {
		return this.srvServiceRegistry.getSrvService(serviceName).getMaxSessions();
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxConnections(String serviceName) {
		return this.srvServiceRegistry.getSrvService(serviceName).getMaxConnections();
	}

	/** {@inheritDoc} */
	@Override
	public void registerService(String scHost, int scPort, String serviceName, int maxSessions, int maxConnections,
			ISCServerCallback scCallback) throws Exception {
		if (this.listening == false) {
			throw new InvalidActivityException("listener should first be started before register service is allowed.");
		}
		if (scHost == null) {
			throw new InvalidParameterException("host must be set.");
		}
		ValidatorUtility.validateInt(0, scPort, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
		ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateInt(1, maxSessions, SCMPError.HV_WRONG_MAX_SESSIONS);
		ValidatorUtility.validateInt(1, maxConnections, maxSessions, SCMPError.HV_WRONG_MAX_SESSIONS);
		if (scCallback == null) {
			throw new InvalidParameterException("callback must be set");
		}
		// register called first time - initialize connection pool & requester
		IConnectionPool connectionPool = new ConnectionPool(scHost, scPort, this.conType,
				this.keepAliveIntervalInSeconds);
		// register service only needs one connection
		connectionPool.setMaxConnections(1);
		IRequester requester = new Requester(new RequesterContext(connectionPool, this.msgId));

		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(requester, serviceName);

		registerServiceCall.setMaxSessions(maxSessions);
		registerServiceCall.setMaxConnections(maxConnections);
		registerServiceCall.setPortNumber(this.localServerPort);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(this.keepAliveIntervalInSeconds);
		try {
			registerServiceCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
		} catch (Exception e) {
			connectionPool.destroy();
			throw new SCServiceException("register service failed", e);
		}
		SCMPMessage reply = this.callback.getMessageSync();
		if (reply.isFault()) {
			connectionPool.destroy();
			throw new SCServiceException("register service failed : "
					+ reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
		// creating srvService & adding to registry
		SrvService srvService = new SrvService(serviceName, maxSessions, maxConnections, requester, scCallback);
		this.srvServiceRegistry.addSrvService(serviceName, srvService);
	}

	/** {@inheritDoc} */
	@Override
	public void deregisterService(String serviceName) throws Exception {
		if (this.srvServiceRegistry.containsKey(serviceName) == false) {
			// sc server not registered - deregister not necessary
			return;
		}
		IRequester req = null;
		try {
			// remove srvService from registry
			SrvService srvService = this.srvServiceRegistry.removeSrvService(serviceName);
			req = srvService.getRequester();
			SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
					.newInstance(req, serviceName);
			try {
				deRegisterServiceCall.invoke(this.callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
			} catch (Exception e) {
				throw new SCServiceException("deregister service failed", e);
			}
			SCMPMessage reply = this.callback.getMessageSync();
			if (reply.isFault()) {
				throw new SCServiceException("deregister service failed : "
						+ reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			}
		} finally {
			// destroy connection pool
			req.getContext().getConnectionPool().destroy();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void startListener(String host, int port, int keepAliveIntervalInSeconds) throws Exception {
		if (this.listening == true) {
			throw new InvalidActivityException("listener is already started not allowed to start again.");
		}
		CommunicatorConfig respConfig = new CommunicatorConfig(SCServer.class.getSimpleName());
		respConfig.setConnectionType(this.conType);

		if (host == null) {
			throw new InvalidParameterException("host must be set.");
		}
		ValidatorUtility.validateInt(0, port, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
		ValidatorUtility.validateInt(0, keepAliveIntervalInSeconds, 3600, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);

		this.keepAliveIntervalInSeconds = keepAliveIntervalInSeconds;
		this.localServerHost = host;
		this.localServerPort = port;
		respConfig.setHost(host);
		respConfig.setPort(port);

		responder = new Responder(respConfig);
		try {
			responder.create();
			responder.startListenAsync();
		} catch (Exception ex) {
			IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
			exceptionLogger.logErrorException(logger, this.getClass().getName(), "startListener", ex);
			return;
		}
		this.listening = true;
	}

	/** {@inheritDoc} */
	@Override
	public void destroyServer() {
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
	public boolean isRegistered(String serviceName) {
		return this.srvServiceRegistry.containsKey(serviceName);
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
	public String getSCHost(String serviceName) {
		return this.srvServiceRegistry.getSrvService(serviceName).getRequester().getContext().getConnectionPool()
				.getHost();
	}

	/** {@inheritDoc} */
	@Override
	public int getSCPort(String serviceName) {
		return this.srvServiceRegistry.getSrvService(serviceName).getRequester().getContext().getConnectionPool()
				.getPort();
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
	 * The Class SrvServerCallback.
	 */
	protected class SrvServerCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
