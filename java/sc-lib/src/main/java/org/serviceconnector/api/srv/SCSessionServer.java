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

import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.call.SCMPCheckRegistrationCall;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.cmd.srv.ServerCommandFactory;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.SynchronousCallback;

/**
 * The Class SCServer. Basic class for any kind of a server which communicates with an SC.
 * 
 * @author JTraber
 */
public class SCSessionServer {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCSessionServer.class);

	/** The registered. Indicates if a server is already registered to an SC. */
	protected boolean registered;
	/** The service name. */
	protected String serviceName;
	/** The requester. */
	protected SCRequester requester;
	/** The SC server. */
	protected SCServer scServer;

	static {
		// Initialize server command factory one time
		AppContext.initCommands(new ServerCommandFactory());
	}

	/**
	 * Instantiates a new SCSessionServer.
	 * 
	 * @param scServer
	 *            the sc server
	 * @param serviceName
	 *            the service name
	 * @param requester
	 *            the requester
	 */
	public SCSessionServer(SCServer scServer, String serviceName, SCRequester requester) {
		this.requester = requester;
		this.scServer = scServer;
		this.serviceName = serviceName;
		// attributes for registerServer
		this.registered = false;
	}

	/**
	 * Gets the keep alive interval in seconds.
	 * 
	 * @return the keep alive interval in seconds
	 */
	public int getKeepAliveIntervalSeconds() {
		return this.scServer.getKeepAliveIntervalSeconds();
	}

	/**
	 * Gets the max sessions the server is serving for service.
	 * 
	 * @return the max sessions
	 */
	public int getMaxSessions() {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		return srvServiceRegistry.getSrvService(this.serviceName + "_" + this.scServer.getListenerPort()).getMaxSessions();
	}

	/**
	 * Gets the max connections which the pool uses to connect to SC.
	 * 
	 * @return the max connections
	 */
	public int getMaxConnections() {
		return this.requester.getRemoteNodeConfiguration().getMaxPoolSize();
	}

	/**
	 * Register server with default operation timeout.
	 * 
	 * @param maxSessions
	 *            the max sessions to serve
	 * @param maxConnections
	 *            the max connections pool uses to connect to SC
	 * @param scCallback
	 *            the SC callback
	 * @throws SCServiceException
	 *             listener not started yet<br>
	 *             server already registered for service<br>
	 *             register server on SC failed<br>
	 *             error message received from SC <br>
	 * @throws SCMPValidatorException
	 *             callback is not set<br>
	 */
	public synchronized void register(int maxSessions, int maxConnections, SCSessionServerCallback scCallback)
			throws SCServiceException, SCMPValidatorException {
		this.register(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, maxSessions, maxConnections, scCallback);
	}

	/**
	 * Register server.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param maxSessions
	 *            the max sessions to serve
	 * @param maxConnections
	 *            the max connections pool uses to connect to SC
	 * @param scCallback
	 *            the SC callback
	 * @throws SCServiceException
	 *             listener not started yet<br>
	 *             server already registered for service<br>
	 *             register server on SC failed<br>
	 *             error message received from SC <br>
	 * @throws SCMPValidatorException
	 *             callback is not set<br>
	 */
	public synchronized void register(int operationTimeoutSeconds, int maxSessions, int maxConnections,
			SCSessionServerCallback scCallback) throws SCServiceException, SCMPValidatorException {
		if (scCallback == null) {
			throw new SCMPValidatorException("Callback is missing.");
		}
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		this.doRegister(operationTimeoutSeconds, maxSessions, maxConnections);
		// creating srvService & adding to registry
		SrvService srvService = new SrvSessionService(this.serviceName, maxSessions, maxConnections, this.requester, scCallback);
		srvServiceRegistry.addSrvService(this.serviceName + "_" + this.scServer.getListenerPort(), srvService);
		this.registered = true;
	}

	/**
	 * Do register.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param maxSessions
	 *            the max sessions to serve
	 * @param maxConnections
	 *            the max connections pool uses to connect to SC
	 * @throws SCServiceException
	 *             listener not started yet<br>
	 *             server already registered for service<br>
	 *             register server on SC failed<br>
	 *             error message received from SC <br>
	 */
	protected synchronized void doRegister(int operationTimeoutSeconds, int maxSessions, int maxConnections)
			throws SCServiceException {
		if (this.scServer.isListening() == false) {
			throw new SCServiceException("Listener must be started before register service is allowed.");
		}
		if (this.registered) {
			throw new SCServiceException("Server already registered for a service.");
		}
		// already validated
		int listenerPort = this.scServer.getListenerPort();
		int keepAliveIntervalSeconds = this.scServer.getKeepAliveIntervalSeconds();
		boolean immediateConnect = this.scServer.isImmediateConnect();
		synchronized (this.scServer) {
			// get lock on scServer - only one server is allowed to communicate over the initial connection
			synchronized (AppContext.communicatorsLock) {
				// get communicator lock - avoids interference with other clients or scServers
				AppContext.init();
				this.requester.getSCMPMsgSequenceNr().reset();

				SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(this.requester, this.serviceName);

				registerServerCall.setMaxSessions(maxSessions);
				registerServerCall.setMaxConnections(maxConnections);
				registerServerCall.setPortNumber(listenerPort);
				registerServerCall.setImmediateConnect(immediateConnect);
				registerServerCall.setKeepAliveInterval(keepAliveIntervalSeconds);
				registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
				registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
				SCServerCallback callback = new SCServerCallback(true);
				try {
					registerServerCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
				} catch (Exception e) {
					throw new SCServiceException("Register server failed. ", e);
				}
				SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
				if (reply.isFault()) {
					SCServiceException ex = new SCServiceException("Register server failed.");
					ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
					ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
					throw ex;
				}
				AppContext.attachedCommunicators.incrementAndGet();
			}
		}
	}

	/**
	 * Check registration with default operation timeout. This message can be sent from the registered server to SC in order to
	 * check its registration.
	 * 
	 * @throws SCServiceException
	 *             server is not registered for a service<br>
	 *             check registration failed<br>
	 *             error message received from SC <br>
	 */
	public synchronized void checkRegistration() throws SCServiceException {
		this.checkRegistration(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Check registration. This message can be sent from the registered server to SC in order to check its registration.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCServiceException
	 *             server is not registered for a service<br>
	 *             check registration failed<br>
	 *             error message received from SC <br>
	 */
	public synchronized void checkRegistration(int operationTimeoutSeconds) throws SCServiceException {
		if (this.registered == false) {
			throw new SCServiceException("Server is not registered for a service.");
		}
		synchronized (this.scServer) {
			// get lock on scServer - only one server is allowed to communicate over the initial connection
			SCMPCheckRegistrationCall checkRegistrationCall = new SCMPCheckRegistrationCall(this.requester, this.serviceName);
			SCServerCallback callback = new SCServerCallback(true);
			try {
				checkRegistrationCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("Check registration failed. ", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("Check registration failed.");
				ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		}
	}

	/**
	 * Deregister server with default operation timeout.
	 * 
	 * @throws SCServiceException
	 *             deregister failed<br>
	 *             error message received from SC<br>
	 */
	public synchronized void deregister() throws SCServiceException {
		this.deregister(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Deregister server.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCServiceException
	 *             deregister failed<br>
	 *             error message received from SC<br>
	 */
	public synchronized void deregister(int operationTimeoutSeconds) throws SCServiceException {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		if (this.registered == false) {
			// sc server not registered - deregister not necessary
			return;
		}
		synchronized (this.scServer) {
			// get lock on scServer - only one server is allowed to communicate over the initial connection
			try {
				// remove srvService from registry
				srvServiceRegistry.removeSrvService(this.serviceName + "_" + this.scServer.getListenerPort());

				SCMPDeRegisterServerCall deRegisterServerCall = new SCMPDeRegisterServerCall(this.requester, this.serviceName);
				SCServerCallback callback = new SCServerCallback(true);
				try {
					deRegisterServerCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
				} catch (Exception e) {
					throw new SCServiceException("Deregister server failed. ", e);
				}
				SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
				if (reply.isFault()) {
					SCServiceException ex = new SCServiceException("Deregister server failed.");
					ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
					ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
					throw ex;
				}
			} finally {
				this.registered = false;
				AppContext.attachedCommunicators.decrementAndGet();
			}
		}
	}

	/**
	 * Checks if server is listening.
	 * 
	 * @return true, if is listening
	 */
	public boolean isListening() {
		return this.scServer.isListening();
	}

	/**
	 * Checks if serve is registered to an SC.
	 * 
	 * @return true, if is registered
	 */
	public boolean isRegistered() {
		return this.registered;
	}

	/**
	 * Checks if server has its immediate connect flag set.
	 * 
	 * @return true, if is immediate connect
	 */
	public boolean isImmediateConnect() {
		return this.scServer.isImmediateConnect();
	}

	/**
	 * Gets the SC host.
	 * 
	 * @return the SC host
	 */
	public String getSCHost() {
		return this.scServer.getSCHost();
	}

	/**
	 * Gets the SC port.
	 * 
	 * @return the SC port
	 */
	public int getSCPort() {
		return this.scServer.getSCPort();
	}

	/**
	 * Gets the interfaces on which server is listening.
	 * 
	 * @return the interfaces
	 */
	public List<String> getListenerInterfaces() {
		return this.scServer.getListenerInterfaces();
	}

	/**
	 * Gets the port on which server is listening.
	 * 
	 * @return the listening port
	 */
	public int getListenerPort() {
		return this.scServer.getListenerPort();
	}

	/**
	 * Gets the connection type which is used to communicate with SC.
	 * 
	 * @return the connection type
	 */
	public ConnectionType getConnectionType() {
		return this.scServer.getConnectionType();
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return this.serviceName;
	}

	/**
	 * Gets the SC server.
	 * 
	 * @return the SC server
	 */
	public SCServer getSCServer() {
		return this.scServer;
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
}
