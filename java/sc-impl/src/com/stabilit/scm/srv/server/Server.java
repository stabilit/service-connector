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
package com.stabilit.scm.srv.server;

import com.stabilit.scm.srv.config.IServerConfigItem;
import com.stabilit.scm.srv.ctx.IServerContext;
import com.stabilit.scm.srv.ctx.ServerContext;
import com.stabilit.scm.srv.server.factory.ServerEnpointFactory;

/**
 * The Class Server. Abstracts server functionality from a application view. It is not the technical representation
 * of a server connection.
 * 
 * @author JTraber
 */
public abstract class Server implements IServer {

	/** The server configuration. */
	private IServerConfigItem serverConfig;
	/** The server connection. */
	private IEndpoint serverConnection;
	/** The server context. */
	protected IServerContext serverContext;

	/** {@inheritDoc} */
	@Override
	public void setServerConfig(IServerConfigItem serverConfig) {
		this.serverConfig = serverConfig;
		this.serverContext = new ServerContext(this);
		ServerEnpointFactory serverConnectionFactory = new ServerEnpointFactory();
		this.serverConnection = serverConnectionFactory.newInstance(this.serverConfig.getConnection());
		this.serverConnection.setServer(this);
		this.serverConnection.setHost(this.serverConfig.getHost());
		this.serverConnection.setPort(this.serverConfig.getPort());
		this.serverConnection.setNumberOfThreads(this.serverConfig.getNumberOfThreads());
	}

	/** {@inheritDoc} */
	@Override
	public void create() throws Exception {
		serverConnection.create();
	}

	/** {@inheritDoc} */
	@Override
	public void runAsync() throws Exception {
		serverConnection.runAsync();
	}

	/** {@inheritDoc} */
	@Override
	public void runSync() throws Exception {
		serverConnection.runSync();
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		serverConnection.destroy();
	}

	/** {@inheritDoc} */
	@Override
	public IServerContext getServerContext() {
		return serverContext;
	}

	/** {@inheritDoc} */
	@Override
	public IServerConfigItem getServerConfig() {
		return serverConfig;
	}
}
