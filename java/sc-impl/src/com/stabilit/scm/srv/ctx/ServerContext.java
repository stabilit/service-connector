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
package com.stabilit.scm.srv.ctx;

import com.stabilit.scm.ctx.ContextAdapter;
import com.stabilit.scm.srv.registry.ServerRegistry;
import com.stabilit.scm.srv.server.IServer;


/**
 * The Class ServerContext.
 */
public class ServerContext extends ContextAdapter implements IServerContext {
		
	/** The server. */
	private IServer server;
	
	/**
	 * Instantiates a new server context.
	 * 
	 * @param server the server
	 */
	public ServerContext(IServer server) {
		this.server = server;
	}

	/** {@inheritDoc} */
	@Override
	public IServer getServer() {
		return server;
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static IServerContext getCurrentInstance() {
		ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
		return (IServerContext) serverRegistry.getCurrentContext();
	}
}
