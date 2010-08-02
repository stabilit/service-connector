/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.unit;

import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequesterContext;
import com.stabilit.scm.common.scmp.SCMPMessageId;

/**
 * @author JTraber
 */
public class TestContext implements IRequesterContext {

	protected IConnectionPool connectionPool;
	private SCMPMessageId msgId;

	public TestContext(ICommunicatorConfig config, SCMPMessageId msgId) {
		this.connectionPool = new ConnectionPool(config.getHost(), config.getPort(), config.getConnectionType());
		this.connectionPool.setMinConnections(1);
		this.msgId = msgId;
	}

	public TestContext(String host, int port, String conType) {
		this.connectionPool = new ConnectionPool(host, port, conType);
	}

	@Override
	public IConnectionPool getConnectionPool() {
		return this.connectionPool;
	}

	@Override
	public SCMPMessageId getSCMPMessageId() {
		return this.msgId;
	}
}
