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
package org.serviceconnector.net.req;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.TimeoutWrapper;

/**
 * The Class SCRequester. Defines behavior of requester in the context of Service Connector.
 * 
 * @author JTraber
 */
public class Requester implements IRequester {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(Requester.class);

	private RemoteNodeConfiguration remoteNodeConfiguration;

	private ConnectionPool connectionPool = null;

	/**
	 * Instantiates a new requester.
	 * 
	 * @param reqContext
	 *            the reqContext
	 */
	public Requester(RemoteNodeConfiguration remoteNodeConfiguration) {
		this.remoteNodeConfiguration = remoteNodeConfiguration;
		this.connectionPool = new ConnectionPool(remoteNodeConfiguration.getHost(), remoteNodeConfiguration.getPort(),
				remoteNodeConfiguration.getConnectionType(), remoteNodeConfiguration.getKeepAliveIntervalSeconds());
		this.connectionPool.setMaxConnections(remoteNodeConfiguration.getMaxPoolSize());
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage message, int timeoutMillis, ISCMPMessageCallback callback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.connectionPool.getConnection();
		ConnectionContext connectionContext = connection.getContext();
		try {
			ISCMPMessageCallback requesterCallback = new RequesterSCMPCallback(callback, connectionContext);
			// setting up operation timeout after successful send
			TimeoutWrapper timeoutWrapper = new TimeoutWrapper((ITimeout) requesterCallback);
			RequesterSCMPCallback reqCallback = (RequesterSCMPCallback) requesterCallback;
			@SuppressWarnings("unchecked")
			ScheduledFuture<TimeoutWrapper> timeout = (ScheduledFuture<TimeoutWrapper>) AppContext.otiScheduler.schedule(
					timeoutWrapper, (long) timeoutMillis, TimeUnit.MILLISECONDS);
			reqCallback.setOperationTimeout(timeout);
			reqCallback.setTimeoutMillis(timeoutMillis);
			connection.send(message, requesterCallback);
		} catch (Exception ex) {
			this.connectionPool.freeConnection(connection);
			throw ex;
		}
	}

	/** {@inheritDoc} */
	@Override
	public RemoteNodeConfiguration getRemoteNodeConfiguration() {
		return this.remoteNodeConfiguration;
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		this.connectionPool.destroy();
	}

	/**
	 * The Class SCRequesterSCMPCallback. Component used for asynchronous communication. It gets informed at the time a reply is
	 * received. Handles freeing up earlier requested connections.
	 */
	private class RequesterSCMPCallback implements ISCMPMessageCallback, ITimeout {

		/** The scmp callback, callback to inform next layer. */
		private ISCMPMessageCallback scmpCallback;
		/** The connection context. */
		private ConnectionContext connectionCtx;
		/** The operation timeout. */
		private ScheduledFuture<TimeoutWrapper> operationTimeout;
		/** The timeout in milliseconds. */
		private int timeoutMillis;

		public RequesterSCMPCallback(ISCMPMessageCallback scmpCallback, ConnectionContext connectionCtx) {
			this.scmpCallback = scmpCallback;
			this.connectionCtx = connectionCtx;
			this.operationTimeout = null;
			this.timeoutMillis = 0;
		}

		/** {@inheritDoc} */
		@Override
		public void receive(SCMPMessage scmpReply) throws Exception {
			// cancel operation timeout
			this.operationTimeout.cancel(false);
			// first handle connection - that user has a connection to work, if he has only 1
			this.freeConnection();
			// removes canceled oti timeouts
			AppContext.otiScheduler.purge();
			this.scmpCallback.receive(scmpReply);
		}

		/** {@inheritDoc} */
		@Override
		public void receive(Exception ex) {
			// cancel operation timeout
			this.operationTimeout.cancel(false);
			// first handle connection - that user has a connection to work, if he has only 1
			if (ex instanceof IdleTimeoutException) {
				// operation timed out - delete this specific connection, prevents race conditions
				this.disconnectConnection();
			} else {
				// another exception occurred - just free the connection
				this.freeConnection();
			}
			// removes canceled oti timeouts
			AppContext.otiScheduler.purge();
			this.scmpCallback.receive(ex);
		}

		/**
		 * Free connection. Orders connectionPool to give the connection free. Its not used by the requester anymore.
		 */
		private void freeConnection() {
			try {
				Requester.this.connectionPool.freeConnection(connectionCtx.getConnection());
			} catch (Exception ex) {
				logger.error("freeConnection", ex);
			}
		}

		/**
		 * Disconnect connection. Orders connectionPool to disconnect this connection. Might be the case if connection has a curious
		 * state.
		 */
		private void disconnectConnection() {
			try {
				Requester.this.connectionPool.forceClosingConnection(connectionCtx.getConnection());
			} catch (Exception ex) {
				logger.error("disconnect", ex);
			}
		}

		/**
		 * Sets the operation timeout.
		 * 
		 * @param operationTimeoutTask
		 *            the new operation timeout
		 */
		public void setOperationTimeout(ScheduledFuture<TimeoutWrapper> operationTimeoutTask) {
			this.operationTimeout = operationTimeoutTask;
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return this.timeoutMillis;
		}

		/**
		 * Sets the timeout milliseconds.
		 * 
		 * @param timeoutMillis
		 *            the new timeout seconds
		 */
		public void setTimeoutMillis(int timeoutMillis) {
			this.timeoutMillis = timeoutMillis;
		}

		/**
		 * Operation timeout run out. Clean up. Close connection and inform upper level by callback.
		 */
		@Override
		public void timeout() {
			logger.warn("oti timeout expiration on SC oti=" + this.timeoutMillis);
			this.disconnectConnection();
			this.scmpCallback.receive(new IdleTimeoutException("idle timeout. operation - could not be completed."));
		}
	}

	public void immediateConnect() {
		// set minimum connections to max for initial process
		this.connectionPool.setMinConnections(this.connectionPool.getMaxConnections());
		this.connectionPool.initMinConnections();
		// initial done - set it back to 1
		this.connectionPool.setMinConnections(Constants.DEFAULT_MIN_CONNECTION_POOL_SIZE);
	}
}
