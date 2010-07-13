/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.sc.service;

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;

/**
 * @author JTraber
 */
public class ServerContext implements IContext {

	private IConnectionPool connectionPool;

	/**
	 * @param host
	 * @param portNr
	 * @param connectionKey
	 */
	public ServerContext(String host, int portNr, String connectionKey) {
		this.connectionPool = new ConnectionPool(host, portNr, connectionKey);
	}

	@Override
	public IConnectionPool getConnectionPool() {
		return this.connectionPool;
	}
}
