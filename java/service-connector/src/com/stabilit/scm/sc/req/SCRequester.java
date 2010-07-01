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
import com.stabilit.scm.common.listener.PerformancePoint;
import com.stabilit.scm.common.net.req.IConnection;
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
	private IContext context;

	public SCRequester(IContext context) {
		this.context = context;
	}

	@Override
	public SCMPMessage sendAndReceive(SCMPMessage scmp) throws Exception {
		// return an already connected live instance
		IConnection connection = this.context.getConnectionPool().getConnection();
		try {
			PerformancePoint.getInstance().fireBegin(this, "sendAndReceive");
			return connection.sendAndReceive(scmp);
		} finally {
			PerformancePoint.getInstance().fireEnd(this, "sendAndReceive");
			this.context.getConnectionPool().freeConnection(connection);// give back to pool
		}
	}

	@Override
	public void send(SCMPMessage message, ISCMPCallback callback) throws Exception {
		// return an already connected live instance
		IConnection connection = this.context.getConnectionPool().getConnection();
		try {
			PerformancePoint.getInstance().fireBegin(this, "send");
			connection.send(message, callback);
		} finally {
			PerformancePoint.getInstance().fireEnd(this, "send");
			this.context.getConnectionPool().freeConnection(connection);// give back to pool
		}
	}
	
	@Override
	public synchronized String toHashCodeString() {
		return " [" + this.hashCode() + "]";
	}
}
