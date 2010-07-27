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

import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.req.IConnection;
import com.stabilit.scm.common.net.req.IConnectionContext;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.IRequesterContext;
import com.stabilit.scm.common.net.req.netty.OperationTimeoutException;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class SCRequester. Defines behavior of requester in the context of Service Connector.
 * 
 * @author JTraber
 */
public class SCRequester implements IRequester {

	/** The context. */
	private IRequesterContext reqContext;

	public SCRequester(IRequesterContext context) {
		this.reqContext = context;
	}

	@Override
	public void send(SCMPMessage message, ISCMPCallback callback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.reqContext.getConnectionPool().getConnection();
		IConnectionContext connectionContext = connection.getContext();
		ISCMPCallback requesterCallback = new SCRequesterSCMPCallback(callback, connectionContext);
		connection.send(message, requesterCallback);
	}

	@Override
	public synchronized String toHashCodeString() {
		return " [" + this.hashCode() + "]";
	}

	private class SCRequesterSCMPCallback implements ISCMPCallback {
		private ISCMPCallback scmpCallback;
		private IConnectionContext connectionCtx;

		public SCRequesterSCMPCallback(ISCMPCallback scmpCallback, IConnectionContext connectionCtx) {
			this.scmpCallback = scmpCallback;
			this.connectionCtx = connectionCtx;
		}

		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			this.scmpCallback.callback(scmpReply);
			this.freeConnection();
		}

		@Override
		public void callback(Throwable th) {
			this.scmpCallback.callback(th);
			if (th instanceof OperationTimeoutException) {
				// operation timed out - delete this specific connection, prevents race conditions
				this.disconnectConnection();
			} else {
				// another exception occurred - just free the connection
				this.freeConnection();
			}
		}

		private void freeConnection() {
			try {
				SCRequester.this.reqContext.getConnectionPool().freeConnection(connectionCtx.getConnection());
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}

		private void disconnectConnection() {
			try {
				SCRequester.this.reqContext.getConnectionPool().forceClosingConnection(connectionCtx.getConnection());
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}
}
