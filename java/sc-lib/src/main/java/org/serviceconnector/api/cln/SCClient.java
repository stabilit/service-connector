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
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.req.IRequester;
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
	private int keepAliveIntervalInSeconds;
	/** The connection pool. */
	protected ConnectionPool connectionPool;
	/** The connection type. {netty.http}*/
	private String connectionType;
	/** The requester. */
	protected IRequester requester;
	/** The context. */
	protected SCContext scContext;

	protected boolean attached;

	/**
	 * Instantiates a new SC client.
	 */
	public SCClient() {
		this.host = null;
		this.port = -1;
		this.connectionType = ConnectionType.DEFAULT_CLIENT_CONNECTION_TYPE;
		this.keepAliveIntervalInSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;
		this.scContext = new SCContext(this);
		this.attached = false;
		this.maxConnections = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
		this.connectionPool = null;
	}

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public SCContext getSCContext() {
		return this.scContext;
	}

	/**
	 * Attach client to SC.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             scPort is not within limits 0 to 0xFFFF, scHost unset
	 */
	public synchronized void attach(String host, int port) throws Exception {
		this.attach(host, port, Constants.DEFAULT_KEEP_ALIVE_INTERVAL);
	}

	/**
	 * Attach client to SC.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param keepAliveIntervalInSeconds
	 *            the keep alive interval in seconds
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset<br>
	 *             keepAliveIntervalInSeconds not within limits 0 to 3600
	 */
	public synchronized void attach(String host, int port, int keepAliveIntervalInSeconds) throws Exception {
		if (this.attached) {
			throw new SCServiceException("already attached");
		}
		if (host == null) {
			throw new InvalidParameterException("host must be set.");
		}
		ValidatorUtility.validateInt(0, port, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
		ValidatorUtility.validateInt(0, keepAliveIntervalInSeconds, 3600, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);
		this.port = port;
		this.host = host;
		this.keepAliveIntervalInSeconds = keepAliveIntervalInSeconds;
		this.connectionPool = new ConnectionPool(host, port, this.connectionType, keepAliveIntervalInSeconds);
		this.connectionPool.setMaxConnections(this.maxConnections);
		// keep always one connection active from client to SC
		this.connectionPool.setMinConnections(1);
		this.scContext.setConnectionPool(this.connectionPool);
		this.requester = new SCRequester(new RequesterContext(this.connectionPool, null));
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			attachCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.connectionPool.destroy();
			throw new SCServiceException("attach to " + host + ":" + port + " failed", e);
		}
		SCMPMessage reply = callback.getMessageSync();
		if (reply.isFault()) {
			this.connectionPool.destroy();
			throw new SCServiceException("attach to " + host + ":" + port + " failed : "
					+ reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));

		}
		this.attached = true;
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
	 * Detach from SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void detach() throws Exception {
		if (this.attached == false) {
			// client is not attached just ignore
			return;
		}
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(this.requester);
			try {
				detachCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("detach client failed", e);
			}
			SCMPMessage reply = callback.getMessageSync();
			if (reply.isFault()) {
				throw new SCServiceException("detach client failed : " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			}
		} finally {
			this.attached = false;
			// destroy connection pool
			this.connectionPool.destroy();
		}
	}

	/**
	 * Gets the connection type. Default {netty.http}
	 * 
	 * @return the connection type in use
	 */
	public String getConnectionType() {
		return connectionType;
	}

	/**
	 * Sets the connection type.
	 * 
	 * @param connectionType
	 *            the new connection type, identifies low level communication technology
	 */
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the keep alive interval in seconds.
	 * 
	 * @return the keep alive interval in seconds
	 */
	public int getKeepAliveIntervalInSeconds() {
		return this.keepAliveIntervalInSeconds;
	}

	/**
	 * Creates a new file service.
	 * 
	 * @param serviceName
	 *            the service name of the file service to use
	 * @return the file service
	 */
	public SCFileService newFileService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		if (this.attached == false) {
			throw new SCServiceException("newFileService not possible - client not attached.");
		}
		return new SCFileService(serviceName, this.scContext);
	}

	/**
	 * Creates a new session service.
	 * 
	 * @param serviceName
	 *            the service name of the session service to use
	 * @return the session service
	 */
	public SCSessionService newSessionService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		if (this.attached == false) {
			throw new SCServiceException("newSessionService not possible - client not attached.");
		}
		return new SCSessionService(serviceName, this.scContext);
	}

	/**
	 * Creates a new publish service.
	 * 
	 * @param serviceName
	 *            the service name of the publish service to use
	 * @return the publish service
	 */
	public SCPublishService newPublishService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		if (this.attached == false) {
			throw new SCServiceException("newPublishService not possible - client not attached.");
		}
		return new SCPublishService(serviceName, this.scContext);
	}

	/**
	 * Sets the max connections. If client is already connected to the SC and max connections is lower than default value or value
	 * set earlier connection pool is not reducing the connections immediately.
	 * 
	 * @param maxConnections
	 *            the new max connections used by connection pool.
	 * @throws InvalidParameterException
	 *             maxConnections smaller one
	 */
	public void setMaxConnections(int maxConnections) throws SCMPValidatorException {
		ValidatorUtility.validateInt(1, maxConnections, SCMPError.HV_WRONG_MAX_CONNECTIONS);
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
