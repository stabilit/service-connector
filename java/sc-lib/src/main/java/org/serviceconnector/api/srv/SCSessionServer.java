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
import java.util.List;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPCheckRegistrationCall;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.cmd.srv.ServerCommandFactory;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
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

	private boolean registered;
	protected String serviceName;
	protected SCRequester requester;
	protected SCServer scServer;

	static {
		// Initialize server command factory one time
		AppContext.initCommands(new ServerCommandFactory());
	}

	/**
	 * Instantiates a new SCSessionServer.
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
	public int getKeepAliveIntervalInSeconds() {
		return this.scServer.getKeepAliveIntervalSeconds();
	}

	public int getMaxSessions() {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		return srvServiceRegistry.getSrvService(this.serviceName + "_" + this.scServer.getListenerPort())
				.getMaxSessions();
	}

	public int getMaxConnections() {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		return srvServiceRegistry.getSrvService(this.serviceName + "_" + this.scServer.getListenerPort())
				.getMaxConnections();
	}

	public synchronized void register(int maxSessions, int maxConnections, SCSessionServerCallback scCallback)
			throws Exception {
		this.register(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, maxSessions, maxConnections, scCallback);
	}

	public synchronized void register(int operationTimeoutSeconds, int maxSessions, int maxConnections,
			SCSessionServerCallback scCallback) throws Exception {
		if (scCallback == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "callback must be set");
		}
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		this.doRegister(operationTimeoutSeconds, maxSessions, maxConnections);
		// creating srvService & adding to registry
		SrvService srvService = new SrvSessionService(this.serviceName, maxSessions, maxConnections, this.requester,
				scCallback);
		srvServiceRegistry.addSrvService(this.serviceName + "_" + this.scServer.getListenerPort(), srvService);
		this.registered = true;
	}

	public synchronized void register(int maxSessions, int maxConnections, SCPublishServerCallback scCallback)
			throws Exception {
		this.register(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, maxSessions, maxConnections, scCallback);
	}

	public synchronized void register(int operationTimeoutSeconds, int maxSessions, int maxConnections,
			SCPublishServerCallback scCallback) throws Exception {
		if (scCallback == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "callback must be set");
		}
		this.doRegister(operationTimeoutSeconds, maxSessions, maxConnections);
		// creating srvService & adding to registry
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		SrvService srvService = new SrvPublishService(this.serviceName, maxSessions, maxConnections, this.requester,
				scCallback);
		srvServiceRegistry.addSrvService(this.serviceName + "_" + this.scServer.getListenerPort(), srvService);
		this.registered = true;
	}

	private synchronized void doRegister(int operationTimeoutSeconds, int maxSessions, int maxConnections)
			throws Exception {
		if (this.scServer.isListening() == false) {
			throw new InvalidActivityException("Listener must be started before register service is allowed.");
		}
		if (this.registered) {
			throw new InvalidActivityException("Server already registered for a service.");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateInt(1, maxSessions, SCMPError.HV_WRONG_MAX_SESSIONS);
		ValidatorUtility.validateInt(1, maxConnections, 1024, SCMPError.HV_WRONG_MAX_SESSIONS);
		ValidatorUtility.validateInt(1, maxConnections, maxSessions, SCMPError.HV_WRONG_MAX_SESSIONS);
		if ((maxSessions == 1) && (maxConnections > 1)) {
			throw new InvalidParameterException("maxConnections must be = 1 for single-session server");
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
				this.requester.getContext().getSCMPMsgSequenceNr().reset();

				SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL
						.newInstance(this.requester, this.serviceName);

				registerServerCall.setMaxSessions(maxSessions);
				registerServerCall.setMaxConnections(maxConnections);
				registerServerCall.setPortNumber(listenerPort);
				registerServerCall.setImmediateConnect(immediateConnect);
				registerServerCall.setKeepAliveInterval(keepAliveIntervalSeconds);
				SCServerCallback callback = new SCServerCallback(true);
				try {
					registerServerCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
				} catch (Exception e) {
					throw new SCServiceException("register server failed", e);
				}
				SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
				if (reply.isFault()) {
					SCServiceException ex = new SCServiceException("register server failed");
					ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
					throw ex;
				}
				AppContext.attachedCommunicators.incrementAndGet();
			}
		}
	}

	public synchronized void checkRegistration() throws Exception {
		this.checkRegistration(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	public synchronized void checkRegistration(int operationTimeoutSeconds) throws Exception {
		if (this.registered == false) {
			throw new InvalidActivityException("Server already is not registered for a service.");
		}
		synchronized (this.scServer) {
			// get lock on scServer - only one server is allowed to communicate over the initial connection
			SCMPCheckRegistrationCall checkRegistrationCall = (SCMPCheckRegistrationCall) SCMPCallFactory.CHECK_REGISTRATION_CALL
					.newInstance(this.requester, this.serviceName);
			SCServerCallback callback = new SCServerCallback(true);
			try {
				checkRegistrationCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("check registration failed", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("check registration failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				throw ex;
			}
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
		synchronized (this.scServer) {
			// get lock on scServer - only one server is allowed to communicate over the initial connection
			try {
				// remove srvService from registry
				srvServiceRegistry.removeSrvService(this.serviceName + "_" + this.scServer.getListenerPort());

				SCMPDeRegisterServerCall deRegisterServerCall = (SCMPDeRegisterServerCall) SCMPCallFactory.DEREGISTER_SERVER_CALL
						.newInstance(this.requester, this.serviceName);
				SCServerCallback callback = new SCServerCallback(true);
				try {
					deRegisterServerCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
				} catch (Exception e) {
					throw new SCServiceException("deregister server failed", e);
				}
				SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
				if (reply.isFault()) {
					SCServiceException ex = new SCServiceException("deregister server failed");
					ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
					throw ex;
				}
			} finally {
				this.registered = false;
				AppContext.attachedCommunicators.decrementAndGet();
			}
		}
	}

	/**
	 * Checks if is listening.
	 * 
	 * @return true, if is listening
	 */
	public boolean isListening() {
		return this.scServer.isListening();
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
	 * Checks if is immediate connect.
	 * 
	 * @return true, if is immediate connect
	 */
	public boolean isImmediateConnect() {
		return this.scServer.isImmediateConnect();
	}

	/**
	 * Gets the sC host.
	 * 
	 * @return the sC host
	 */
	public String getSCHost() {
		return this.scServer.getSCHost();
	}

	/**
	 * Gets the sC port.
	 * 
	 * @return the sC port
	 */
	public int getSCPort() {
		return this.scServer.getSCPort();
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public List<String> getListenerInterfaces() {
		return this.scServer.getListenerInterfaces();
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getListenerPort() {
		return this.scServer.getListenerPort();
	}

	public ConnectionType getConnectionType() {
		return this.scServer.getConnectionType();
	}

	public String getServiceName() {
		return this.serviceName;
	}

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
