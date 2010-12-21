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
package org.serviceconnector.api.cln;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDetachCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ValidatorUtility;

/**
 * Client to an SC.
 * 
 * @author JTraber
 */
public class SCClient {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCClient.class);

	/** The host of the SC. */
	private String host;
	/** The port of the SC. */
	private int port;
	/** The max connections to use in pool. */
	private int maxConnections;
	/** The keep alive interval. */
	private int keepAliveIntervalSeconds;
	/** The connection type. {netty.http} */
	private ConnectionType connectionType;
	/** The requester. */
	protected SCRequester requester;

	protected boolean attached;

	/**
	 * Instantiates a new SC client.
	 */
	public SCClient(String host, int port) {
		this(host, port, ConnectionType.DEFAULT_CLIENT_CONNECTION_TYPE);
	}

	/**
	 * Instantiates a new SC client.
	 */
	public SCClient(String host, int port, ConnectionType connectionType) {
		this.host = host;
		this.port = port;
		this.connectionType = connectionType;
		this.keepAliveIntervalSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS;
		this.attached = false;
		this.maxConnections = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
	}

	/**
	 * Attach client to SC.
	 * 
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset
	 */
	public synchronized void attach() throws Exception {
		this.attach(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Attach client to SC.
	 * 
	 * @param operationTimeout
	 *            the operation timeout
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset<br>
	 */
	public synchronized void attach(int operationTimeout) throws Exception {
		if (this.attached) {
			throw new SCServiceException("already attached");
		}
		if (host == null) {
			throw new InvalidParameterException("host must be set");
		}
		ValidatorUtility.validateInt(1, operationTimeout, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		ValidatorUtility.validateInt(1, this.port, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
		synchronized (AppContext.communicatorsLock) {
			AppContext.init();
			this.requester = new SCRequester(new RequesterContext(this.host, this.port, this.connectionType.getValue(),
					keepAliveIntervalSeconds, this.maxConnections));
			SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);
			SCServiceCallback callback = new SCServiceCallback(true);
			try {
				attachCall.invoke(callback, operationTimeout * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				this.requester.destroy();
				throw new SCServiceException("attach to " + host + ":" + port + " failed", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeout * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				this.requester.destroy();
				SCServiceException ex = new SCServiceException("attach to " + host + ":" + port + " failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
			this.attached = true;
			AppContext.attachedCommunicators.incrementAndGet();
		}
	}

	/**
	 * Checks if client is attached to SC.
	 * 
	 * @return true, if is attached
	 */
	public boolean isAttached() {
		return this.attached;
	}

	/**
	 * Detach.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void detach() throws Exception {
		this.detach(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Detach from SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void detach(int operationTimeout) throws Exception {
		if (this.attached == false) {
			// client is not attached just ignore
			return;
		}
		ValidatorUtility.validateInt(1, operationTimeout, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(this.requester);
			try {
				detachCall.invoke(callback, operationTimeout * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("detach client failed", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeout * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("detach client failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		} finally {
			this.attached = false;
			AppContext.attachedCommunicators.decrementAndGet();
			// destroy connection pool
			this.requester.destroy();
			// release resources
			AppContext.destroy();
		}
	}

	/**
	 * Gets the connection type. Default {netty.http}
	 * 
	 * @return the connection type in use
	 */
	public ConnectionType getConnectionType() {
		return this.connectionType;
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Gets the keep alive interval in seconds.
	 * 
	 * @return the keep alive interval in seconds
	 */
	public int getKeepAliveIntervalSeconds() {
		return this.keepAliveIntervalSeconds;
	}

	/**
	 * Sets the keep alive interval in seconds.
	 * 
	 * @param keepAliveIntervalSeconds
	 *            the new keep alive interval in seconds
	 * @throws SCMPValidatorException
	 * @throws Exception
	 *             SCMPValidatorException - keepAliveIntervalSeconds not within limits 0 to 3600 <br>
	 *             SCServiceException - if called after attach
	 */
	public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) throws Exception {
		ValidatorUtility.validateInt(0, this.keepAliveIntervalSeconds, 3600, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);
		if (this.attached) {
			throw new SCServiceException("cannot set property, client is already attached");
		}
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
	}

	/**
	 * Creates a new file service.
	 * 
	 * @param serviceName
	 *            the service name of the file service to use
	 * @return the file service
	 */
	public SCFileService newFileService(String serviceName) throws Exception {
		if (this.attached == false) {
			throw new SCServiceException("newFileService not possible - client not attached.");
		}
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		return new SCFileService(this, serviceName, this.requester);
	}

	/**
	 * Creates a new session service.
	 * 
	 * @param serviceName
	 *            the service name of the session service to use
	 * @return the session service
	 */
	public SCSessionService newSessionService(String serviceName) throws Exception {
		if (this.attached == false) {
			throw new SCServiceException("newSessionService not possible - client not attached.");
		}
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		return new SCSessionService(this, serviceName, this.requester);
	}

	/**
	 * Creates a new publish service.
	 * 
	 * @param serviceName
	 *            the service name of the publish service to use
	 * @return the publish service
	 */
	public SCPublishService newPublishService(String serviceName) throws Exception {
		if (this.attached == false) {
			throw new SCServiceException("newPublishService not possible - client not attached.");
		}
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
		ValidatorUtility.validateAllowedCharacters(serviceName, SCMPError.HV_WRONG_SERVICE_NAME);
		return new SCPublishService(this, serviceName, this.requester);
	}

	/**
	 * Sets the max connections. If client is already connected to the SC and max connections is lower than default value or value
	 * set earlier connection pool is not reducing the connections immediately.
	 * 
	 * @param maxConnections
	 *            the new max connections used by connection pool.
	 * @throws Exception
	 *             SCMPValidatorException - if maxConnections smaller one <br>
	 *             SCServiceException - when called after attach()
	 */
	public void setMaxConnections(int maxConnections) throws Exception {
		ValidatorUtility.validateInt(1, maxConnections, SCMPError.HV_WRONG_MAX_CONNECTIONS);
		if (this.attached) {
			throw new SCServiceException("cannot set property, client is already attached");
		}
		this.maxConnections = maxConnections;
	}

	/**
	 * Gets the max connections.
	 * 
	 * @return the max connections used in pool
	 */
	public int getMaxConnections() {
		return this.maxConnections;
	}

}
