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
package com.stabilit.sc.srv.server;

import com.stabilit.sc.srv.config.IServerConfigItem;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.ctx.ServerContext;
import com.stabilit.sc.srv.server.factory.ServerConnectionFactory;

/**
 * The Class Server. Abstracts server functionality from a application view. It is not the technical
 * representation of a server connection.
 * 
 * @author JTraber
 */
public abstract class Server implements IServer {

	/** The server configuration. */
	private IServerConfigItem serverConfig;
	/** The server connection. */
	private IServerConnection serverConnection;
	/** The server context. */
	protected IServerContext serverContext;

	/**
	 * Sets the server configuration.
	 * 
	 * @param serverConfig
	 *            the new server configuration
	 */
	@Override
	public void setServerConfig(IServerConfigItem serverConfig) {
		this.serverConfig = serverConfig;
		this.serverContext = new ServerContext(this);
		ServerConnectionFactory serverConnectionFactory = new ServerConnectionFactory();
		this.serverConnection = serverConnectionFactory.newInstance(this.serverConfig.getCon());
		this.serverConnection.setServer(this);
		this.serverConnection.setHost(this.serverConfig.getHost());
		this.serverConnection.setPort(this.serverConfig.getPort());
	}

	/**
	 * Creates the server.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void create() throws Exception {
		serverConnection.create();
	}

	/**
	 * Run asynchronously. Starts server in a separate thread.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void runAsync() throws Exception {
		serverConnection.runAsync();
	}

	/**
	 * Run synchronously. Starts server with incoming thread. Thread never returns.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void runSync() throws Exception {
		serverConnection.runSync();
	}

	/**
	 * Gets the server context.
	 * 
	 * @return the server context
	 */
	@Override
	public IServerContext getServerContext() {
		return serverContext;
	}

	/**
	 * Gets the server config.
	 * 
	 * @return the server config
	 */
	@Override
	public IServerConfigItem getServerConfig() {
		return serverConfig;
	}
}
