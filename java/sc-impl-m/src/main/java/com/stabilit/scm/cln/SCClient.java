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
package com.stabilit.scm.cln;

import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.call.SCMPAttachCall;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDetachCall;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.service.IFileService;
import com.stabilit.scm.common.service.IPublishService;
import com.stabilit.scm.common.service.ISCClient;
import com.stabilit.scm.common.service.ISCContext;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.common.util.SynchronousCallback;

/**
 * The Class SCClient.
 * 
 * @author JTraber
 */
public class SCClient implements ISCClient {

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
	private ServiceConnectorContext context;
	private SCClientCallback callback;

	/**
	 * Instantiates a new service connector.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public SCClient(String host, int port) {
		this(host, port, Constants.DEFAULT_CLIENT_CON, Constants.DEFAULT_KEEP_ALIVE_INTERVAL,
				Constants.DEFAULT_NR_OF_THREADS);
	}

	/**
	 * Instantiates a new service connector.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionType
	 *            the connection type
	 */
	public SCClient(String host, int port, String connectionType) {
		this(host, port, connectionType, Constants.DEFAULT_KEEP_ALIVE_INTERVAL, Constants.DEFAULT_NR_OF_THREADS);
	}

	/**
	 * Instantiates a new service connector.
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
	public SCClient(String host, int port, String connectionType, int keepAliveInterval) {
		this(host, port, connectionType, keepAliveInterval, Constants.DEFAULT_NR_OF_THREADS);
	}

	/**
	 * Instantiates a new service connector.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionType
	 *            the connection type
	 * @param keepAliveInterval
	 *            the keep alive interval
	 * @param numberOfThreads
	 *            the number of threads
	 */
	public SCClient(String host, int port, String connectionType, int keepAliveInterval, int numberOfThreads) {
		this.host = host;
		this.port = port;
		this.conType = connectionType;
		this.numberOfThreads = numberOfThreads;
		this.connectionPool = new ConnectionPool(this.host, this.port, this.conType, keepAliveInterval, numberOfThreads);
		this.context = new ServiceConnectorContext();
		this.callback = null;
	}

	/** {@inheritDoc} */
	@Override
	public ISCContext getContext() {
		return context;
	}

	/** {@inheritDoc} */
	@Override
	public void attach() throws Exception {
		if (this.callback != null) {
			throw new SCServiceException(
					"already attached before - detach first, attaching in sequence is not allowed.");
		}
		this.requester = new Requester(new RequesterContext(this.context.getConnectionPool(), null));
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);
		this.callback = new SCClientCallback();
		attachCall.invoke(this.callback);
		this.callback.getMessageSync(Constants.getServiceLevelOperationTimeoutMillis());
	}

	/** {@inheritDoc} */
	@Override
	public void detach() throws Exception {
		if (this.callback == null) {
			throw new SCServiceException("detach not possible - client not attached.");
		}
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(this.requester);
		detachCall.invoke(this.callback);
		this.callback.getMessageSync(Constants.getServiceLevelOperationTimeoutMillis());
		this.callback = null;
		// destroy connection pool
		this.connectionPool.destroy();
	}

	/**
	 * Gets the number of threads.
	 * 
	 * @return the number of threads
	 */
	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	/**
	 * Sets the number of threads.
	 * 
	 * @param numberOfThreads
	 *            the new number of threads
	 */
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/**
	 * Gets the connection key.
	 * 
	 * @return the connection key
	 */
	public String getConnectionKey() {
		return conType;
	}

	/**
	 * Sets the connection type.
	 * 
	 * @param conType
	 *            the new connection type
	 */
	public void setConnectionType(String conType) {
		this.conType = conType;
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

	/** {@inheritDoc} */
	@Override
	public IFileService newFileService(String serviceName) {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public ISessionService newSessionService(String serviceName) {
		return new SessionService(serviceName, this.context);
	}

	/** {@inheritDoc} */
	@Override
	public IPublishService newPublishService(String serviceName) {
		return new PublishService(serviceName, this.context);
	}

	/** {@inheritDoc} */
	@Override
	public void setMaxConnections(int maxConnections) {
		this.connectionPool.setMaxConnections(maxConnections);
	}

	/**
	 * The Class ServiceConnectorContext.
	 */
	class ServiceConnectorContext implements ISCContext {

		/** {@inheritDoc} */
		@Override
		public IConnectionPool getConnectionPool() {
			return connectionPool;
		}

		/** {@inheritDoc} */
		@Override
		public ISCClient getServiceConnector() {
			return SCClient.this;
		}
	}

	private class SCClientCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
