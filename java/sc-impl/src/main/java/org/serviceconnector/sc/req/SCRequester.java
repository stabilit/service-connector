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
package org.serviceconnector.sc.req;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.serviceconnector.common.net.req.IConnection;
import org.serviceconnector.common.net.req.IConnectionContext;
import org.serviceconnector.common.net.req.IRequester;
import org.serviceconnector.common.net.req.IRequesterContext;
import org.serviceconnector.common.scmp.ISCMPCallback;
import org.serviceconnector.common.scmp.SCMPError;
import org.serviceconnector.common.scmp.SCMPFault;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.util.ITimerRun;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.util.TimerTaskWrapper;


/**
 * The Class SCRequester. Defines behavior of requester in the context of Service Connector.
 * 
 * @author JTraber
 */
public class SCRequester implements IRequester {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCRequester.class);

	/** The context. */
	private IRequesterContext reqContext;
	/** The Constant timer, triggers all operation timeout for sending. */
	protected final static Timer timer = new Timer("OperationTimerSCRequester");

	public SCRequester(IRequesterContext context) {
		this.reqContext = context;
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage message, double timeoutInMillis, ISCMPCallback callback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.reqContext.getConnectionPool().getConnection();
		IConnectionContext connectionContext = connection.getContext();
		ISCMPCallback requesterCallback = new SCRequesterSCMPCallback(callback, connectionContext);
		// setting up operation timeout after successful send
		TimerTask task = new TimerTaskWrapper((ITimerRun) requesterCallback);
		SCRequesterSCMPCallback reqCallback = (SCRequesterSCMPCallback) requesterCallback;
		reqCallback.setOperationTimeoutTask(task);
		reqCallback.setTimeoutMillis(timeoutInMillis);
		timer.schedule(task, (long) timeoutInMillis);
		connection.send(message, requesterCallback);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized String toHashCodeString() {
		return " [" + this.hashCode() + "]";
	}

	/** {@inheritDoc} */
	@Override
	public IRequesterContext getContext() {
		return reqContext;
	}

	/**
	 * The Class SCRequesterSCMPCallback. Component used for asynchronous communication. It gets informed at the time a
	 * reply is received. Handles freeing up earlier requested connections.
	 */
	private class SCRequesterSCMPCallback implements ISCMPCallback, ITimerRun {

		/** The scmp callback, callback to inform next layer. */
		private ISCMPCallback scmpCallback;
		/** The connection context. */
		private IConnectionContext connectionCtx;
		/** The operation timeout task. */
		private TimerTask operationTimeoutTask;
		/** The timeout in milliseconds. */
		private double timeoutInMillis;

		public SCRequesterSCMPCallback(ISCMPCallback scmpCallback, IConnectionContext connectionCtx) {
			this.scmpCallback = scmpCallback;
			this.connectionCtx = connectionCtx;
			this.operationTimeoutTask = null;
			this.timeoutInMillis = 0;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// cancel operation timeout
			this.operationTimeoutTask.cancel();
			// first handle connection - that user has a connection to work, if he has only 1
			this.freeConnection();
			this.scmpCallback.callback(scmpReply);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(Exception ex) {
			// cancel operation timeout
			this.operationTimeoutTask.cancel();
			// first handle connection - that user has a connection to work, if he has only 1
			if (ex instanceof IdleTimeoutException) {
				// operation timed out - delete this specific connection, prevents race conditions
				this.disconnectConnection();
			} else {
				// another exception occurred - just free the connection
				this.freeConnection();
			}
			this.scmpCallback.callback(ex);
		}

		/**
		 * Free connection. Orders connectionPool to give the connection free. Its not used by the requester anymore.
		 */
		private void freeConnection() {
			try {
				SCRequester.this.reqContext.getConnectionPool().freeConnection(connectionCtx.getConnection());
			} catch (Exception ex) {
				logger.error("freeConnection", ex);
			}
		}

		/**
		 * Disconnect connection. Orders connectionPool to disconnect this connection. Might be the case if connection
		 * has a curious state.
		 */
		private void disconnectConnection() {
			try {
				SCRequester.this.reqContext.getConnectionPool().forceClosingConnection(connectionCtx.getConnection());
			} catch (Exception ex) {
				logger.error("disconnectConnection", ex);
			}
		}

		/**
		 * Sets the operation timeout task.
		 * 
		 * @param operationTimeoutTask
		 *            the new operation timeout task
		 */
		public void setOperationTimeoutTask(TimerTask operationTimeoutTask) {
			this.operationTimeoutTask = operationTimeoutTask;
		}

		/** {@inheritDoc} */
		@Override
		public double getTimeoutMillis() {
			return this.timeoutInMillis;
		}

		/**
		 * Sets the timeout milliseconds.
		 * 
		 * @param timeoutInMillis
		 *            the new timeout seconds
		 */
		public void setTimeoutMillis(double timeoutInMillis) {
			this.timeoutInMillis = timeoutInMillis;
		}

		/**
		 * Operation timeout run out. Clean up. Close connection and inform upper level by callback.
		 */
		@Override
		public void timeout() {
			this.disconnectConnection();
			SCMPFault fault = new SCMPFault(SCMPError.GATEWAY_TIMEOUT, "getting message took too long");
			try {
				this.scmpCallback.callback(fault);
			} catch (Exception e) {
				this.scmpCallback.callback(e);
			}
		}
	}
}
