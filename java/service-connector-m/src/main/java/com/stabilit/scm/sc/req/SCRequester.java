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

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.PerformancePoint;
import com.stabilit.scm.common.net.req.IConnection;
import com.stabilit.scm.common.net.req.IConnectionContext;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPMessage;


/**
 * The Class SCRequester. Defines behavior of requester in the context of Service Connector.
 * 
 * @author JTraber
 */
public class SCRequester implements IRequester {

	/** The context. */
	private IContext outerContext;

	public SCRequester(IContext context) {
		this.outerContext = context;
	}

	@Override
	public SCMPMessage sendAndReceive(SCMPMessage scmp) throws Exception {
		// return an already connected live instance
		IConnection connection = this.outerContext.getConnectionPool().getConnection();
		IConnectionContext connectionContext = connection.getContext();
		connectionContext.setOuterContext(this.outerContext);
		try {
			PerformancePoint.getInstance().fireBegin(this, "sendAndReceive");
			return connection.sendAndReceive(scmp);
		} finally {
			PerformancePoint.getInstance().fireEnd(this, "sendAndReceive");
			connectionContext.getConnectionPool().freeConnection(connection);
			connectionContext.setOuterContext(null);
		}
	}

	@Override
	public void send(SCMPMessage message, ISCMPCallback callback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.outerContext.getConnectionPool().getConnection();
		IConnectionContext connectionContext = connection.getContext();
		connectionContext.setOuterContext(this.outerContext);
		ISCMPCallback requesterCallback = new SCRequesterSCMPCallback(callback);
		requesterCallback.setContext(connectionContext);
		try {
			connection.send(message, requesterCallback);
		} finally {
			// don't free it here, free them after call message received,
			// this.outerContext.getConnectionPool().freeConnection(connection);//
			// give back to pool
		}
	}

	@Override
	public synchronized String toHashCodeString() {
		return " [" + this.hashCode() + "]";
	}

	// member class
	private class SCRequesterSCMPCallback implements ISCMPCallback {
		private ISCMPCallback scmpCallback;

		public SCRequesterSCMPCallback(ISCMPCallback scmpCallback) {
			this.scmpCallback = scmpCallback;
		}

		@Override
		public void callback(SCMPMessage scmpReply) throws Exception {
			this.scmpCallback.callback(scmpReply);
			freeConnection();
		}

		@Override
		public void callback(Throwable th) {
			this.scmpCallback.callback(th);
			freeConnection();
		}

		@Override
		public IContext getContext() {
			return scmpCallback.getContext();
		}

		@Override
		public void setContext(IContext context) {
			this.scmpCallback.setContext(context);
		}

		private void freeConnection() {
			try {
				IConnectionContext connectionContext = (IConnectionContext) this.scmpCallback.getContext();
				connectionContext.getConnectionPool().freeConnection(connectionContext.getConnection());
				connectionContext.setOuterContext(null);
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}
}
