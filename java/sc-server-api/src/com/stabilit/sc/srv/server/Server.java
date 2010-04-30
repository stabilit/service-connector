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

import com.stabilit.sc.srv.conf.ServerConfig.ServerConfigItem;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.ctx.ServerContext;
import com.stabilit.sc.srv.server.factory.ServerConnectionFactory;

/**
 * @author JTraber
 * 
 */
public abstract class Server implements IServer {

	private ServerConfigItem serverConfig;
	private IServerConnection serverConnection;
	protected IServerContext serverContext;	

	@Override
	public void setServerConfig(ServerConfigItem serverConfig) {
		this.serverConfig = serverConfig;
		this.serverContext = new ServerContext(this);
		ServerConnectionFactory serverConnectionFactory = new ServerConnectionFactory();
		this.serverConnection = serverConnectionFactory.newInstance(this.serverConfig.getCon());
		this.serverConnection.setServer(this);
		this.serverConnection.setHost(this.serverConfig.getHost());
		this.serverConnection.setPort(this.serverConfig.getPort());
	}

	@Override
	public void create() throws Exception {		
		serverConnection.create();
	}

	@Override
	public void runAsync() throws Exception {
		serverConnection.runAsync();
	}
	
	@Override
	public Thread runAsyncForTest() {
		return serverConnection.runAsyncForTest();
	}

	@Override
	public void runSync() throws Exception {
		serverConnection.runSync();
	}

	@Override
	public IServerContext getServerContext() {
		return serverContext;
	}

	@Override
	public ServerConfigItem getServerConfig() {
		return serverConfig;
	}
}
