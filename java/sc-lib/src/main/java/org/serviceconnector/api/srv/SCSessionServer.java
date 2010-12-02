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
package org.serviceconnector.api.srv;

import java.security.InvalidParameterException;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.cmd.srv.ServerCommandFactory;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.SynchronousCallback;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SCServer. Basic class for any kind of a server which communicates with an SC.
 * 
 * @author JTraber
 */
public class SCSessionServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCSessionServer.class);

	/** Identifies low level component to use for communication default for severs is {netty.tcp}. */
	private String conType;
	/** The message sequence number. */
	private SCMPMessageSequenceNr msgSequenceNr;

	// fields for register server
	/** The immediate connect. */
	private boolean immediateConnect;
	/** The local server host. */
	private String localServerHost;
	/** The local server port. */
	private int localServerPort;
	private boolean registered;
	protected String serviceName;
	protected SCServerContext scServerContext;

	static {
		// Initialize server command factory one time
		AppContext.initCommands(new ServerCommandFactory());
	}

	/**
	 * Instantiates a new SCSessionServer.
	 */
	public SCSessionServer(SCServerContext scServerContext, String serviceName) {
		this.scServerContext = scServerContext;
		this.serviceName = serviceName;
		this.conType = ConnectionType.DEFAULT_SERVER_CONNECTION_TYPE.getValue();
		// attributes for registerServer
		this.immediateConnect = true;
		this.localServerHost = null;
		this.registered = false;
		this.localServerPort = -1;
		this.msgSequenceNr = new SCMPMessageSequenceNr();
	}

	/**
	 * Gets the keep alive interval in seconds.
	 * 
	 * @return the keep alive interval in seconds
	 */
	public int getKeepAliveIntervalInSeconds() {
		return this.scServerContext.getKeepAliveIntervalSeconds();
	}

	public int getMaxSessions() {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		return srvServiceRegistry.getSrvService(this.serviceName + "_" + this.scServerContext.getListenerPort()).getMaxSessions();
	}

	public int getMaxConnections() {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		return srvServiceRegistry.getSrvService(this.serviceName + "_" + this.scServerContext.getListenerPort()).getMaxConnections();
	}

	public synchronized void register(int maxSessions, int maxConnections, SCSessionServerCallback scCallback) throws Exception {
		this.register(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, maxSessions, maxConnections, scCallback);
	}

	public synchronized void register(int operationTimeoutSeconds, int maxSessions, int maxConnections,
			SCSessionServerCallback scCallback) throws Exception {
		if (scCallback == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "callback must be set");
		}
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		IRequester requester = this.doRegister(operationTimeoutSeconds, maxSessions, maxConnections);
		// creating srvService & adding to registry
		SrvService srvService = new SrvSessionService(this.serviceName, maxSessions, maxConnections, requester, scCallback);
		srvServiceRegistry.addSrvService(this.serviceName + "_" + this.scServerContext.getListenerPort(), srvService);
		this.registered = true;
	}

	public synchronized void register(int maxSessions, int maxConnections, SCPublishServerCallback scCallback) throws Exception {
		this.register(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, maxSessions, maxConnections, scCallback);
	}

	public synchronized void register(int operationTimeoutSeconds, int maxSessions, int maxConnections,
			SCPublishServerCallback scCallback) throws Exception {
		if (scCallback == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "callback must be set");
		}
		IRequester requester = this.doRegister(operationTimeoutSeconds, maxSessions, maxConnections);
		// creating srvService & adding to registry
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		SrvService srvService = new SrvPublishService(this.serviceName, maxSessions, maxConnections, requester, scCallback);
		srvServiceRegistry.addSrvService(this.serviceName + "_" + this.scServerContext.getListenerPort(), srvService);
		this.registered = true;
	}

	private synchronized IRequester doRegister(int operationTimeoutSeconds, int maxSessions, int maxConnections) throws Exception {
		if (this.scServerContext.isListening() == false) {
			throw new InvalidActivityException("listener should first be started before register service is allowed.");
		}
		if (this.registered) {
			throw new InvalidActivityException("Server already registered for a service.");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		AppContext.init();
		synchronized (AppContext.communicatorsLock) {
			this.msgSequenceNr.reset();
			int keepAliveIntervalSeconds = this.scServerContext.getKeepAliveIntervalSeconds();
			boolean immediateConnect = this.scServerContext.isImmediateConnect();
			ConnectionType connectionType = this.scServerContext.getConnectionType();
			int scPort = this.scServerContext.getSCPort();
			String scHost = this.scServerContext.getSCHost();
			int listenerPort = this.scServerContext.getListenerPort();

			if (scHost == null) {
				throw new InvalidParameterException("host must be set.");
			}
			ValidatorUtility.validateInt(0, scPort, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
			ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
			ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
			ValidatorUtility.validateInt(1, maxSessions, SCMPError.HV_WRONG_MAX_SESSIONS);
			ValidatorUtility.validateInt(1, maxConnections, 1024, SCMPError.HV_WRONG_MAX_SESSIONS);
			ValidatorUtility.validateInt(1, maxConnections, maxSessions, SCMPError.HV_WRONG_MAX_SESSIONS);

			// initialize connection pool & requester
			ConnectionPool connectionPool = new ConnectionPool(scHost, scPort, connectionType.getValue(), keepAliveIntervalSeconds);
			// register server only needs one connection
			connectionPool.setMaxConnections(1);
			IRequester requester = new SCRequester(new RequesterContext(connectionPool, this.msgSequenceNr));

			SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL.newInstance(
					requester, this.serviceName);

			registerServerCall.setMaxSessions(maxSessions);
			registerServerCall.setMaxConnections(maxConnections);
			registerServerCall.setPortNumber(listenerPort);
			registerServerCall.setImmediateConnect(immediateConnect);
			registerServerCall.setKeepAliveInterval(keepAliveIntervalSeconds);
			SCServerCallback callback = new SCServerCallback(true);
			try {
				registerServerCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				connectionPool.destroy();
				throw new SCServiceException("register server failed", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				connectionPool.destroy();
				SCServiceException ex = new SCServiceException("register server failed");
				ex.setAppErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				throw ex;
			}
			AppContext.attachedCommunicators.incrementAndGet();
			return requester;
		}
	}

	public synchronized void deregister() throws Exception {
		this.deregister(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	public synchronized void deregister(int operationTimeoutSeconds) throws Exception {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		if (this.registered == false) {
			// sc server not registered - deregister not necessary
			return;
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		IRequester req = null;
		try {
			// remove srvService from registry
			SrvService srvService = srvServiceRegistry.removeSrvService(this.serviceName + "_"
					+ this.scServerContext.getListenerPort());
			req = srvService.getRequester();
			SCMPDeRegisterServerCall deRegisterServerCall = (SCMPDeRegisterServerCall) SCMPCallFactory.DEREGISTER_SERVER_CALL
					.newInstance(req, this.serviceName);
			SCServerCallback callback = new SCServerCallback(true);
			this.msgSequenceNr.incrementMsgSequenceNr();
			try {
				deRegisterServerCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("deregister server failed", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("deregister server failed");
				ex.setAppErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				throw ex;
			}
		} finally {
			// destroy connection pool
			req.getContext().getConnectionPool().destroy();
			this.registered = false;
			AppContext.attachedCommunicators.decrementAndGet();
			AppContext.destroy();
		}
	}

	/**
	 * Checks if is listening.
	 * 
	 * @return true, if is listening
	 */
	public boolean isListening() {
		return this.scServerContext.isListening();
	}

	/**
	 * Checks if is registered.
	 * 
	 * @return true, if is registered
	 */
	public boolean isRegistered() {
		return this.registered;
	}

	/**
	 * Sets the immediate connect. Affects connecting behavior from SC. If immediateConnect is set SC establishes connection to
	 * server at the time registerServer is received.
	 * 
	 * @param immediateConnect
	 *            the new immediate connect
	 */
	public void setImmediateConnect(boolean immediateConnect) {
		this.immediateConnect = immediateConnect;
	}

	/**
	 * Checks if is immediate connect.
	 * 
	 * @return true, if is immediate connect
	 */
	public boolean isImmediateConnect() {
		return this.immediateConnect;
	}

	/**
	 * Gets the sC host.
	 * 
	 * @return the sC host
	 */
	public String getSCHost() {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		return srvServiceRegistry.getSrvService(this.serviceName + "_" + this.scServerContext.getListenerPort()).getRequester()
				.getContext().getConnectionPool().getHost();
	}

	/**
	 * Gets the sC port.
	 * 
	 * @return the sC port
	 */
	public int getSCPort() {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		return srvServiceRegistry.getSrvService(this.serviceName + "_" + this.scServerContext.getListenerPort()).getRequester()
				.getContext().getConnectionPool().getPort();
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return this.localServerHost;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return this.localServerPort;
	}

	/**
	 * Gets the connection type. Default {netty.tcp}
	 * 
	 * @return the connection type in use
	 */
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
	 * Gets the sC server context.
	 * 
	 * @return the sC server context
	 */
	public SCServerContext getContext() {
		return this.scServerContext;
	}

	/**
	 * The Class SCServerCallback.
	 */
	protected class SCServerCallback extends SynchronousCallback {

		/**
		 * Instantiates a new sC server callback.
		 * 
		 * @param synchronous
		 *            the synchronous
		 */
		public SCServerCallback(boolean synchronous) {
			this.synchronous = synchronous;
		}
		// nothing to implement in this case - everything is done by super-class
	}

	public void destroy() {
		SrvService srvService = AppContext.getSrvServiceRegistry().getSrvService(
				this.serviceName + "_" + this.scServerContext.getListenerPort());
		srvService.getRequester().getContext().getConnectionPool().destroy();
	}
}
