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
package com.stabilit.scm.sc.req;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.req.IConnection;
import com.stabilit.scm.common.net.req.IConnectionContext;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.IRequesterContext;
import com.stabilit.scm.common.net.req.netty.IdleTimeoutException;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPMessage;

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

	public SCRequester(IRequesterContext context) {
		this.reqContext = context;
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMPMessage message, ISCMPCallback callback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.reqContext.getConnectionPool().getConnection();
		IConnectionContext connectionContext = connection.getContext();
		ISCMPCallback requesterCallback = new SCRequesterSCMPCallback(callback, connectionContext);
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
	private class SCRequesterSCMPCallback implements ISCMPCallback {

		/** The scmp callback, callback to inform next layer. */
		private ISCMPCallback scmpCallback;
		/** The connection context. */
		private IConnectionContext connectionCtx;

		public SCRequesterSCMPCallback(ISCMPCallback scmpCallback, IConnectionContext connectionCtx) {
			this.scmpCallback = scmpCallback;
			this.connectionCtx = connectionCtx;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			// first handle connection - that user has a connection to work, if he has only 1
			this.freeConnection();
			this.scmpCallback.callback(scmpReply);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(Exception ex) {
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
				logger.error("freeConnection "+ex.getMessage(), ex);
				ExceptionPoint.getInstance().fireException(this, ex);
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
				logger.error("disconnectConnection "+ex.getMessage(), ex);
				ExceptionPoint.getInstance().fireException(this, ex);
			}
		}
	}
}
